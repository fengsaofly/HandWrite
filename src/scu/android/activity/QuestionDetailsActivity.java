package scu.android.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import scu.android.application.MyApplication;
import scu.android.base.MFindInputField;
import scu.android.dao.Question;
import scu.android.dao.Reply;
import scu.android.dao.Resource;
import scu.android.dao.User;
import scu.android.db.DBTools;
import scu.android.ui.CircularImage;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 回复问题
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class QuestionDetailsActivity extends ActivitySupport implements OnScrollListener, OnClickListener {
	private Context context;
	private Question question;// 问题
	private User user;
	private Long replyNum;
	private MFindInputField input;
	private BroadcastReceiver receiver;
	private ImageLoader loader;
	private DisplayImageOptions options;

	// private PullToRefreshListView refreshView;// 下拉刷新组件
	// private View noNetworkConnect;
	private boolean isRefreshing;// 刷新中
	private boolean isDownloading;// 下载中
	private ReplysAdapter replysAdapter;
	private ArrayList<Reply> replys;
	private long curRepId;
	private MListViewAdapter curRepAdapter;

	private ListView replysView;
	private View header;
	private View loadingMore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_reply_question_item);
		init();
	}

	public void init() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("问题详情");
		context = getApplicationContext();
		initImageLoader();

		replysView = (ListView) findViewById(R.id.replys_view);
		header = LayoutInflater.from(context).inflate(R.layout.question_detail_header, null, false);
		header.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		initQuestion();
		replysView.addHeaderView(header, null, false);
		loadingMore = LayoutInflater.from(context).inflate(R.layout.loading_more, null, false);
		loadingMore.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
		replysView.addFooterView(loadingMore, null, true);
		loadingMore.findViewById(R.id.loading_more).setOnClickListener(this);
		// replysView.setEmptyView(findViewById(R.id.empty_view));
		replys = new ArrayList<Reply>();
		replysAdapter = new ReplysAdapter(context, replys);
		replysView.setAdapter(replysAdapter);
		replysView.setOnScrollListener(this);
		replysView.setOnItemLongClickListener(new LongClickListener());
		replysView.setVisibility(View.VISIBLE);

		input = (MFindInputField) findViewById(R.id.input);
		input.init(this, Constants.REPLYS);

		loadData(Constants.DOWN);

		receiver = new ReplysBroadcastRecevier();
		registerReceiver(receiver, new IntentFilter(Constants.REPLYS));
	}

	public void initImageLoader() {
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public void initQuestion() {
		user = (User) getIntent().getSerializableExtra("user");
		loader.displayImage(user.getUser_avatar(), (CircularImage) header.findViewById(R.id.avatar_val), options);
		((TextView) header.findViewById(R.id.nickname)).setText(user.getUser_nickname());
		((TextView) header.findViewById(R.id.distance)).setText("附近");
		question = (Question) getIntent().getSerializableExtra("question");
		List<Resource> mResources = DBTools.getInstance(context).loadResources(question.getQ_resource());
		String thumbnailUri = DBTools.getSImage(mResources);
		String imgUri = DBTools.getLImage(mResources);
		((TextView) header.findViewById(R.id.publish_time)).setText(AppUtils.timeToNow(question.getCreated_time()));
		((TextView) header.findViewById(R.id.post_alert_info)).setText(DBTools.getState(question.getQ_state()));
		ImageView thumbnail = (ImageView) header.findViewById(R.id.find_img);
		if (thumbnailUri != null) {
			AppUtils.disImg(context, loader, options, thumbnail, thumbnailUri, imgUri);
		} else {
			thumbnail.setVisibility(View.GONE);
		}
		String descStr = question.getQ_text_content();
		TextView desc = ((TextView) header.findViewById(R.id.find_desc));
		if (descStr != null) {
			desc.setText(question.getQ_text_content());
		} else {
			desc.setVisibility(View.GONE);
		}
		((TextView) header.findViewById(R.id.grade)).setText(question.getQ_grade());
		((TextView) header.findViewById(R.id.subject)).setText(question.getQ_subject());
	}

	/*
	 * 获取问题的回复数目
	 */
	private class GetReplysTask extends AsyncTask<Long, Void, Long> {

		@Override
		protected Long doInBackground(Long... params) {
			long replyNum = DBTools.getInstance(context).getReplysNum(question.getQ_id());
			return replyNum;
		}

		@Override
		protected void onPostExecute(Long result) {
			replyNum = result;
			// ((TextView) findViewById(R.id.reply_number)).setText("回答(" +
			// replyNum + ")");
			super.onPostExecute(result);
		}

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.reply:
			doReply();
			break;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final String prefix = "file:///";
		switch (requestCode) {
		case Constants.SYS_CAMEAR:// 相机
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = input.getImgUri();
				input.setImg(prefix + imgPath);
			}else{
				input.setImgUri(null);
			}
			break;
		case Constants.PHONE_PICTURES:// 图库
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> photos = data.getStringArrayListExtra("photos");
				input.setImg(prefix + photos.get(0));
			}
			break;
		case Constants.DOODLE_BOARD:// 涂鸦
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("doodlePath");
				input.setImg(prefix + imgPath);
			}
			break;
		case Constants.HANDWRITE_BOARD:// 手写
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("handwritePath");
				input.setImg(prefix + imgPath);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 回复问题
	public void doReplyQuestion() {
		closeInput();
		final String qr_text = input.getText();
		final long qr_user = MyApplication.getCurrentUser(context).getUser_id();
		ArrayList<String> thumbnails = new ArrayList<String>();
		if (input.getImgUri() != null) {
			thumbnails.add(input.getImgUri());
		}
		if ((qr_text != null && qr_text.length() > 0) || thumbnails.size() > 0) {
			List<Resource> resources = new ArrayList<Resource>();
			for (String resourcePath : thumbnails) {
				resources.add(new Resource(null, null, resourcePath, resourcePath));
			}
			final String path = input.getAudio();
			if (path != null)
				resources.add(new Resource(null, null, null, path));
			final long qr_q = question.getQ_id();
			long qr_resource = -1;
			if (resources.size() > 0) {
				qr_resource = DBTools.getInstance(context).insertResources(resources);
				MyApplication.oldResourceId = qr_resource;
			}
			Reply reply = new Reply(null, null, qr_text, System.currentTimeMillis() / 1000, (short) 0, qr_user, qr_q, qr_resource);
			final long rId = DBTools.getInstance(context).insertReply(reply);
			
			input.reset();
			MyApplication.oldId = rId;
			MyApplication.uploadReply(context, reply);// 上传回复

			
		} else {
			showToast("没有发现回复内容。。。");
		}
	}

	public void myOnclick(View view) {
		switch (view.getId()) {
		case R.id.reply_number:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
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
						toggleEmptyView();
						isDownloading = false;
						replysView.invalidate();
					}
				} else {
					doReplyQuestion();
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
			final boolean isNetworkConnected = AppUtils.isNetworkConnect(QuestionDetailsActivity.this);
			final DBTools mDBTools = DBTools.getInstance(context);
			List<Reply> mReplys = new ArrayList<Reply>();
			final int size = replys.size();
			if (size > 0) {// 获取更多数据
				long floorTime = replys.get(0).getCreated_time();
				long topTime = replys.get(size - 1).getCreated_time();
				switch (action) {
				case Constants.DOWN:// 下拉
					mReplys = mDBTools.loadReplys(question.getQ_id(), (short) 0, floorTime);
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
				mReplys = mDBTools.loadReplys(question.getQ_id(), (short) 0, 10);
				Log.d(getClass().getName(), "|replys:" + mReplys.size());
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
			// if (AppUtils.isNetworkConnect(ReplysActiviy.this)) {
			// noNetworkConnect.setVisibility(View.GONE);
			// } else {
			// noNetworkConnect.setVisibility(View.VISIBLE);
			// }
			Log.d(getClass().getName(), ":" + result.size());
			if (result.size() > 0) {
				final Reply firstReply = result.get(0);
				if (replys.size() > 0 && firstReply.getCreated_time() > replys.get(0).getCreated_time()) {
					replys.addAll(0, result);
				} else {
					replys.addAll(result);
				}
			} else {
				toggleEmptyView();
				if (!isDownloading)
					showToast("没有加载到数据");
			}

			replysAdapter.notifyDataSetChanged();
			// refreshView.onRefreshComplete();
			isRefreshing = false;
		}
	}

	static class ReplyHolder {
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
			ReplyHolder holder = new ReplyHolder();
			// if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.question_reply_item, null, false);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar_val);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder.reply = (ImageView) convertView.findViewById(R.id.reply);
			holder.publishTime = (TextView) convertView.findViewById(R.id.publish_time);
			holder.postAlterInfo = (TextView) convertView.findViewById(R.id.post_alert_info);
			holder.desc = (TextView) convertView.findViewById(R.id.find_desc);
			holder.img = (ImageView) convertView.findViewById(R.id.find_img);
			holder.audio = (MAudioView) convertView.findViewById(R.id.audio);
			holder.replys = (MListView) convertView.findViewById(R.id.replys);
			convertView.setTag(holder);
			// } else {
			holder = (ReplyHolder) convertView.getTag();
			// }

			final Reply reply = (Reply) replys.get(position);
			User user = MyApplication.getCurrentUser(context);
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
			final MListViewAdapter adapter = new MListViewAdapter(QuestionDetailsActivity.this, user.getUser_nickname(), mReplysList);
			if (mReplysList == null || mReplysList.size() == 0) {
				holder.replys.setVisibility(View.GONE);
			} else {
				holder.replys.setAdapter(adapter);
			}
			holder.reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					input.enableExtras(false);
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
					// toggleEditor();
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

	// 回复评论
	public void doReply() {
		// editor.hideSoft();
		// String qr_text = editor.getContent();
		// if ((qr_text != null && qr_text.length() > 0)) {
		// final long qr_user =
		// MyApplication.getCurrentUser(context).getUser_id();
		// Reply reply = new Reply(null, null, qr_text,
		// System.currentTimeMillis() / 1000, (short) 1, qr_user, curRepId,
		// -1l);
		// long rId = DBTools.getInstance(context).insertReply(reply);
		// if (rId != 0L) {
		// editor.getThumbnailsParentView().setVisibility(View.INVISIBLE);
		// editor.getExtrasView().setVisibility(View.GONE);
		// editor.getThumbnails().clear();
		// editor.clearContent();
		// editor.setCurPhotosNum(0);
		// reply.setQr_id(rId);
		// if (curRepAdapter.mReplys == null) {
		// curRepAdapter.mReplys = new ArrayList<Reply>();
		// }
		// curRepAdapter.mReplys.add(0, reply);
		// curRepAdapter.notifyDataSetChanged();
		// // toggleEditor();
		// MyApplication.oldId = rId;
		// MyApplication.uploadReply(context, reply);// 上传回复
		// } else {
		// Toast.makeText(this, "回复失败。。。", Toast.LENGTH_SHORT).show();
		// }
		// } else {
		// Toast.makeText(this, "没有发现回复内容。。。", Toast.LENGTH_SHORT).show();
		// }
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

	public void toggleEmptyView() {
		if (replys.size() == 0) {
			findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.empty_view).setVisibility(View.GONE);
		}
		// replysView.removeFooterView(loadingMore);
	}

	// private int lastItem;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		// if(lastItem==replys.size()&&scrollState==SCROLL_STATE_IDLE){
		// replysView.addFooterView(loadingMore, null, false);
		// loadData(Constants.DOWN);
		// }
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// lastItem=firstVisibleItem+visibleItemCount-1;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loading_more:
			if (!MyApplication.isUploading) {
				loadData(Constants.DOWN);
			}
			break;
		}
	}

	// @Override
	// public void onRefresh(PullToRefreshBase<ListView> refreshView) {
	// if (refreshView.isHeadherShow()) {
	// String label = DateUtils.formatDateTime(getApplicationContext(),
	// System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME |
	// DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
	// refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	// if (!MyApplication.isUploading) {
	// loadData(Constants.DOWN);
	// } else {
	// showToast("玩命上传中,稍后刷新...");
	// this.refreshView.onRefreshComplete();
	// }
	// } else {
	// loadData(Constants.UP);
	// }
	// }
}
