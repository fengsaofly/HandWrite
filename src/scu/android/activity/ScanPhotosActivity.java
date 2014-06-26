package scu.android.activity;

import java.util.ArrayList;

import scu.android.ui.PhotosViewPager;
import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 图片浏览
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class ScanPhotosActivity extends Activity {

	private PhotosViewPager viewPager;
	private ScanPageAdater adapter;
	private ArrayList<String> photos;
	private String action;
	private TextView pageIndex;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_photos);
		init();
	}

	// 初始化
	public void init() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_photo)
				.showImageForEmptyUri(R.drawable.default_photo)
				.showImageOnFail(R.drawable.default_photo).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imageLoader = ImageLoader.getInstance();
		final Intent intent = getIntent();
		final Bundle bundle = intent.getExtras();
		photos = bundle.getStringArrayList("photos");
		int index = bundle.getInt("index", 1);
		action = bundle.getString("action");
		toggleDel();

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

		viewPager = (PhotosViewPager) findViewById(R.id.viewPager);
		adapter = new ScanPageAdater(photos);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new PageListener());
		if (photos.size() > 3)
			viewPager.setOffscreenPageLimit(photos.size() - 2);

		pageIndex = (TextView) findViewById(R.id.pageIndex);
		pageIndex.setText(index + "/" + photos.size());

		viewPager.setCurrentItem(index - 1);
	}

	private class ScanPageAdater extends PagerAdapter {
		private LayoutInflater inflater;
		private ArrayList<String> photos;

		public ScanPageAdater(ArrayList<String> photos) {
			this.photos = photos;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		// 动态删除必须添加
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return photos.size();
		}

		@Override
		public Object instantiateItem(View view, int position) {
			final View photoLayout = inflater.inflate(R.layout.scan_photo_item,
					null);
			final PhotoView photo = (PhotoView) photoLayout
					.findViewById(R.id.photo);
			final ProgressBar loading = (ProgressBar) photoLayout
					.findViewById(R.id.loading);
			/* "file:///" + photos.get(position) */
			final String uri = photos.get(position);
			final boolean isFromNetwork = (uri.startsWith("http://")) ? true
					: false;
			if (isFromNetwork) {
				imageLoader.displayImage(uri, photo, options,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								loading.setProgress(0);
								loading.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								loading.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								loading.setVisibility(View.GONE);
								// BitmapUtils.saveBitmap(activity, loadedImage,
								// imgSaveDir);
							}
						}, new ImageLoadingProgressListener() {
							@Override
							public void onProgressUpdate(String imageUri,
									View view, int current, int total) {
								loading.setProgress(Math.round(100.0f * current
										/ total));
							}
						});
			} else {
				imageLoader.displayImage(uri, photo, options);
			}

			((ViewPager) view).addView(photoLayout, 0);
			return photoLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int position) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			pageIndex.setText((position + 1) + "/" + photos.size());
		}

	}

	public void toggleDel() {
		if (action != null) {
			findViewById(R.id.delete_photo).setVisibility(View.VISIBLE);
		}
	}

	public void OnClick(View view) {
		switch (view.getId()) {
		case R.id.delete_photo:
			deletePhoto();
			break;
		}
	}

	public void deletePhoto() {
		final int index = viewPager.getCurrentItem();
		Dialog alert = new AlertDialog.Builder(this).setTitle("破题")
				.setMessage("删除这张照片?")
				.setPositiveButton("确定", new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						photos.remove(index);
						adapter.notifyDataSetChanged();
						Intent del = new Intent();
						del.setAction(action);
						del.putExtra("photoIndex", index);
						sendBroadcast(del);
						if (photos.size() <= 0)
							finish();
						else {
							if (index >= 1) {
								if (index < photos.size())
									pageIndex.setText((index + 1) + "/"
											+ photos.size());
								else
									pageIndex.setText(index + "/"
											+ photos.size());
							} else {
								pageIndex.setText(1 + "/" + photos.size());
							}
						}
					}
				}).setNegativeButton("取消", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
		alert.show();
	}

	@Override
	protected void onStop() {
		imageLoader.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		imageLoader.clearMemoryCache();
		imageLoader.clearDiskCache();
		// viewPager.destroyDrawingCache();
		super.onDestroy();
	}

}
