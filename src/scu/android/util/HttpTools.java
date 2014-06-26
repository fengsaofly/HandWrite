package scu.android.util;

import scu.android.entity.AsyncHttpParams;

/**
 * Http相关工具类 包含异步上传和下载
 * 
 * @author YouMingyang
 */
public class HttpTools {

	public static void asyncUpload(final AsyncHttpParams asyncHttpParams) {
		
		new AsyncUploadTask().execute(asyncHttpParams);
	}

	public static void asyncDownload() {

	}

}
