package scu.android.activity;

import java.util.ArrayList;
import org.jivesoftware.smack.RosterGroup;
import scu.android.util.ActivitySupport;
import scu.android.util.AppUtils;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 群组列表
 * 
 * @author YouMingyang
 * 
 */
public class GroupListActivity extends ActivitySupport {
	private ImageLoader loader;
	private DisplayImageOptions options;

	private ProgressDialog dialog;
	private View noNetworkConnect;

	private GroupsListAdapter groupsListAdapter;
	private ArrayList<RosterGroup> groups;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_list_lay);
		init();
	}

	public void init() {
		final View activityTop = findViewById(R.id.activity_top);
		final TextView title = (TextView) activityTop.findViewById(R.id.title);
		title.setText("群组");
		
		noNetworkConnect=findViewById(R.id.no_network_connect);
		refreshData(null);

		this.loader = ImageLoader.getInstance();
		this.options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_photo)
				.showImageForEmptyUri(R.drawable.default_photo)
				.showImageOnFail(R.drawable.default_photo).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		final GridView groupsListView = (GridView) findViewById(R.id.groups_list);
		this.groups = new ArrayList<RosterGroup>();

		this.groupsListAdapter = new GroupsListAdapter(groups);
		groupsListView.setAdapter(groupsListAdapter);

	}

	private class GetDataTask extends
			AsyncTask<String, Void, ArrayList<RosterGroup>> {

		int resultCode = -1;

		@Override
		protected ArrayList<RosterGroup> doInBackground(String... params) {
			if (hasInternetConnected()) {
			} else {
				resultCode = 0;
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<RosterGroup> result) {
			dialog.dismiss();
			if (result != null) {
				groups.addAll(result);
				groupsListAdapter.notifyDataSetChanged();
			} else {
				switch (resultCode) {
				case 0:
					
					break;
				default:
					showToast("暂无数据", Toast.LENGTH_SHORT);
					break;
				}
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

	private class GroupsListAdapter extends BaseAdapter {

		private ArrayList<RosterGroup> groups;

		public GroupsListAdapter(ArrayList<RosterGroup> groups) {
			this.groups = groups;
		}

		@Override
		public int getCount() {
			return groups.size();
		}

		@Override
		public Object getItem(int position) {
			return groups.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.group_list_item, null);
			}
			final RosterGroup group = (RosterGroup) getItem(position);
			final TextView groupName = (TextView) convertView
					.findViewById(R.id.tag_group_name_val);
			groupName.setText(group.getName());
			// final ImageView groupAvater = (ImageView) convertView
			// .findViewById(R.id.list_item_icon);
			// final String uri = "drawables://"+R.drawable.;
			// loader.displayImage(uri, groupAvater, options);
			return convertView;
		}

	}

	public void refreshData(String param) {
		
		if (hasInternetConnected()) {
			showProgressDialog();
			noNetworkConnect.setVisibility(View.GONE);
			new GetDataTask().execute(param);
		} else {
			noNetworkConnect.setVisibility(View.VISIBLE);
		}
	}

	public void showProgressDialog() {
		dialog = getProgressDialog();
		dialog.setMessage("正在搜索群组");
		dialog.show();
	}
	
	public void OnClick(View view){
		switch(view.getId()){
		case R.id.no_network_connect:
			AppUtils.networkSet(this);
			break;
		}
	}

}
