package scu.android.activity;

import java.util.ArrayList;
import scu.android.entity.User;
import scu.android.util.ActivitySupport;
import scu.android.util.AppUtils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
 * 附近的人列表
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class NearByPeopleActivity extends ActivitySupport implements
		OnLastItemVisibleListener, OnRefreshListener<ListView> {

	private PullToRefreshListView refreshListView;

	private boolean isRefreshing;
	private boolean isAllDownload;
	private RecordsAdapter recordsAdapter;
	private ArrayList<User> records;

	private ImageLoader loader;
	private DisplayImageOptions options;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nerarby_people);
		init();
	}

	public void init() {
		refreshListView = (PullToRefreshListView) findViewById(R.id.nearby_list);
		refreshListView.setOnRefreshListener(this);
		refreshListView.setOnLastItemVisibleListener(this);
		final ListView recordsView = refreshListView.getRefreshableView();
		records = new ArrayList<User>();
		isAllDownload = false;
		refreshData(0L);

		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageForEmptyUri(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(10))
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		recordsAdapter = new RecordsAdapter(records);
		recordsView.setAdapter(recordsAdapter);
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

	private class GetDataTask extends AsyncTask<Long, Void, ArrayList<User>> {
		int resultCode = -1;// 错误类型

		@Override
		protected ArrayList<User> doInBackground(Long... params) {
			isRefreshing = true;

			// long param = params[0];param=0代表首次获取数据，1代表获取后面的数据
			if (hasInternetConnected()) {
			} else {
				resultCode = 0;
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<User> result) {
			dialog.dismiss();
			if (result != null) {
				records.addAll(result);
				recordsAdapter.notifyDataSetChanged();
				onLastItemVisible();
			} else {
				switch (resultCode) {
				case 0:
					showAlertDialog();
					break;
				default:
					showToast("暂无数据", Toast.LENGTH_SHORT);
					break;
				}
			}
			refreshListView.onRefreshComplete();
			isRefreshing = false;
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	private class RecordsAdapter extends BaseAdapter {
		ArrayList<User> records;

		public RecordsAdapter(ArrayList<User> records) {
			this.records = records;
		}

		@Override
		public int getCount() {
			return records.size();
		}

		@Override
		public Object getItem(int position) {
			return records.get(position);
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
			final User record = (User) getItem(position);
			final ImageView avatar = (ImageView) convertView
					.findViewById(R.id.list_item_icon);
			if (record.getAvatar() != null) {
				final String avatarPath = "assets://" + record.getAvatar();
				loader.displayImage(avatarPath, avatar, options);
			}
			((TextView) convertView.findViewById(R.id.find_user))
					.setText(record.getNickname());
			((TextView) convertView.findViewById(R.id.distance_textview))
					.setText("100米以内");
			((TextView) convertView.findViewById(R.id.list_item_description))
					.setText("不用看了，这就是我的个性签名...");
			return convertView;
		}
	}

	// 加载数据
	public void refreshData(Long param) {
		if (hasInternetConnected()) {
			showProgressDialog();
			new GetDataTask().execute(param);
		} else {
			showAlertDialog();
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
			refreshData(0L);
	}

	@Override
	public void onLastItemVisible() {
		if (isAllDownload) {
			// disMore.setVisibility(View.GONE);
			final String text = "数据加载完毕...";
			showToast(text, Toast.LENGTH_SHORT);
		} else {
			// disMore.setVisibility(View.VISIBLE);
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

			}
			return true;
		}
	}

}
