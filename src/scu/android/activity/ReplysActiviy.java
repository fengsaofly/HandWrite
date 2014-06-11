package scu.android.activity;

import java.util.ArrayList;

import scu.android.db.ReplyDao;
import scu.android.entity.Reply;
import scu.android.ui.MGridView;
import scu.android.ui.PhotosAdapter;
import scu.android.util.AppUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

public class ReplysActiviy extends Activity {

	private ListView replysView;
	private ReplysAdapter replysAdapter;
	private ArrayList<Reply> replys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_replys);
		init();
	}

	@SuppressWarnings("unchecked")
	public void init() {
		replys = (ArrayList<Reply>) getIntent().getSerializableExtra("replys");

		replysView = (ListView) findViewById(R.id.replys);
		replysAdapter = new ReplysAdapter(this, replys);
		replysView.setAdapter(replysAdapter);
		replysView.setOnItemLongClickListener(new LongClickListener());
	}

	private class ReplysAdapter extends BaseAdapter {
		Activity activity;
		ArrayList<Reply> replys;
		int width;

		public ReplysAdapter(Activity activity, ArrayList<Reply> replys) {
			this.activity = activity;
			this.replys = replys;
			this.width = AppUtils.getDefaultPhotoWidth(activity) / 3;
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
				convertView = LayoutInflater.from(
						activity.getApplicationContext()).inflate(
						R.layout.reply_item, null);
			}
			final Reply reply = replys.get(position);
			ImageView avatar = (ImageView) convertView
					.findViewById(R.id.avatar);
			AppUtils.setViewSize(avatar, width, width);
			avatar.setBackgroundResource(R.drawable.avatar);
			((TextView) convertView.findViewById(R.id.nickname))
					.setText("测试用户名");
			((TextView) convertView.findViewById(R.id.reply_time))
					.setText(AppUtils.timeToNow(reply.getReplyTime()));
			((TextView) convertView.findViewById(R.id.content)).setText(reply
					.getContent());
			MGridView photosView = (MGridView) convertView
					.findViewById(R.id.photos_view);
			photosView.setAdapter(new PhotosAdapter(ReplysActiviy.this, reply
					.getImages()));
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
							if (replys.size() == 0)
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
