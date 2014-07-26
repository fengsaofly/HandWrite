package scu.android.ui;

import java.util.ArrayList;
import scu.android.activity.ScanPhotosActivity;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 图片适配器：显示小图
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class SelectPhotoAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<String> photos;
	private String action;

	private ImageLoader loader;
	private DisplayImageOptions options;

	public SelectPhotoAdapter(Activity activity, ArrayList<String> photos) {
		
		initImageLoader();
		this.activity=activity;
		this.photos=photos;
		this.photos.add("drawable://" + R.drawable.find_add_btn);
	}
	
	public void initImageLoader() {
		this.loader = ImageLoader.getInstance();
		this.options = new DisplayImageOptions.Builder().cacheInMemory(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getCount() {
		return photos.size();
	}

	public Object getItem(int position) {
		return photos.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private class PhotosViewHolder {
		ImageView thumbnail;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		PhotosViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.thumbnail_item, null);
			holder = new PhotosViewHolder();
			holder.thumbnail = ((ImageView) convertView.findViewById(R.id.thumbnail));
			convertView.setTag(holder);
		} else {
			holder = (PhotosViewHolder) convertView.getTag();
		}
		showImage((String) getItem(position), holder.thumbnail);
		final int index = position;
		holder.thumbnail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(getCount()>1&&index==0){
					Intent intent = new Intent(activity, ScanPhotosActivity.class);
					Bundle bundle = new Bundle();
					ArrayList<String> photo=new ArrayList<String>();
					photo.add(photos.get(0));
					bundle.putStringArrayList("photos",photo);
					bundle.putInt("index", index + 1);
					if (action != null) {
						bundle.putString("action", action);
					}
					intent.putExtras(bundle);
					activity.startActivity(intent);
				}else{
					activity.sendBroadcast(new Intent(Constants.SELECT_PHOTO));
				}
			}
		});
		return convertView;
	}

	// 显示图片
	public void showImage(String imageDir,ImageView imageView) {
		int width = AppUtils.getDefaultPhotoWidth(activity, 4);
		AppUtils.setViewSize(imageView, width, width);
		loader.displayImage(imageDir, imageView, options);
	}

}
