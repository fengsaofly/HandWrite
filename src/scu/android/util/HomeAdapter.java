package scu.android.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import scu.android.util.ChatMsgViewAdapter.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.demo.note.R;

public class HomeAdapter extends BaseAdapter {

	Context context = null;
	Activity activity = null;
	List<Map<String, Object>> list = null;

	// ViewHolder holder;

	public HomeAdapter(Context context, Activity act,
			List<Map<String, Object>> list) {
		super();

		this.context = context;
		this.activity = act;
		this.list = list;

	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();

			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_for_frag_recent, null);
			// holder = new ViewHolder();
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.home_name);

			viewHolder.content = (TextView) convertView
					.findViewById(R.id.home_content);

			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.home_icon);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.name.setText(list.get(position).get("name").toString());

		if(list.get(position).get("content").toString().toLowerCase().contains("amr")){
			viewHolder.content.setText("新的语音信息");
		}
		else if(list.get(position).get("content").toString().toLowerCase().contains("jpg")||list.get(position).get("content").toString().toLowerCase().contains("png")){
			viewHolder.content.setText("新的图片信息");
		}
		else{
			viewHolder.content
			.setText(list.get(position).get("content").toString());
		}
		
		int type = Integer.parseInt(list.get(position).get("type").toString());
		switch (type) {
		case 0:
			viewHolder.icon.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.friend_icon_sample));
			break;
		case 1:
			viewHolder.icon.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.message_note));
			break;
		case 2:
			viewHolder.icon.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.message_poti));
			break;
		case 3:
			viewHolder.icon.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.message_dongtai));
			break;
		}

		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return list.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	public class ViewHolder {

		TextView name;
		TextView content;
		ImageView icon;

	}

}
