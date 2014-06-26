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
 * 图片适配器
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class PhotosAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<String> bitmaps;
	private int columnNum;
	private String action;
	private String imgSaveDir;

	private ImageLoader loader;
	private DisplayImageOptions options;

	public PhotosAdapter(Activity activity, ArrayList<String> bitmaps) {
		init(activity, bitmaps);
	}

	public PhotosAdapter(Activity activity, ArrayList<String> bitmaps,
			String imgSaveDir) {
		init(activity, bitmaps);
		this.imgSaveDir = imgSaveDir;
	}

	public void init(Activity activity, ArrayList<String> bitmaps) {
		this.activity = activity;
		this.bitmaps = bitmaps;

		this.loader = ImageLoader.getInstance();
		this.options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_photo)
				.showImageForEmptyUri(R.drawable.default_photo)
				.showImageOnFail(R.drawable.default_photo).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		this.columnNum = 3;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
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
		final ImageView view = ((ImageView) convertView
				.findViewById(R.id.thumbnail));
		showImage(bitmaps.get(position), view);
		final int index = position;
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, ScanPhotosActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("photos", bitmaps);
				bundle.putInt("index", index + 1);
				if(action!=null){
				bundle.putString("action",action);
				}
				intent.putExtras(bundle);
				activity.startActivity(intent);
			}
		});
		return convertView;
	}

	public void showImage(final String imageDir, final ImageView imageView) {
		final int width = AppUtils.getDefaultPhotoWidth(activity, columnNum);
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
					// BitmapUtils.saveBitmap(activity, loadedImage,
					// imgSaveDir);
				}
			});
		} else {
			loader.displayImage(imageDir, imageView, options);
		}
	}

	public void clear() {
		loader.clearDiskCache();
		loader.clearMemoryCache();
	}

}
