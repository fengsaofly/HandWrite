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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/*
 * 图片浏览
 */
public class ScanPhotosActivity extends Activity {

	private PhotosViewPager viewPager;
	private ScanPageAdater adapter;
	private ArrayList<String> photos;
	private Intent intent;

	private TextView pageIndex;

	protected ImageLoader loader;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_photos);
		init();
	}

	// 初始化
	public void init() {
		intent = getIntent();
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.question)
				.showImageForEmptyUri(R.drawable.question).cacheInMemory()
				.cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.build();

		photos = intent.getStringArrayListExtra("photos");
		int index = intent.getIntExtra("index", 1);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

		viewPager = (PhotosViewPager) findViewById(R.id.viewPager);
		adapter = new ScanPageAdater(photos);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new PageListener());

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

			loader.displayImage("file:///" + photos.get(position), photo,
					options, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted() {
							loading.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(FailReason failReason) {
							loading.setVisibility(View.GONE);
							photo.setImageResource(R.drawable.question);
						}

						@Override
						public void onLoadingComplete(Bitmap loadedImage) {
							loading.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled() {
						}
					});

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 添加刪除
		String action = intent.getAction();
		if (action != null
				&& (action.equals("scu.android.activiy.IssueQuestionActivity") || action
						.equals("scu.android.activiy.ReplyQuestionActivity"))) {
			getMenuInflater().inflate(R.menu.scan_photos_actionbar_menu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_photo:
			deletePhoto();
			break;
		}
		return true;
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
						del.setAction("scu.android.ui.ScanPhotosActivity");
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
		loader.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// 清空cache
		loader.clearMemoryCache();
		loader.clearDiscCache();
		super.onDestroy();
	}

}
