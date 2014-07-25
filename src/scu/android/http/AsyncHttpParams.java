package scu.android.http;

import java.util.LinkedList;

import org.apache.http.NameValuePair;

/**
 * Http参数
 * 
 * @author YouMingyang
 * 
 */
public class AsyncHttpParams {

	private String baseUrl;// 网址
	private LinkedList<NameValuePair> textParams;
	private LinkedList<NameValuePair> fileParams;

	// HashMap<String, String> params;// 文本参数
	// HashMap<String, File> files;// 文件参数
	private String action;// 接收信息action

	public AsyncHttpParams(String baseUrl, String action) {
		super();
		this.baseUrl = baseUrl;
		this.action = action;
	}

	public AsyncHttpParams(String baseUrl,
			LinkedList<NameValuePair> textParams,
			LinkedList<NameValuePair> fileParams, String action) {
		super();
		this.baseUrl = baseUrl;
		this.textParams = textParams;
		this.fileParams = fileParams;
		this.action = action;
	}

	// public AsyncHttpParams(String url, String action) {
	// super();
	// this.url = url;
	// this.action = action;
	// }
	//
	//

	// public AsyncHttpParams(String url, HashMap<String, String> params,
	// String action) {
	// super();
	// this.url = url;
	// this.params = params;
	// this.action = action;
	// }

	// public AsyncHttpParams(String url, HashMap<String, String> params,
	// HashMap<String, File> files, String action) {
	// super();
	// this.url = url;
	// this.params = params;
	// this.files = files;
	// this.action = action;
	// }
	//
	// public String getUrl() {
	// return url;
	// }
	//
	// public void setUrl(String url) {
	// this.url = url;
	// }

	// public HashMap<String, String> getParams() {
	// return params;
	// }
	//
	// public void setParams(HashMap<String, String> params) {
	// this.params = params;
	// }
	//
	// public HashMap<String, File> getFiles() {
	// return files;
	// }
	//
	// public void setFiles(HashMap<String, File> files) {
	// this.files = files;
	// }

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public LinkedList<NameValuePair> getTextParams() {
		return textParams;
	}

	public void setTextParams(LinkedList<NameValuePair> textParams) {
		this.textParams = textParams;
	}

	public LinkedList<NameValuePair> getFileParams() {
		return fileParams;
	}

	public void setFileParams(LinkedList<NameValuePair> fileParams) {
		this.fileParams = fileParams;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
