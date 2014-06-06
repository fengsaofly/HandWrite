package scu.android.ui;

import java.util.ArrayList;

import scu.android.activity.ScanPhotosActivity;
import scu.android.util.AppUtils;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/*
 * 
 */
public class PhotosAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<String> bitmaps;
	private ImageLoader loader;
	private DisplayImageOptions options;
	private int width;

	public PhotosAdapter(Activity activity, ArrayList<String> bitmaps) {
		this.activity = activity;
		this.bitmaps = bitmaps;
		this.loader = ImageLoader.getInstance();
		this.options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.default_photo)
				.showImageForEmptyUri(R.drawable.default_photo).cacheInMemory()
				.cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.build();
		this.width = AppUtils.getDefaultPhotoWidth(activity);
	}

	public int getCount() {
		return bitmaps.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(activity.getApplicationContext())
					.inflate(R.layout.thumbnail_item, null);
		}
		String photo = bitmaps.get(position);
		ImageView thumbnail = ((ImageView) convertView
				.findViewById(R.id.thumbnail));

		AppUtils.setViewSize(thumbnail, width, width);
		loader.displayImage("file:///" + photo, thumbnail, options);
		final int index = position;
		thumbnail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, ScanPhotosActivity.class);
				intent.putStringArrayListExtra("photos", bitmaps);
				intent.putExtra("index", index + 1);
				activity.startActivity(intent);
			}
		});
		return convertView;
	}

	public void clear() {
		loader.clearDiscCache();
		loader.clearMemoryCache();
	}

}
