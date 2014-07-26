package scu.android.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import scu.android.application.MyApplication;
import scu.android.dao.Story;
import scu.android.dao.User;
import scu.android.db.DBTools;
import scu.android.ui.MEditText;
import scu.android.util.ActivitySupport;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.demo.note.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;

public class SchoolStoryActivity extends ActivitySupport implements
		OnEditorActionListener, OnRefreshListener<ListView>, OnItemLongClickListener {

	private MEditText title;
	private MEditText content;

	private final String TAG = getClass().getName();
	private ArrayList<Story> storys;
	private StorysAdapter storysAdapter;
	private PullToRefreshListView refreshView;// 下拉刷新组件
	private boolean isRefreshing;// 刷新中
	private boolean isDownloading;// 下载中
	private View noNetworkConnect;// 无网络连接
	private User loginUser;// 登录用户
	private boolean isNetworkConnected;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_school_story);
		init();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// NavUtils.navigateUpFromSameTask(this);
			finish();
			break;
		}
		return false;
	}

	public void init() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		title = (MEditText) findViewById(R.id.title);
		title.setOnEditorActionListener(this);
		content = (MEditText) findViewById(R.id.content);
		content.setOnEditorActionListener(this);

		loginUser = MyApplication.getCurrentUser(context);
		noNetworkConnect=findViewById(R.id.no_network_connect);
		refreshView = (PullToRefreshListView) findViewById(R.id.school_storys);

		ListView storysView = refreshView.getRefreshableView();
		storys = new ArrayList<Story>();
		storysAdapter = new StorysAdapter();
		storysView.setAdapter(storysAdapter);
		// storysView.setOnItemClickListener(this);
		storysView.setOnItemLongClickListener(this);
		refreshView.setMode(Mode.BOTH);
		refreshView.setOnRefreshListener(this);

		receiver = new StorysBroadcastRecevier();
		registerReceiver(receiver, new IntentFilter(Constants.STORYS));
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		switch (actionId) {
		case EditorInfo.IME_ACTION_DONE:
			content.setVisibility(View.VISIBLE);
			break;
		case EditorInfo.IME_ACTION_SEND:
			uploadStory();
			content.setVisibility(View.GONE);
			break;
		}
		return false;
	}

	/**
	 * 上传或下载之后更新故事列表
	 */
	private class StorysBroadcastRecevier extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.STORYS)) {
				final String action = intent.getStringExtra("action");
				if (action.equals("upload") && MyApplication.isUploading) {
					final String result = intent.getStringExtra("result");
					if (result.equals("success")) {
						showToast("上传成功");
					} else if (result.equals("failed")) {
						showToast("上传失败");
					}
					MyApplication.isUploading = false;
				} else if (action.equals("download") && isDownloading) {
					@SuppressWarnings("unchecked")
					ArrayList<Story> mStorys = (ArrayList<Story>) intent.getSerializableExtra("storys");
					if (mStorys != null && mStorys.size() > 0) {
						final Story firstStory = mStorys.get(0);
						if (storys.size() > 0&& firstStory.getCreated_time() > storys.get(0).getCreated_time()) {
							storys.addAll(0, mStorys);
						} else {
							storys.addAll(mStorys);
						}
						showToast("下载完毕");
					} else {
						showToast("已获取最新数据");
					}
					storysAdapter.notifyDataSetChanged();
					isDownloading = false;
				} else if (action.equals("special")) {
					if (isDownloading) {
						showToast("正在下载中,稍后再试...");
					} else {
						loadData(Constants.DOWN);
					}
				}
			}
		}
	}

	/**
	 * 获取数据，查看更多
	 */
	private class GetDataTask extends AsyncTask<Integer, Void, List<Story>> {

		@Override
		protected List<Story> doInBackground(Integer... params) {
			isRefreshing = true;
			final int action = params[0];
			final DBTools mDBTools = DBTools.getInstance(context);
			List<Story> mStorys = new ArrayList<Story>();
			final int size = storys.size();
			if (size > 0) {// 获取更多数据
				long floorTime = storys.get(0).getCreated_time();
				long topTime = storys.get(size - 1).getCreated_time();
				switch (action) {
				case Constants.DOWN:// 下拉
					mStorys = mDBTools.loadStorys(floorTime);
					if (mStorys.size() == 0) {
						if (isNetworkConnected) {
							isDownloading = true;
							LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
							mParams.add(new BasicNameValuePair("start", "0"));
							mParams.add(new BasicNameValuePair("floorTime",
									String.valueOf(floorTime)));
							MyApplication.downloadStory(context, mParams);
						}
					}
					break;
				case Constants.UP:// 上拉
					mStorys = mDBTools.loadStorys(topTime, 5);// 本地数据
					if (mStorys.size() == 0) {
						if (isNetworkConnected) {
							isDownloading = true;
							LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
							mParams.add(new BasicNameValuePair("start", "0"));
							mParams.add(new BasicNameValuePair("topTime",
									String.valueOf(topTime)));
							MyApplication.downloadStory(context, mParams);
						}
					}
					break;
				}
			} else {// 首次加载数据
				mStorys = DBTools.getInstance(context).loadStorys(5);
				if (mStorys.size() == 0) {
					if (isNetworkConnected) {
						isDownloading = true;
						LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
						mParams.add(new BasicNameValuePair("start", "0"));
						MyApplication.downloadStory(context, mParams);
					}
				}
			}
			return mStorys;
		}

		@Override
		protected void onPostExecute(List<Story> result) {
			if (hasInternetConnected()) {
				noNetworkConnect.setVisibility(View.GONE);
			} else {
				noNetworkConnect.setVisibility(View.VISIBLE);
			}
			if (result.size() > 0) {
				final Story firstStory = result.get(0);
				if (storys.size() > 0
						&& firstStory.getCreated_time() > storys.get(0)
								.getCreated_time()) {
					storys.addAll(0, result);
				} else {
					storys.addAll(result);
				}
			} else {
				if (!isDownloading)
					showToast("没有加载到数据");
			}
			isRefreshing = false;
			storysAdapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();
		}
	}

	private class StoryViewHolder {
		TextView title;
		TextView content;
		TextView nickname;
		TextView distance;
		TextView publishTime;

	}

	/**
	 * 破题列表适配器
	 */
	private class StorysAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public StorysAdapter() {
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return storys.size();
		}

		@Override
		public Object getItem(int position) {
			return storys.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			StoryViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.school_story_item, null);
				holder = new StoryViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				holder.nickname = (TextView) convertView
						.findViewById(R.id.nickname);
				holder.distance = (TextView) convertView
						.findViewById(R.id.distance);
				holder.publishTime = (TextView) convertView
						.findViewById(R.id.publish_time);
				convertView.setTag(holder);
			} else {
				holder = (StoryViewHolder) convertView.getTag();
			}
			final Story mStory = (Story) getItem(position);
			holder.title.setText(mStory.getS_title());
			holder.content.setText(mStory.getS_text());
			holder.nickname.setText(loginUser.getUser_nickname());
			holder.distance.setText("附近");
			holder.publishTime.setText(AppUtils.timeToNow(mStory
					.getCreated_time()));
			return convertView;
		}

	}

	public void loadData(int action) {
		isNetworkConnected = AppUtils.isNetworkConnect(context);
		if (!isRefreshing && !isDownloading) {
			new GetDataTask().execute(action);
		} else {
			showToast("刷新中...");
			refreshView.onRefreshComplete();
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.isHeadherShow()) {
			String label = DateUtils.formatDateTime(getApplicationContext(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
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

	public void uploadStory() {
		long s_user = MyApplication.getCurrentUser(this).getUser_id();
		String s_title = title.getText().toString().trim();
		if (s_title.length() != 0) {
			String s_text = content.getText().toString();
			Story story = new Story(null, null, s_text, s_user, s_title,
					System.currentTimeMillis() / 1000);
			final long id = DBTools.getInstance(context).insertStory(story);
			MyApplication.oldId = id;
			MyApplication.uploadStory(context, story);
		} else {
			showToast("忘记输入标题了。。。");
		}
		title.setText(null);
		content.setText(null);
	}
	
	// 删除问题
	public void deleteStory(final Story story, final int location) {
			Dialog alert = new AlertDialog.Builder(this).setTitle("破题").setMessage("确定删除该故事?")
					.setPositiveButton("确定", new Dialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DBTools.getInstance(context).deleteStory(story);
							storys.remove(location);
							storysAdapter.notifyDataSetChanged();
							if (storys.size() == 0){
					
							}
						}
					}).setNegativeButton("取消", new Dialog.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).create();
			alert.show();
	}
	
//	public void toggleRefreshView(){
//		if(storys.size()==0){
//			refreshView.setVisibility(View.GONE);
//		}else{
//			refreshView.setVisibility(View.VISIBLE);
//		}
//	}
	
	@Override
	protected void onResume() {
		loadData(Constants.DOWN);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Story story = storys.get(position - 1);
		deleteStory(story, position - 1);
		return true;
	}

}
