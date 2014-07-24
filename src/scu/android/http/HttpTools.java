package scu.android.http;

import android.content.Context;

/**
 * Http相关工具类 包含异步上传和下载
 * 
 * @author YouMingyang
 */
public class HttpTools {

	/**
	 * 异步上传
	 * 
	 * @param asyncHttpParams
	 */
	public static void asyncUpload(final Context context,
			final AsyncHttpParams params) {
		new Thread(new AsyncUploadTask(context, params)).start();
	}

	/**
	 * 异步下载
	 * 
	 * @param asyncHttpParams
	 */
	public static void asyncDownload(final AsyncHttpParams params) {
		new AsyncDownloadTask().execute(params);
	}

}
