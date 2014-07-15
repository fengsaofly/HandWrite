package scu.android.ui;

import java.util.ArrayList;
import scu.android.activity.ScanPhotosActivity;
import scu.android.util.AppUtils;
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
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 图片适配器：显示小图
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class PhotosAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<String> thumbnails;// 缩略图
	private ArrayList<String> bitmaps;// 原图
	private int columnNum;// 列数
	private String action;// 用于图片删除

	private ImageLoader loader;
	private DisplayImageOptions options;

	public PhotosAdapter(Activity activity, ArrayList<String> thumbnails) {
		init(activity, thumbnails);
		this.bitmaps = thumbnails;
	}

	/**
	 * 
	 * @param activity
	 * @param thumbnails
	 *            缩略图路径
	 * @param bitmaps
	 *            原图路径
	 */
	public PhotosAdapter(Activity activity, ArrayList<String> thumbnails,
			ArrayList<String> bitmaps) {
		if (thumbnails != null && thumbnails.size() > 0) {
			init(activity, thumbnails);
		} else {
			init(activity, bitmaps);
		}
		this.bitmaps = bitmaps;
	}

	public void init(Activity activity, ArrayList<String> thumbnails) {
		this.activity = activity;
		this.thumbnails = thumbnails;

		this.loader = ImageLoader.getInstance();
		this.options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_photo)
				.showImageForEmptyUri(R.drawable.default_photo)
				.showImageOnFail(R.drawable.default_photo).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		this.columnNum = 3;
		// this.columnNum = thumbnails.size() >= 3 ? 3 : thumbnails.size();
		// if (this.columnNum == 0)
		// this.columnNum = 1;
	}

	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}

	public void setAction(String action) {
		this.action = action;
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

	private class PhotosViewHolder {
		ImageView thumbnail;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		PhotosViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity.getApplicationContext())
					.inflate(R.layout.thumbnail_item, null);
			holder = new PhotosViewHolder();
			holder.thumbnail = ((ImageView) convertView
					.findViewById(R.id.thumbnail));
			convertView.setTag(holder);
		} else {
			holder = (PhotosViewHolder) convertView.getTag();
		}
		showImage((String) getItem(position), holder.thumbnail);
		final int index = position;

		holder.thumbnail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, ScanPhotosActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("photos", bitmaps);
				bundle.putInt("index", index + 1);
				if (action != null) {
					bundle.putString("action", action);
				}
				intent.putExtras(bundle);
				activity.startActivity(intent);
			}
		});
		return convertView;
	}

	// 显示图片
	public void showImage(final String imageDir, final ImageView imageView) {
		final int width = AppUtils.getDefaultPhotoWidth(activity, columnNum);
		// final int height = (columnNum == 1 ? 2 * width : width);
		AppUtils.setViewSize(imageView, width, width);
		final String uri = imageDir;
		final boolean isFromNetwork = (uri.startsWith("http://")) ? true
				: false;
		if (isFromNetwork) {

			loader.loadImage(uri, options, new SimpleImageLoadingListener() {

				@Override
				public void onLoadingComplete(String imageUri, View view,
						Bitmap loadedImage) {
					imageView.setImageBitmap(loadedImage);
				}
			});
		} else {
			loader.displayImage(imageDir, imageView, options);
		}

	}

}
