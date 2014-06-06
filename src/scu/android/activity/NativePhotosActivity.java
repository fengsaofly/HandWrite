package scu.android.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import scu.android.util.AppUtils;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/*
 * 浏览手机图库
 */
public class NativePhotosActivity extends Activity {

	private GridView photosView;
	private PhotosAdapter adapter;

	private HashMap<String, ArrayList<String>> photos;// 手机图库(包含父目录)
	private ArrayList<Photo> bitmaps;// 图库
	private ArrayList<String> selectedPhotos;
	private Intent intent;

	private int width;
	private ImageLoader loader;
	private DisplayImageOptions options;
	private int availNumber;
	// /////////////////////////////////////////////////////////
	private PopupWindow parentsWindow;
	private ArrayList<Parent> items;
	private TextView selectParent;
	private TextView selectNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_native_photos);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.nativephotos_actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.selectComplete:
			intent.putStringArrayListExtra("photos", getSelectedPhotos());
			setResult(Activity.RESULT_OK, intent);
			NativePhotosActivity.this.finish();
			Log.i("Complete", "selectComplete");
			break;
		}
		return true;
	}

	// 初始化
	public void init() {
		intent = getIntent();
		getActionBar().setDisplayHomeAsUpEnabled(true);

		availNumber = intent.getIntExtra("availNumber", 4);
		width = AppUtils.getDefaultPhotoWidth(this);

		photos = getPhotos();
		if (photos.size() > 0) {
			bitmaps = new ArrayList<Photo>();
			selectedPhotos = new ArrayList<String>();
			// ////////////////////////////////////////////
			items = new ArrayList<NativePhotosActivity.Parent>();

			@SuppressWarnings("rawtypes")
			Iterator iterator = photos.entrySet().iterator();
			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Entry entry = (Entry) iterator.next();
				String parent = (String) entry.getKey();
				@SuppressWarnings("unchecked")
				ArrayList<String> paths = (ArrayList<String>) entry.getValue();
				for (String path : paths) {
					bitmaps.add(new Photo(path, false));
				}
				String photo = paths.get(0);
				int number = paths.size();
				Parent item = new Parent(photo, parent, number);
				items.add(item);
			}

			items.add(0,
					new Parent(bitmaps.get(0).path, "所有图片", bitmaps.size()));// 所有图片

			photosView = (GridView) findViewById(R.id.photosView);
			adapter = new PhotosAdapter(this, bitmaps);
			photosView.setAdapter(adapter);

			loader = ImageLoader.getInstance();
			options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.default_photo)
					.showImageForEmptyUri(R.drawable.default_photo)
					.cacheInMemory().cacheOnDisc()
					.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

			selectParent = (TextView) findViewById(R.id.selectParent);
			selectNumber = (TextView) findViewById(R.id.selectNumber);
			selectNumber.setText("已选" + selectedPhotos.size() + "张");
		} else {
			Toast.makeText(this, "没有发现任何图片哦...", Toast.LENGTH_SHORT).show();
		}
	}

	// 获取手机图库
	public HashMap<String, ArrayList<String>> getPhotos() {
		HashMap<String, ArrayList<String>> photos = new HashMap<String, ArrayList<String>>();
		Uri photosUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver resolver = getContentResolver();
		// 只查询jpeg和png的图片
		Cursor cursor = resolver.query(photosUri, null,
				MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=?",
				new String[] { "image/jpeg", "image/png" },
				MediaStore.Images.Media.DATE_MODIFIED);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				// 获取图片的路径
				String path = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				String parent = new File(path).getParentFile().getName();
				if (photos.containsKey(parent)) {
					photos.get(parent).add(path);
				} else {
					ArrayList<String> subPhotos = new ArrayList<String>();
					subPhotos.add(path);
					photos.put(parent, subPhotos);
				}
			}
		}
		return photos;
	}

	private class PhotosAdapter extends BaseAdapter {
		private Activity activity;
		private ArrayList<Photo> bitmaps;

		public PhotosAdapter(Activity activity, ArrayList<Photo> bitmaps) {
			this.activity = activity;
			this.bitmaps = bitmaps;
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
				convertView = LayoutInflater.from(
						activity.getApplicationContext()).inflate(
						R.layout.native_photo_item, null);
			}
			final Photo photo = bitmaps.get(position);
			ImageView thumbnail = ((ImageView) convertView
					.findViewById(R.id.thumbnail));
			AppUtils.setViewSize(thumbnail, width, width);// 设置图片大小
			loader.displayImage("file:///" + photo.path, thumbnail, options);
			final ImageView back = ((ImageView) convertView
					.findViewById(R.id.back));
			AppUtils.setViewSize(back, width, width);// 设置和后面的图片一样大小
			back.setVisibility(photo.selected ? View.VISIBLE : View.INVISIBLE);
			final CheckBox select = (CheckBox) convertView
					.findViewById(R.id.select);
			select.setChecked(photo.selected);
			final int index = position;
			select.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (selectedPhotos.size() < availNumber) {
						boolean status = select.isChecked();
						bitmaps.get(index).selected = status;
						if (status) {
							back.setVisibility(View.VISIBLE);
							selectedPhotos.add(photo.path);
						} else {
							back.setVisibility(View.INVISIBLE);
							selectedPhotos.remove(photo.path);
						}
					} else {
						if (bitmaps.get(index).selected) {
							bitmaps.get(index).selected = false;
							back.setVisibility(View.INVISIBLE);
							selectedPhotos.remove(photo.path);
						} else {
							select.setEnabled(false);
							Toast.makeText(NativePhotosActivity.this,
									"最多只能选择" + availNumber + "张图片",
									Toast.LENGTH_SHORT).show();
						}
					}
					selectNumber.setText("已选" + selectedPhotos.size() + "张");
				}
			});
			return convertView;
		}
	}

	private class Photo {
		boolean selected;
		String path;

		public Photo(String path, boolean selected) {
			super();
			this.selected = selected;
			this.path = path;
		}

	}

	// 获取选择的图片
	public ArrayList<String> getSelectedPhotos() {
		return selectedPhotos;
	}

	@Override
	protected void onStop() {
		if (loader != null)
			loader.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// 清空cache
		if (loader != null) {
			loader.clearMemoryCache();
			loader.clearDiscCache();
		}
		super.onDestroy();
	}

	// ///////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////
	public void OnClick(View view) {
		switch (view.getId()) {
		case R.id.selectParent:
			select();
			break;
		}
	}

	private class ParentsAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<Parent> items;

		public ParentsAdapter(Context context, ArrayList<Parent> items) {
			this.context = context;
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.native_photos_parent_item, null);
			}
			Parent item = items.get(position);
			ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
			android.view.ViewGroup.LayoutParams params = photo
					.getLayoutParams();
			params.width = params.height = width / 2;
			photo.setLayoutParams(params);
			loader.displayImage("file:///" + item.photo, photo, options);
			((TextView) convertView.findViewById(R.id.parent))
					.setText(item.parent);
			((TextView) convertView.findViewById(R.id.number))
					.setText(item.number + "张");
			return convertView;
		}
	}

	// 父目录
	private class Parent {
		String photo;// 封面
		String parent;// 目录名
		int number;// 图片数目

		public Parent(String photo, String parent, int number) {

			this.photo = photo;
			this.parent = parent;
			this.number = number;
		}

	}

	// 选择目录监听器
	private class SelectListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {

			parentsWindow.dismiss();
			Parent item = items.get(position);
			selectParent.setText(item.parent);
			bitmaps.clear();
			if (position == 0) {
				@SuppressWarnings("rawtypes")
				Iterator iterator = photos.entrySet().iterator();
				while (iterator.hasNext()) {
					@SuppressWarnings("rawtypes")
					Entry entry = (Entry) iterator.next();
					@SuppressWarnings("unchecked")
					ArrayList<String> paths = (ArrayList<String>) entry
							.getValue();
					for (String path : paths) {
						bitmaps.add(new Photo(path, selectedPhotos
								.contains(path)));
					}
				}
			} else {
				for (String photo : photos.get(item.parent)) {
					bitmaps.add(new Photo(photo, selectedPhotos.contains(photo)));
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	// 选择目录
	public void select() {
		View contentView = getLayoutInflater().inflate(
				R.layout.native_photos_parent, null);
		parentsWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		ListView parent = (ListView) contentView.findViewById(R.id.parents);
		AppUtils.setViewSize(parent, LayoutParams.MATCH_PARENT, width * 4);
		parent.setAdapter(new ParentsAdapter(this, items));
		parent.setOnItemClickListener(new SelectListener());
		parentsWindow.setBackgroundDrawable(new ColorDrawable());
		parentsWindow.showAtLocation(findViewById(R.id.parent), Gravity.CENTER,
				0, 0);
		contentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (parentsWindow.isShowing()) {
					parentsWindow.dismiss();
				}
				return false;
			}
		});
	}
}
