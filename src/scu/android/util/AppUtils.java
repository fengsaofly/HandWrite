package scu.android.util;

import java.io.File;
import java.util.Date;

import scu.android.activity.DoodleBoardActivity;
import scu.android.activity.NativePhotosActivity;
import scu.android.demo.Demo;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

public class AppUtils {

	public static final String CAMERA_PHOTO_DIR = "ConquerQuestion/CurrentUser/NewQuestion/image/camera";
	public static final String DOODLE_DIR = "ConquerQuestion/CurrentUser/NewQuestion/image/doodle";
	public static final String HANDWRITE_DIR = "ConquerQuestion/CurrentUser/NewQuestion/image/handwrite";

	public final static int SYS_CAMEAR = 1;
	public final static int PHONE_PICTURES = 2;
	public final static int HANDWRITE_BOARD = 3;
	public final static int DOODLE_BOARD = 4;

	public final static int ISSUE_QUESTION = 11;
	public final static int ISSUE_QUESTION_REPLY = 12;

	// 调用系统相机
	public static String sysCamera(Activity activity) {
		String imgName = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory() + "/"
					+ CAMERA_PHOTO_DIR);
			if (!dir.exists())
				dir.mkdirs();
			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			imgName = System.currentTimeMillis() + ".png";
			File f = new File(dir, imgName);
			Uri u = Uri.fromFile(f);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
			activity.startActivityForResult(intent, SYS_CAMEAR);
		}
		return imgName;
	}

	// 调用手机图库
	public static void phonePictures(Activity activity, int availNumber) {
		Intent selectPhoto = new Intent(activity, NativePhotosActivity.class);
		selectPhoto.putExtra("availNumber", availNumber);
		activity.startActivityForResult(selectPhoto, AppUtils.PHONE_PICTURES);
	}

	// 调用手写功能
	public static void hwBoard(Activity activity) {
		Intent intent = new Intent(activity, Demo.class);
		activity.startActivityForResult(intent, AppUtils.HANDWRITE_BOARD);
	}

	// 调用手写功能
	public static void doodleBoard(Activity activity) {
		Intent intent = new Intent(activity, DoodleBoardActivity.class);
		activity.startActivityForResult(intent, AppUtils.DOODLE_BOARD);
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

	//

}
