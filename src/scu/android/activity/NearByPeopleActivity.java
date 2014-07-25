package scu.android.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import scu.android.application.MyApplication;
import scu.android.dao.User;
import scu.android.db.DBTools;
import scu.android.util.ActivitySupport;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import scu.android.util.MLocationManager;
import scu.android.util.MLocationManager.LocationCallBack;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.demo.note.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 附近的人列表
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class NearByPeopleActivity extends ActivitySupport implements
		OnRefreshListener<ListView>,LocationCallBack {

	private PullToRefreshListView refreshView;

	private boolean isRefreshing;// 刷新中
	private boolean isDownloading;// 下载中
	private usersAdapter usersAdapter;
	private ArrayList<User> users;
	private MLocationManager locationManager;

	private ImageLoader loader;
	private DisplayImageOptions options;
	private BroadcastReceiver receiver;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nerarby_people);
		init();
	}

	public void init() {
		refreshView = (PullToRefreshListView) findViewById(R.id.nearby_list);
		refreshView.setMode(Mode.BOTH);
		refreshView.setOnRefreshListener(this);
		
		final ListView usersView = refreshView.getRefreshableView();
		users = new ArrayList<User>();
		initImageLoader();
		loadData(Constants.DOWN);

		usersAdapter = new usersAdapter(users);
		usersView.setAdapter(usersAdapter);

		locationManager=MLocationManager.getInstance(context, NearByPeopleActivity.this);
		receiver = new UsersBroadcastRecevier();
		context.registerReceiver(receiver, new IntentFilter(Constants.PEOPLE_NEARBY));
	}

	public void initImageLoader() {
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageForEmptyUri(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(10))
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nearby_people_actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.nearby_settings:
			final View view = findViewById(R.id.nearby_settings);
			showPopupMenu(view);
			break;
		}
		return true;
	}

	private class UsersBroadcastRecevier extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.PEOPLE_NEARBY)) {
				final String action = intent.getStringExtra("action");
				if (action.equals("download") && isDownloading) {
					@SuppressWarnings("unchecked")
					ArrayList<User> mUsers = (ArrayList<User>) intent
							.getSerializableExtra("Users");
					if (mUsers != null && mUsers.size() > 0) {
						final User firstUser = mUsers.get(0);
						if (users.size() > 0
								&& firstUser.getCreated_time() > users.get(0)
										.getCreated_time()) {
							users.addAll(0, mUsers);
						} else {
							users.addAll(mUsers);
						}
						showToast("下载完毕");
					} else {
						showToast("已获取最新数据");
					}
					usersAdapter.notifyDataSetChanged();
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
	private class GetDataTask extends AsyncTask<Integer, Void, List<User>> {

		@Override
		protected List<User> doInBackground(Integer... params) {
			isRefreshing = true;
			final int action = params[0];
			final boolean isNetworkConnected = hasInternetConnected();
			final DBTools mDBTools = DBTools.getInstance(context);
			List<User> mUsers = new ArrayList<User>();
			final int size = users.size();
			if (size > 0) {// 获取更多数据
				long floorTime = users.get(0).getCreated_time();
				long topTime = users.get(size - 1).getCreated_time();
				switch (action) {
				case Constants.DOWN:// 下拉
					// mUsers = mDBTools.loadUsers(floorTime);
					if (mUsers.size() == 0) {
						if (isNetworkConnected) {
							isDownloading = true;
							LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
							mParams.add(new BasicNameValuePair("start", "0"));
							mParams.add(new BasicNameValuePair("floorTime",
									String.valueOf(floorTime)));
							// MyApplication.downloadUser(context, mParams);
						}
					}
					break;
				case Constants.UP:// 上拉
					// mUsers = mDBTools.loadUsers(topTime, 5);// 本地数据
					if (mUsers.size() == 0) {
						if (isNetworkConnected) {
							isDownloading = true;
							LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
							mParams.add(new BasicNameValuePair("start", "0"));
							mParams.add(new BasicNameValuePair("topTime",
									String.valueOf(topTime)));
							// MyApplication.downloadUser(context, mParams);
						}
					}
					break;
				}
			} else {// 首次加载数据
			// mUsers = DBTools.getInstance(context).loadUsers(5);
				if (mUsers.size() == 0) {
					if (isNetworkConnected) {
						isDownloading = true;
						LinkedList<NameValuePair> mParams = new LinkedList<NameValuePair>();
						mParams.add(new BasicNameValuePair("start", "0"));
						// MyApplication.downloadUser(context, mParams);
					}
				}
			}
			return mUsers;
		}

		@Override
		protected void onPostExecute(List<User> result) {
			if (!hasInternetConnected()) {
				showAlertDialog();
			}
			if (result.size() > 0) {
				final User firstUser = result.get(0);
				if (users.size() > 0
						&& firstUser.getCreated_time() > users.get(0)
								.getCreated_time()) {
					users.addAll(0, result);
				} else {
					users.addAll(result);
				}
			} else {
				if (!isDownloading)
					showToast("没有加载到数据");
			}
			isRefreshing = false;
			usersAdapter.notifyDataSetChanged();
			refreshView.onRefreshComplete();
		}
	}

	private class usersAdapter extends BaseAdapter {
		ArrayList<User> users;

		public usersAdapter(ArrayList<User> users) {
			this.users = users;
		}

		@Override
		public int getCount() {
			return users.size();
		}

		@Override
		public Object getItem(int position) {
			return users.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.nearby_list_item, null);
			}
			// final User record = (User) getItem(position);
			final User record = MyApplication.getCurrentUser(context);
			final ImageView avatar = (ImageView) convertView
					.findViewById(R.id.list_item_icon);
			if (record.getUser_avatar() != null) {
				final String avatarPath = "assets://" + record.getUser_avatar();
				loader.displayImage(avatarPath, avatar, options);
			}
			((TextView) convertView.findViewById(R.id.find_user))
					.setText(record.getUser_nickname());
			((TextView) convertView.findViewById(R.id.distance_textview))
					.setText("100米以内");
			((TextView) convertView.findViewById(R.id.list_item_description))
					.setText("不用看了，这就是我的个性签名...");
			return convertView;
		}
	}

	// 加载数据
	public void loadData(int action) {
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
			String label = DateUtils.formatDateTime(
					this.getApplicationContext(), System.currentTimeMillis(),
					DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			loadData(Constants.DOWN);
		} else {
			loadData(Constants.UP);
		}
	}

	public void showPopupMenu(View v) {
		PopupMenu popup = new PopupMenu(this, v);
		popup.setOnMenuItemClickListener(new PopupItemClickListener());
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.nearby_popup_menu, popup.getMenu());
		popup.show();
	}

	public void showProgressDialog() {
		dialog = getProgressDialog();
		dialog.setMessage("正在搜索附近的人");
		dialog.show();
	}

	public void showAlertDialog() {
		final String title = "附近的人";
		final String message = getString(R.string.nearby_error_tips);
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setNeutralButton("设置", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AppUtils.networkSet(NearByPeopleActivity.this);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
	}

	private class PopupItemClickListener implements OnMenuItemClickListener {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.popup_menu_option_all:
				break;
			case R.id.popup_menu_option_clear:
				finish();
				break;
			}
			return true;
		}
	}

	@Override
	public void onDestroy() {
		locationManager.destoryLocationManager();
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	public void updateLocation(Location location) {
		showToast(location.getLatitude()+","+location.getLongitude());
	}
}
