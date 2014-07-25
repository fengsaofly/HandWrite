package scu.android.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import scu.android.activity.DoodleBoardActivity;
import scu.android.activity.ImageCropperActivity;
import scu.android.activity.NativePhotosActivity;
import scu.android.activity.ScanPhotosActivity;
import scu.android.dao.Question;
import scu.android.dao.Reply;
import scu.android.handwrite.HwActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class AppUtils {

	// 调用系统相机
	public static String sysCamera(Activity activity) {
		String imgPath = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory() + "/" + Constants.CAMERA_PHOTO_DIR);
			if (!dir.exists())
				dir.mkdirs();
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			String imgName = System.currentTimeMillis() + ".png";
			File f = new File(dir, imgName);
			Uri u = Uri.fromFile(f);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
			activity.startActivityForResult(intent, Constants.SYS_CAMEAR);
			imgPath = f.getAbsolutePath();
		}
		return imgPath;
	}

	// 调用手机图库
	public static void phonePictures(Activity activity, int availNumber) {
		Intent selectPhoto = new Intent(activity, NativePhotosActivity.class);
		selectPhoto.putExtra("availNumber", availNumber);
		activity.startActivityForResult(selectPhoto, Constants.PHONE_PICTURES);
	}

	// 调用手写功能
	public static void hwBoard(Activity activity) {
		Intent intent = new Intent(activity, HwActivity.class);
		activity.startActivityForResult(intent, Constants.HANDWRITE_BOARD);
	}

	// 调用手写功能
	public static void doodleBoard(Activity activity) {
		Intent intent = new Intent(activity, DoodleBoardActivity.class);
		activity.startActivityForResult(intent, Constants.DOODLE_BOARD);
	}

	// 获取手机屏幕尺寸
	public static DisplayMetrics getWindowMetrics(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

	/*
	 * 以后修改为Calendar
	 */
	@SuppressWarnings("deprecation")
	public static String timeToNow(long time) {
		Date date = new Date(time * 1000);
		String elapsed = null;
		Date now = new Date();
		int nDay = now.getDate();
		int nHour = now.getHours();
		int nSecond = now.getSeconds();
		int nMinute = now.getMinutes();
		int dDay = date.getDate();
		int dHour = date.getHours();
		int dMinute = date.getMinutes();
		int dSecond = date.getSeconds();
		if (nDay == dDay) {
			if (nHour == dHour) {
				if (nMinute == dMinute) {
					if (Math.abs(nSecond - dSecond) == 0) {
						elapsed = "刚刚";
					} else {
						elapsed = Math.abs(nSecond - dSecond) + "秒前";
					}
				} else {
					if (nSecond >= dSecond || Math.abs(nMinute - dMinute) > 1) {
						elapsed = Math.abs(nMinute - dMinute) + "分钟前";
					} else {
						elapsed = 60 - Math.abs(nSecond - dSecond) + "秒前";
					}
				}
			} else {
				if (nMinute >= dMinute || Math.abs(nHour - dHour) > 1) {
					elapsed = Math.abs(nHour - dHour) + "小时前";
				} else {
					elapsed = 60 - Math.abs(nMinute - dMinute) + "分钟前";
				}
			}
		} else {
			// if (Math.abs(nDay - dDay) == 1) {
			// // if (nHour >= dHour) {
			// elapsed = "1天前";
			// // } else {
			// // elapsed = Math.abs(nHour + 24 - dHour) + "小时前";
			// // }
			// } else {
			elapsed = (date.getMonth() + 1) + "月" + (dDay) + "日";
			// }
		}
		return elapsed;
		// return date.toLocaleString();
	}

	// 设置图片缩略图大小
	public static void setViewSize(View view, int width, int height) {
		LayoutParams params = (LayoutParams) view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);
	}

	public static int getDefaultPhotoWidth(Activity activity, int columnNum) {
		return (getWindowMetrics(activity).widthPixels - (columnNum - 1) * 2) / columnNum;
	}

	/**
	 * 手机截图
	 * 
	 * @param activity
	 * @param imgPath
	 */
	public static void sysCrop(Activity activity, String imgPath) {
		Intent intent = new Intent(activity, ImageCropperActivity.class);
		intent.putExtra("imgPath", imgPath);
		activity.startActivityForResult(intent, Constants.SYS_CROP);
	}

	public static boolean delete(String path) {
		File file = new File(path);
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					delete(files[i].getAbsolutePath()); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 获取当前网络连接
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isNetworkConnect(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			switch (networkInfo.getType()) {
			case ConnectivityManager.TYPE_MOBILE:
			case ConnectivityManager.TYPE_WIFI:
				return true;
			default:
				return false;
			}
		}
		return false;
	}

	/**
	 * 跳转到网络设置
	 * 
	 * @param activity
	 */
	public static void networkSet(Activity activity) {
		Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		activity.startActivity(intent);
	}

	/**
	 * 解码加密的字符串
	 * 
	 * @param content
	 * @return
	 */
	public static String decodeString(String content) {
		if (content != null) {
			return new String(Base64.decode(content, android.util.Base64.DEFAULT));
		}
		return null;
	}

	/**
	 * 安全策略
	 */
	public static void setStrictMode() {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	// public static void getCurLocation(Context context) {
	// double lat = 0.0;
	// double lng = 0.0;
	//
	// LocationManager locationManager = (LocationManager)
	// context.getSystemService(Context.LOCATION_SERVICE);
	// LocationListener locationListener = new LocationListener() {
	// @Override
	// public void onStatusChanged(String provider, int status,
	// Bundle extras) {
	//
	// }
	//
	// @Override
	// public void onProviderEnabled(String provider) {
	//
	// }
	//
	// @Override
	// public void onProviderDisabled(String provider) {
	//
	// }
	//
	// @Override
	// public void onLocationChanged(Location location) {
	// if (location != null) {
	// Log.e("Map","Location changed : Lat: "+ location.getLatitude() +
	// " Lng: "+ location.getLongitude());
	// }
	// }
	// };
	// if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	// Location location =
	// locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	// if (location != null) {
	// lat = location.getLatitude();
	// lng = location.getLongitude();
	// Log.d("MAP", "Lat:" + lat + ",lng:" + lng);
	// }
	// } else {
	// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,
	// 0, locationListener);
	// if(isNetworkConnect(context)){
	// Location location =
	// locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	// if (location != null) {
	// lat = location.getLatitude();
	// lng = location.getLongitude();
	// Log.d("MAP", "Lat:" + lat + ",lng:" + lng);
	// }
	// }
	// }
	// }

	/**
	 * 
	 * @param context
	 * @param loader
	 * @param options
	 * @param imgView
	 * @param thumbnailUri
	 * @param imgUri
	 */
	public static void disImg(final Context context, final ImageLoader loader, final DisplayImageOptions options, final ImageView imgView, final String thumbnailUri, final String imgUri) {
		boolean isFromNetwork = (thumbnailUri.startsWith("http://")) ? true : false;
		if (isFromNetwork) {
			loader.loadImage(thumbnailUri, options, new SimpleImageLoadingListener() {

				@Override
				public void onLoadingFailed(String thumbnailUri, View view, FailReason failReason) {
					imgView.setImageResource(R.drawable.default_photo);
				}

				@Override
				public void onLoadingStarted(String thumbnailUri, View view) {
					imgView.setImageResource(R.drawable.default_photo);
				}

				@Override
				public void onLoadingComplete(String thumbnailUri, View view, Bitmap loadedImage) {
					imgView.setImageBitmap(loadedImage);
				}
			});
		} else {
			loader.displayImage(thumbnailUri, imgView, options);
		}
		imgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ScanPhotosActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ArrayList<String> photos = new ArrayList<String>();
				photos.add(imgUri);
				Bundle data = new Bundle();
				data.putStringArrayList("photos", photos);
				data.putInt("type", Constants.QUESTION);
				// intent.putExtra("photos", photos);
				intent.putExtras(data);
				context.startActivity(intent);
			}
		});

	}

	public static void disImg(final Context context, final ImageLoader loader, final DisplayImageOptions options, final ImageView imgView, final int type, final Object object) {
		switch (type) {
		case Constants.QUESTION:
			Question mQuestion = (Question) object;
			
			break;
		case Constants.REPLY:
			Reply mReply = (Reply) object;
			break;
		}
	}
}
