package scu.android.util;

import java.io.File;
import java.util.Date;

import org.jivesoftware.smack.util.Base64;

import scu.android.activity.DoodleBoardActivity;
import scu.android.activity.ImageCropperActivity;
import scu.android.activity.NativePhotosActivity;
import scu.android.handwrite.HwActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class AppUtils {

	// 调用系统相机
	public static String sysCamera(Activity activity) {
		String imgPath = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory() + "/"
					+ Constants.CAMERA_PHOTO_DIR);
			if (!dir.exists())
				dir.mkdirs();
			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
	public static String timeToNow(Date date) {
		String elapsed = null;
		Date now = new Date();
		int nDay = now.getDay();
		int nHour = now.getHours();
		int nSecond = now.getSeconds();
		int nMinute = now.getMinutes();
		int dDay = date.getDay();
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
			if (Math.abs(nDay - dDay) == 1) {
				if (nHour >= dHour) {
					elapsed = "1天前";
				} else {
					elapsed = Math.abs(nHour - dHour) + "小时前";
				}
			} else {
				elapsed = (date.getMonth() + 1) + "月" + (dDay + 1) + "日";
			}
		}
		return elapsed;
	}

	// 设置图片缩略图大小
	public static void setViewSize(View view, int width, int height) {
		LayoutParams params = (LayoutParams) view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);
	}

	public static int getDefaultPhotoWidth(Activity activity, int columnNum) {
		return (getWindowMetrics(activity).widthPixels - (columnNum - 1) * 2)
				/ columnNum;
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
	public static boolean isNetworkConnect(Activity activity) {
		ConnectivityManager connectivityManager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
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
			return new String(Base64.decode(content,
					android.util.Base64.DEFAULT));
		}
		return null;
	}

}
