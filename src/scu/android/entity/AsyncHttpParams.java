package scu.android.entity;

import java.io.File;
import java.util.HashMap;

/**
 * Http上传参数
 * 
 * @author YouMingyang
 * 
 */
public class AsyncHttpParams {

	String url;// 上传网址
	HashMap<String, String> params;// 文本参数
	HashMap<String, File> files;// 上传文件

	public AsyncHttpParams(String url, HashMap<String, String> params,
			HashMap<String, File> files) {
		super();
		this.url = url;
		this.params = params;
		this.files = files;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HashMap<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}

	public HashMap<String, File> getFiles() {
		return files;
	}

	public void setFiles(HashMap<String, File> files) {
		this.files = files;
	}

}
