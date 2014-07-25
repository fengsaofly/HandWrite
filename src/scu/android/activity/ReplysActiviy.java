package scu.android.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import scu.android.application.MyApplication;
import scu.android.base.CommonEditor;
import scu.android.dao.Question;
import scu.android.dao.Reply;
import scu.android.dao.Resource;
import scu.android.dao.User;
import scu.android.db.DBTools;
import scu.android.ui.MAudioView;
import scu.android.ui.MListView;
import scu.android.util.ActivitySupport;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 问题回复列表
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class ReplysActiviy extends ActivitySupport implements OnRefreshListener<ListView> {
	private final String TAG = getClass().getName();

	private PullToRefreshListView refreshView;// 下拉刷新组件
	private View noNetworkConnect;
	private Question question;
	private boolean isRefreshing;// 刷新中
	private boolean isDownloading;// 下载中
	private ReplysAdapter replysAdapter;
	private ArrayList<Reply> replys;
	private CommonEditor editor;
	private long curRepId;
	private MListViewAdapter curRepAdapter;
	private BroadcastReceiver receiver;
	private Context context;

	private ImageLoader loader;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_replys);
		init();
	}

	public void init() {
		getActionBar().setTitle("问题回复");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		context = getApplicationContext();
		refreshView = (PullToRefreshListView) findViewById(R.id.replys);
		noNetworkConnect = findViewById(R.id.no_network_connect);
		refreshView.setOnRefreshListener(this);
		refreshView.setMode(Mode.BOTH);
		ListView replysView = refreshView.getRefreshableView();
		replysView.setEmptyView(findViewById(R.id.empty_view));
		replys = new ArrayList<Reply>();
		replysAdapter = new ReplysAdapter(this, replys);
		replysView.setAdapter(replysAdapter);
		replysView.setOnItemLongClickListener(new LongClickListener());
		editor = (CommonEditor) findViewById(R.id.reply_editor);
		editor.setAction(Constants.REPLYS);
		editor.setActivity(this);
		editor.disableAddExtras();

		receiver = new ReplysBroadcastRecevier();
		registerReceiver(receiver, new IntentFilter(Constants.REPLYS));
		initImageLoader();
	}

	public void initImageLoader() {
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 上传或下载之后更新问题回复列表
	 */
	private class ReplysBroadcastRecevier extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(Constants.REPLYS)) {
				final String action = intent.getStringExtra("action");
				if (action != null) {
					if (action.equals("upload") && MyApplication.isUploading) {
						final String result = intent.getStringExtra("result");
						if (result.equals("success")) {
							showToast("上传成功");
							long qr_id = intent.getLongExtra("qr_id", 0);
							long created_time = intent.getLongExtra("created_time", 0);
							long qr_resource = intent.getLongExtra("qr_resource", 0);
							updateReply(qr_id, created_time, qr_resource);
						} else if (result.equals("failed")) {
							showToast("上传失败");
							updateReply(0, 0, 0);
						}
						MyApplication.isUploading = false;
					} else if (action.equals("download") && isDownloading) {
						@SuppressWarnings("unchecked")
						ArrayList<Reply> mReplys = (ArrayList<Reply>) intent.getSerializableExtra("replys");
						if (mReplys != null && mReplys.size() > 0) {
							final Reply firstReply = mReplys.get(0);
							if (replys.size() > 0 && firstReply.getCreated_time() > replys.get(0).getCreated_time()) {
								replys.addAll(0, mReplys);
							} else {
								replys.addAll(mReplys);
							}
							showToast("下载完毕");
						} else {
							if (replys.size() == 0) {
							} else {
								showToast("已获取最新数据");
							}
						}
						replysAdapter.notifyDataSetChanged();
						isDownloading = false;
					}
				} else {
					doReply();
				}
				replysAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 获取数据，查看更多
	 */
	private class GetDataTask extends AsyncTask<Integer, Void, List<Reply>> {

		@Override
		protected List<Reply> doInBackground(Integer... params) {
			isRefreshing = true;
			int action = params[0];
			final boolean isNetworkConnected = AppUtils.isNetworkConnect(ReplysActiviy.this);
			final DBTools mDBTools = DBTools.getInstance(context);
			List<Reply> mReplys = new ArrayList<Reply>();
			final int size = replys.size();
			if (size > 0) {// 获取更多数据
				long floorTime = replys.get(0).getCreated_time();
				long topTime = replys.get(size - 1).getCreated_time();
				switch (action) {
				case Constants.DOWN:// 下拉
					mReplys = mDBTools.loadReplys(1, (short) 0, floorTime);
					if (mReplys.size() == 0) {
						if (isNetworkConnected) {
							isDownloading = true;
							LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
							mParams.add(new BasicNameValuePair("start", "0"));
							mParams.add(new BasicNameValuePair("floorTime", String.valueOf(floorTime)));
							MyApplication.downloadReplys(context, question.getQ_id(), mParams);
						}
					}
					break;
				case Constants.UP:// 上拉
					mReplys = mDBTools.loadReplys(question.getQ_id(), (short) 0, topTime, 5);// 本地数据
					if (mReplys.size() == 0) {
						if (isNetworkConnected) {
							isDownloading = true;
							LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
							mParams.add(new BasicNameValuePair("start", "0"));
							mParams.add(new BasicNameValuePair("topTime", String.valueOf(topTime)));
							MyApplication.downloadReplys(context, question.getQ_id(), mParams);
						}
					}
					break;
				}
			} else {// 首次加载数据
				mReplys = mDBTools.loadReplys(question.getQ_id(), (short) 0, 5);
				if (mReplys.size() == 0) {
					if (isNetworkConnected) {
						isDownloading = true;
						LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
						mParams.add(new BasicNameValuePair("start", "0"));
						MyApplication.downloadReplys(context, question.getQ_id(), mParams);
					}
				}
			}
			return mReplys;
		}

		@Override
		protected void onPostExecute(List<Reply> result) {
			if (AppUtils.isNetworkConnect(ReplysActiviy.this)) {
				noNetworkConnect.setVisibility(View.GONE);
			} else {
				noNetworkConnect.setVisibility(View.VISIBLE);
			}
			if (result.size() > 0) {
				final Reply firstReply = result.get(0);
				if (replys.size() > 0 && firstReply.getCreated_time() > replys.get(0).getCreated_time()) {
					replys.addAll(0, result);
				} else {
					replys.addAll(result);
				}
			} else {
				if (!isDownloading)
					showToast("没有加载到数据");
			}
			replysAdapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();
			isRefreshing = false;
		}
	}

	private class ReplyHolder {
		ImageView avatar;
		TextView distance;
		TextView nickname;
		ImageView reply;
		TextView publishTime;
		TextView postAlterInfo;
		TextView desc;
		ImageView img;
		MAudioView audio;
		MListView replys;
	}

	private class ReplysAdapter extends BaseAdapter {
		Activity activity;
		ArrayList<Reply> replys;

		public ReplysAdapter(Activity activity, ArrayList<Reply> replys) {
			this.activity = activity;
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
			ReplyHolder holder = null;
			// if (convertView == null) {
			convertView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.question_reply_item, null);
			holder = new ReplyHolder();
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar_val);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder.reply = (ImageView) convertView.findViewById(R.id.reply);
			holder.publishTime = (TextView) convertView.findViewById(R.id.publish_time);
			holder.postAlterInfo = (TextView) convertView.findViewById(R.id.post_alert_info);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.audio = (MAudioView) convertView.findViewById(R.id.audio);
			holder.replys = (MListView) convertView.findViewById(R.id.replys);
			convertView.setTag(holder);
			// }else{
			// holder=(ReplyHolder) convertView.getTag();
			// }
			final Reply reply = (Reply) getItem(position);
			User user = MyApplication.getCurrentUser(activity);
			loader.displayImage(user.getUser_avatar(), holder.avatar, options);
			holder.distance.setText("附近");
			holder.nickname.setText(user.getUser_nickname());

			holder.publishTime.setText(AppUtils.timeToNow(reply.getCreated_time()));
			holder.postAlterInfo.setText("");
			if (reply.getQr_text() == null || reply.getQr_text().length() == 0)
				holder.desc.setVisibility(View.GONE);
			else
				holder.desc.setText(reply.getQr_text());
			DBTools mDBTools = DBTools.getInstance(context);
			List<Resource> mResources = mDBTools.loadResources(reply.getQr_resource());
			// List<String> thumbnails = DBTools.getSImages(mResources);
			// List<String> images = DBTools.getLImages(mResources);
			// if ((thumbnails != null && thumbnails.size() > 0)) {
			// holder.photosView.setAdapter(new
			// PhotosAdapter(ReplysActiviy.this,thumbnails, images));
			// holder.photosView.setVisibility(View.VISIBLE);
			// } else {
			// holder.photosView.setVisibility(View.GONE);
			// }
			String thunmbnail = DBTools.getSImage(mResources);
			String img = DBTools.getLImage(mResources);
			if (thunmbnail != null) {
				AppUtils.disImg(context, loader, options, holder.img, thunmbnail, img);
			} else {
				holder.img.setVisibility(View.GONE);
			}
			String sAudio = DBTools.getAudio(mResources);
			if (sAudio != null && sAudio.length() > 0) {
				holder.audio.setAudioUrl(sAudio);
				holder.audio.setVisibility(View.VISIBLE);
			} else {
				holder.audio.setVisibility(View.GONE);
			}
			List<Reply> mReplysList = mDBTools.loadReplys(reply.getQr_id(), (short) 1, 5);
			final MListViewAdapter adapter = new MListViewAdapter(ReplysActiviy.this, user.getUser_nickname(), mReplysList);
			if (mReplysList == null || mReplysList.size() == 0) {
				holder.replys.setVisibility(View.GONE);
			} else {
				holder.replys.setAdapter(adapter);
			}
			holder.reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					toggleEditor();
					curRepId = reply.getQr_id();
					curRepAdapter = adapter;
				}
			});
			return convertView;
		}
	}

	private class MListViewHolder {
		TextView nickname;
		TextView createTime;
		TextView content;
	}

	private class MListViewAdapter extends BaseAdapter {

		private Activity activity;
		private String nickname;
		private List<Reply> mReplys;

		public MListViewAdapter(Activity activity, String nickname, List<Reply> mReplys) {
			this.activity = activity;
			this.nickname = nickname;
			this.mReplys = mReplys;
		}

		@Override
		public int getCount() {
			return mReplys.size();
		}

		@Override
		public Object getItem(int position) {
			return mReplys.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MListViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.secondary_reply_item, null);
				holder = new MListViewHolder();
				holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
				holder.createTime = (TextView) convertView.findViewById(R.id.reply_time);
				holder.content = (TextView) convertView.findViewById(R.id.content);
				convertView.setTag(holder);
			} else {
				holder = (MListViewHolder) convertView.getTag();
			}
			final Reply reply = (Reply) getItem(position);
			holder.nickname.setText(MyApplication.getCurrentUser(activity).getUser_nickname() + "回复：" + nickname);
			holder.createTime.setText(AppUtils.timeToNow(reply.getCreated_time()));
			holder.content.setText(reply.getQr_text());
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					toggleEditor();
					curRepId = reply.getQr_q();
					curRepAdapter = MListViewAdapter.this;
				}
			});
			return convertView;
		}
	}

	private class LongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			Reply reply = replys.get(position - 1);
			deleteReply(reply, position - 1);
			return true;
		}
	};

	public void toggleEditor() {
		if (editor.getVisibility() == View.GONE) {
			editor.setVisibility(View.VISIBLE);
			editor.focus();
		} else {
			editor.setVisibility(View.GONE);
		}
		editor.hideSoft();
	}

	// 回复评论
	public void doReply() {
		editor.hideSoft();
		String qr_text = editor.getContent();
		if ((qr_text != null && qr_text.length() > 0)) {
			final long qr_user = MyApplication.getCurrentUser(context).getUser_id();
			Reply reply = new Reply(null, null, qr_text, System.currentTimeMillis() / 1000, (short) 1, qr_user, curRepId, -1l);
			long rId = DBTools.getInstance(context).insertReply(reply);
			if (rId != 0L) {
				editor.getThumbnailsParentView().setVisibility(View.INVISIBLE);
				editor.getExtrasView().setVisibility(View.GONE);
				editor.getThumbnails().clear();
				editor.clearContent();
				editor.setCurPhotosNum(0);
				reply.setQr_id(rId);
				if (curRepAdapter.mReplys == null) {
					curRepAdapter.mReplys = new ArrayList<Reply>();
				}
				curRepAdapter.mReplys.add(0, reply);
				curRepAdapter.notifyDataSetChanged();
				toggleEditor();
				MyApplication.oldId = rId;
				MyApplication.uploadReply(context, reply);// 上传回复
			} else {
				Toast.makeText(this, "回复失败。。。", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "没有发现回复内容。。。", Toast.LENGTH_SHORT).show();
		}
	}

	// 删除问题
	public void deleteReply(final Reply reply, final int location) {
		Dialog alert = new AlertDialog.Builder(this).setTitle("破题").setMessage("确定删除回复?").setPositiveButton("确定", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DBTools.getInstance(context).deleteReply(reply);
				replys.remove(location);
				replysAdapter.notifyDataSetChanged();
				if (replys.size() == 0) {
					loadData(Constants.DOWN);
				}
			}
		}).setNegativeButton("取消", new Dialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create();
		alert.show();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (editor.getVisibility() == View.VISIBLE) {
			toggleEditor();
		} else {
			super.onBackPressed();
		}
	}

	public void loadData(int action) {
		if (!isRefreshing && !isDownloading) {
			new GetDataTask().execute(action);
		}
	}

	public void updateReply(long qr_id, long created_time, long qr_resource) {
		if (qr_id != 0) {
			for (Reply mReply : replys) {
				if (mReply.getQr_id() == MyApplication.oldId) {
					mReply.setQr_id(qr_id);
					mReply.setCreated_time(created_time);
					mReply.setQr_resource(qr_resource);
					break;
				}
			}
		} else {
			// for (Reply mReply : replys) {
			// if (mReply.getQr_id() == MyApplication.oldId) {
			// mReply.setQr_type((short) 0);
			// break;
			// }
			// }
		}
		replysAdapter.notifyDataSetChanged();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.isHeadherShow()) {
			String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			if (!MyApplication.isUploading) {
				loadData(Constants.DOWN);
			} else {
				showToast("玩命上传中,稍后刷新...");
				this.refreshView.onRefreshComplete();
			}
		} else {
			loadData(Constants.UP);
		}
	}

	@Override
	protected void onResume() {
		question = (Question) getIntent().getSerializableExtra("question");
		loadData(Constants.DOWN);
		super.onResume();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return false;
	}

}
