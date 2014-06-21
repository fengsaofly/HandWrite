package scu.android.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import scu.android.application.MyApplication;
import scu.android.base.CommonEditor;
import scu.android.db.ReplyDao;
import scu.android.db.UserDao;
import scu.android.entity.Reply;
import scu.android.entity.User;
import scu.android.ui.MGridView;
import scu.android.ui.MListView;
import scu.android.ui.PhotosAdapter;
import scu.android.util.ActivitySupport;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 问题回复列表
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class ReplysActiviy extends ActivitySupport implements
		OnLastItemVisibleListener, OnRefreshListener<ListView> {

	private PullToRefreshListView refreshView;// 下拉刷新组件
	private View noNetworkConnect;
	private ProgressDialog progressDialog;
	private Button disMore;
	private long quesId;
	private boolean isRefreshing;
	private boolean isAllDownload;
	private ReplysAdapter replysAdapter;
	private ArrayList<Reply> replys;
	private HashMap<Reply, ArrayList<Reply>> maps;

	private CommonEditor editor;
	private long curRepId;
	private MListViewAdapter curRepAdapter;
	private DoReplyReceiver receiver;
	private String action = "scu.android.activity.ReplysActivity";
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
		context = getApplicationContext();
		refreshView = (PullToRefreshListView) findViewById(R.id.replys);
		noNetworkConnect = findViewById(R.id.no_network_connect);
		disMore = (Button) findViewById(R.id.dis_more);
		progressDialog = getProgressDialog();

		refreshView.setOnRefreshListener(this);
		refreshView.setOnLastItemVisibleListener(this);
		ListView replysView = refreshView.getRefreshableView();
		replys = new ArrayList<Reply>();
		quesId = getIntent().getLongExtra("quesId", 0);
		isAllDownload = false;
		refreshData(0l);

		replysAdapter = new ReplysAdapter(this, replys);
		replysView.setAdapter(replysAdapter);
		replysView.setOnItemLongClickListener(new LongClickListener());

		maps = new HashMap<Reply, ArrayList<Reply>>();

		editor = (CommonEditor) findViewById(R.id.reply_editor);
		editor.setAction(action);
		editor.setActivity(this);
		editor.disableAddExtras();
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(5))
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageForEmptyUri(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		receiver = new DoReplyReceiver();
		registerReceiver(receiver, new IntentFilter(action));
	}

	/*
	 * 获取图片删除信息
	 */
	private class DoReplyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(action)) {
				doReply();
			}
		}
	}

	private class GetDataTask extends AsyncTask<Long, Void, ArrayList<Reply>> {
		int resultCode = -1;// 错误类型

		/**
		 * params[0],数据来源:0,net;1,local
		 * parems[1],目的:0,首次获取数据;1,(获取当前列表表首问题之后的数据);2:加载更多(获取当前列表表尾问题之前的数据);
		 */
		@Override
		protected ArrayList<Reply> doInBackground(Long... params) {
			isRefreshing = true;
			long srcType = params[0];
			long object = params[1];

			ArrayList<Reply> mLocalReplys = null;
			switch ((int) srcType) {
			case Constants.SRC_NET:
				long localReplyNum = ReplyDao.getReplyNum(context, quesId);
				long localLens = 10L;
				switch ((int) object) {
				case Constants.DATA_INIT:
					if (localReplyNum > 0) {
						if (localReplyNum <= localLens) {
							localLens = localReplyNum;
							isAllDownload = true;
						}
						mLocalReplys = ReplyDao.getReply(context, quesId,
								(long) replys.size(), localLens, 0);
					} else {
						resultCode = -1;
					}
					break;
				case Constants.DATA_AFTER:
					resultCode = -1;
					break;

				}
				break;
			case Constants.SRC_LOCAL:
				localReplyNum = ReplyDao.getReplyNum(context, quesId);
				localLens = 10L;
				switch ((int) object) {
				case Constants.DATA_INIT:
					if (localReplyNum > 0) {
						if (localReplyNum <= localLens) {
							localLens = localReplyNum;
							isAllDownload = true;
						}
						mLocalReplys = ReplyDao.getReply(context, quesId,
								(long) replys.size(), localLens, 0);
					} else {
						resultCode = -1;
					}
					break;
				case Constants.DATA_AFTER:
					resultCode = -1;
					break;
				case Constants.DATA_BEFORE:
					long lave = localReplyNum - (replys.size() + localLens);
					if (lave <= 0) {
						localLens = localReplyNum - replys.size();
						isAllDownload = true;
					}
					mLocalReplys = ReplyDao.getReply(context, quesId,
							(long) replys.size(), localReplyNum, 0);
				}
				break;
			}
			return mLocalReplys;
		}

		@Override
		protected void onPostExecute(ArrayList<Reply> result) {
			progressDialog.dismiss();
			if (result != null) {
				for (Reply reply : result) {
					ArrayList<Reply> replysString = ReplyDao.getReply(context,
							reply.getRepId(), 0, 5, 1);
//					reply.setReplys(replysString);
					replys.add(reply);
					maps.put(reply, replysString);
				}
				onLastItemVisible();
			} else {
				switch (resultCode) {
				case -1:
					showToast("暂无数据...", Toast.LENGTH_SHORT);
					break;
				case Constants.ERR_LOCAL_NO_DATA:
					showToast("本地无数据,打开网络连接加载数据...", Toast.LENGTH_SHORT);
					break;
				case Constants.ERR_LOCAL_NO_NEW_DATA:
					showToast("请打开网络连接,加载新数据...", Toast.LENGTH_SHORT);
					break;
				}
			}
			replysAdapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();
			isRefreshing = false;
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	private class GetReplyDataTask extends
			AsyncTask<Long, Void, ArrayList<Reply>> {
		int resultCode = -1;// 错误类型

		@Override
		protected ArrayList<Reply> doInBackground(Long... params) {
			isRefreshing = true;
			long srcType = params[0];
			long object = params[1];
			long repId=params[2];

			ArrayList<Reply> mLocalReplys = null;
			switch ((int) srcType) {
			case Constants.SRC_NET:
				long localReplyNum = ReplyDao.getReplyNum(context, quesId);
				long localLens = 10L;
				switch ((int) object) {
				case Constants.DATA_INIT:
					if (localReplyNum > 0) {
						if (localReplyNum <= localLens) {
							localLens = localReplyNum;
							isAllDownload = true;
						}
						mLocalReplys = ReplyDao.getReply(context,repId,
								(long) replys.size(), localLens, 1);
					} else {
						resultCode = -1;
					}
					break;
				case Constants.DATA_AFTER:
					resultCode = -1;
					break;

				}
				break;
			case Constants.SRC_LOCAL:
				localReplyNum = ReplyDao.getReplyNum(context, quesId);
				localLens = 10L;
				switch ((int) object) {
				case Constants.DATA_INIT:
					if (localReplyNum > 0) {
						if (localReplyNum <= localLens) {
							localLens = localReplyNum;
							isAllDownload = true;
						}
						mLocalReplys = ReplyDao.getReply(context, repId,
								(long) replys.size(), localLens, 1);
					} else {
						resultCode = -1;
					}
					break;
				case Constants.DATA_AFTER:
					resultCode = -1;
					break;
				case Constants.DATA_BEFORE:
					long lave = localReplyNum - (replys.size() + localLens);
					if (lave <= 0) {
						localLens = localReplyNum - replys.size();
						isAllDownload = true;
					}
					mLocalReplys = ReplyDao.getReply(context, repId,
							(long) replys.size(), localReplyNum, 1);
				}
				break;
			}
			return mLocalReplys;
		}

		@Override
		protected void onPostExecute(ArrayList<Reply> result) {
			progressDialog.dismiss();
			if (result != null) {

				onLastItemVisible();
			} else {
				switch (resultCode) {
				case -1:
					showToast("暂无数据...", Toast.LENGTH_SHORT);
					break;
				case Constants.ERR_LOCAL_NO_DATA:
					showToast("本地无数据,打开网络连接加载数据...", Toast.LENGTH_SHORT);
					break;
				case Constants.ERR_LOCAL_NO_NEW_DATA:
					showToast("请打开网络连接,加载新数据...", Toast.LENGTH_SHORT);
					break;
				}
			}
			replysAdapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();
			isRefreshing = false;
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
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
			if (convertView == null) {
				convertView = LayoutInflater.from(
						activity.getApplicationContext()).inflate(
						R.layout.reply_item, null);
			}
			final Reply reply = replys.get(position);
			final User user = UserDao.getUserById(ReplysActiviy.this,
					reply.getUserId());
			ImageView avatar = (ImageView) convertView
					.findViewById(R.id.avatar);
			final int width = AppUtils.getDefaultPhotoWidth(ReplysActiviy.this,
					9);
			AppUtils.setViewSize(avatar, width, width);
			loader.displayImage(user.getAvatar(), avatar, options);
			((TextView) convertView.findViewById(R.id.nickname)).setText(user
					.getNickname());
			final TextView distance = (TextView) convertView
					.findViewById(R.id.location);
			if (user.getUserId() == MyApplication.getLoginUser(context)
					.getUserId())
				distance.setText("附近");
			else
				distance.setText("1.2千米");
			((TextView) convertView.findViewById(R.id.reply_time))
					.setText(AppUtils.timeToNow(reply.getReplyTime()));
			TextView content = ((TextView) convertView
					.findViewById(R.id.content));
			if (reply.getContent() == null || reply.getContent().length() == 0)
				content.setVisibility(View.GONE);
			else
				content.setText(reply.getContent());
			final TextView goRely = (TextView) convertView
					.findViewById(R.id.go_reply);
			MGridView photosView = (MGridView) convertView
					.findViewById(R.id.photos_view);
			if (reply.getImages() == null || reply.getImages().size() < 0) {
				photosView.setVisibility(View.GONE);
			} else {
				photosView.setAdapter(new PhotosAdapter(ReplysActiviy.this,
						reply.getImages(), null));
			}
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
			MListView replysView = (MListView) convertView
					.findViewById(R.id.replys);
			final MListViewAdapter adapter = new MListViewAdapter(
					ReplysActiviy.this, user.getNickname(), reply.getReplys());
			if (reply.getReplys() == null || reply.getReplys().size() < 0) {
				replysView.setVisibility(View.GONE);
			} else {
				replysView.setAdapter(adapter);
			}

			goRely.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					toggleEditor();
					curRepId = reply.getRepId();
					curRepAdapter = adapter;
				}
			});
			return convertView;
		}
	}

	private class MListViewAdapter extends BaseAdapter {

		private Activity activity;
		private String nickname;
		private ArrayList<Reply> replys;

		public MListViewAdapter(Activity activity, String nickname,
				ArrayList<Reply> replys) {
			this.activity = activity;
			this.nickname = nickname;
			this.replys = replys;
		}

		public ArrayList<Reply> getReplys() {
			return replys;
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
						R.layout.secondary_reply_item, null);
			}
			final Reply reply = (Reply) getItem(position);
			((TextView) convertView.findViewById(R.id.nickname))
					.setText(MyApplication.getLoginUser(ReplysActiviy.this)
							.getNickname() + "回复：" + nickname);
			((TextView) convertView.findViewById(R.id.reply_time))
					.setText(AppUtils.timeToNow(reply.getReplyTime()));
			((TextView) convertView.findViewById(R.id.content)).setText(reply
					.getContent());
			return convertView;
		}
	}

	private class LongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Reply reply = replys.get(position - 1);
			deleteReply(reply, position - 1);
			return true;
		}
	};

	public void OnClick(View view) {
		switch (view.getId()) {
		case R.id.dis_more:
			new GetDataTask().execute(1L, 2L);
			break;
		}
	}

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
		String content = editor.getContent();
		if ((content != null && content.length() > 0)) {
			Reply reply = new Reply(0, content, null, null, null, curRepId,
					MyApplication.getLoginUser(this).getUserId(), 1);
			long repId = ReplyDao.insertReply(this, reply);
			if (repId != 0L) {
				editor.getThumbnailsParentView().setVisibility(View.INVISIBLE);
				editor.getExtrasView().setVisibility(View.GONE);
				editor.getThumbnails().clear();
				editor.clearContent();
				editor.setCurPhotosNum(0);
				reply.setRepId(repId);
				reply.setReplyTime(new Date());
				curRepAdapter.getReplys().add(0, reply);
				curRepAdapter.notifyDataSetChanged();
				toggleEditor();
			} else {
				Toast.makeText(this, "回复失败。。。", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "没有发现回复内容。。。", Toast.LENGTH_SHORT).show();
		}
	}

	// 删除问题
	public void deleteReply(final Reply reply, final int location) {
		if (reply.getUserId() == MyApplication.getLoginUser(this).getUserId()) {
			Dialog alert = new AlertDialog.Builder(this).setTitle("破题")
					.setMessage("确定删除回复?")
					.setPositiveButton("确定", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!ReplyDao.deleteReply(ReplysActiviy.this,
									reply.getRepId(), 0)) {
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

	public void showProgressDialog() {
		progressDialog.setMessage("正在 加载回复列表");
		progressDialog.show();
	}

	public void refreshData(Long param) {
		Long[] params = new Long[2];
		params[1] = param;

		if (hasInternetConnected()) {
			progressDialog.show();
			noNetworkConnect.setVisibility(View.GONE);
			params[0] = 0L;
			new GetDataTask().execute(params);
		} else {
			noNetworkConnect.setVisibility(View.VISIBLE);
			params[0] = 1L;
			if (replys.size() == 0)
				params[1] = 0L;
			new GetDataTask().execute(params);
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(getApplicationContext(),
				System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		if (!isRefreshing)
			refreshData(1L);
	}

	@Override
	public void onLastItemVisible() {
		if (isAllDownload) {
			disMore.setVisibility(View.GONE);
			Toast.makeText(context, "数据加载完毕...", Toast.LENGTH_SHORT).show();
		} else {
			disMore.setVisibility(View.VISIBLE);
		}
	}

}
