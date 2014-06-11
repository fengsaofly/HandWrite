package scu.android.ui;

import java.util.ArrayList;
import scu.android.util.AppUtils;
import scu.android.util.BitmapUtils;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.demo.note.R;

/**
 * 缩略图
 * 
 */
public class ThumbnailsAdapter extends BaseAdapter {

	private Activity activity;// 所在activity
	private ArrayList<String> thumbnails;// 图片路径
	private int width;// 缩略图大小

	public ThumbnailsAdapter(Activity activity, ArrayList<String> thumbnails) {
		this.activity = activity;
		this.thumbnails = thumbnails;
		this.width = (AppUtils.getWindowMetrics(activity).widthPixels - 10) / 4;
	}

	public int getCount() {
		return thumbnails.size();
	}

	public Object getItem(int position) {
		return thumbnails.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(activity.getApplicationContext())
					.inflate(R.layout.thumbnail_item, null);
		}
		ImageView thumbnail = (ImageView) convertView
				.findViewById(R.id.thumbnail);
		thumbnail.setImageBitmap(BitmapUtils.getThumbnails(activity,
				(String) getItem(position), width, width));
		AppUtils.setThumbnailSize(thumbnail, width, width);
		return convertView;
	}

}
