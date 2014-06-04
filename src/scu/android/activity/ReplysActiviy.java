package scu.android.activity;

import java.util.ArrayList;

import scu.android.db.ReplyDao;
import scu.android.entity.Reply;
import scu.android.ui.MGridView;
import scu.android.util.AppUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ReplysActiviy extends Activity {

	private ListView replysView;
	private ReplysAdapter replysAdapter;
	private ArrayList<Reply> replys;

	private ImageLoader loader;
	private DisplayImageOptions options;
	private int photoWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_replys);
		init();
	}

	@SuppressWarnings("unchecked")
	public void init() {
		photoWidth = (AppUtils.getWindowMetrics(this).widthPixels - 4) / 3;
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.question)
				.showImageForEmptyUri(R.drawable.question).cacheInMemory()
				.cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.build();

		replys = (ArrayList<Reply>) getIntent().getSerializableExtra("replys");

		replysView = (ListView) findViewById(R.id.replys);
		replysAdapter = new ReplysAdapter(this, replys);
		replysView.setAdapter(replysAdapter);
		replysView.setOnItemLongClickListener(new LongClickListener());
	}

	private class ReplysAdapter extends BaseAdapter {
		Context context;
		ArrayList<Reply> replys;

		public ReplysAdapter(Context context, ArrayList<Reply> replys) {
			this.context = context;
			this.replys = replys;
		}

		@Override
		public int getCount() {
			return replys.size();
		}

		@Override
		public Object getItem(int position) {
			return replys.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.reply_item, null);
			}
			final Reply reply = replys.get(position);
			ImageView avatar = (ImageView) convertView
					.findViewById(R.id.avatar);
			LayoutParams params = avatar.getLayoutParams();
			params.width = params.height = photoWidth / 3;
			avatar.setLayoutParams(params);
			avatar.setBackgroundResource(R.drawable.avatar);
			((TextView) convertView.findViewById(R.id.nickname))
					.setText("测试用户名");
			((TextView) convertView.findViewById(R.id.reply_time))
					.setText(AppUtils.timeToNow(reply.getReplyTime()));
			((TextView) convertView.findViewById(R.id.content)).setText(reply
					.getContent());
			MGridView photosView = (MGridView) convertView
					.findViewById(R.id.photos_view);
			photosView
					.setAdapter(new PhotosAdapter(context, reply.getImages()));
			ImageButton audio = (ImageButton) convertView
					.findViewById(R.id.audio);
			final String sAudio = reply.getAudio();
			if (sAudio != null && sAudio.length() > 0) {
				audio.setVisibility(View.VISIBLE);
				audio.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
			}
			return convertView;
		}
	}

	private class LongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Reply reply = replys.get(position);
			deleteReply(reply.getRepId(), position);
			Log.i("info", "longClick");
			return true;
		}
	};

	/*
	 * 图片
	 */
	private class PhotosAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<String> bitmaps;

		public PhotosAdapter(Context context, ArrayList<String> bitmaps) {
			this.context = context;
			this.bitmaps = bitmaps;
		}

		public int getCount() {
			return bitmaps.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.thumbnail_item, null);
			}
			String photo = bitmaps.get(position);
			ImageView thumbnail = ((ImageView) convertView
					.findViewById(R.id.thumbnail));
			android.view.ViewGroup.LayoutParams params = thumbnail
					.getLayoutParams();
			// if (bitmaps.size() == 1) {
			// params.width = getWindowMetrics().widthPixels - 10;
			// params.height = 2 * photoWidth;
			// } else {
			params.width = params.height = photoWidth;
			// }
			thumbnail.setLayoutParams(params);// 设置图片大小
			loader.displayImage("file:///" + photo, thumbnail, options);
			final int index = position;
			thumbnail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ReplysActiviy.this,
							ScanPhotosActivity.class);
					intent.putStringArrayListExtra("photos", bitmaps);
					intent.putExtra("index", index + 1);
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	// 删除问题
	public void deleteReply(final long repId, final int location) {
		Dialog alert = new AlertDialog.Builder(this).setTitle("破题")
				.setMessage("确定删除回复?")
				.setPositiveButton("确定", new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!ReplyDao.deleteReply(ReplysActiviy.this, repId)) {
							Toast.makeText(ReplysActiviy.this, "删除失败。。.",
									Toast.LENGTH_SHORT).show();
						} else {
							replys.remove(location);
							replysAdapter.notifyDataSetChanged();
							if(replys.size()==0)
								ReplysActiviy.this.finish();
						}
					}
				}).setNegativeButton("取消", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		alert.show();
	}
}
