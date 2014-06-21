package scu.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/**
 * 文件和参数下载
 * 
 * @author YouMingyang
 * 
 */
public class DownloadUtils {

	public static final String Tag = "DownloadUtils";

	public static void get(String url) {
		int res = 0;
		HttpClient client = new DefaultHttpClient();
		StringBuilder str = new StringBuilder();
		HttpGet httpGet = new HttpGet(url);
		BufferedReader buffer = null;
		try {
			HttpResponse httpRes = client.execute(httpGet);
			httpRes = client.execute(httpGet);
			res = httpRes.getStatusLine().getStatusCode();
			if (res == 200) {
				buffer = new BufferedReader(new InputStreamReader(httpRes
						.getEntity().getContent(), Charset.forName("UTF-8")));
				for (String s = buffer.readLine(); s != null; s = buffer
						.readLine()) {
					str.append(s);
				}
				buffer.close();
				final JSONObject result = new JSONObject(str.toString());
				JSONArray json = result.getJSONArray("content");
				
				String strs = "";
				for (int i = 0; i < json.length(); i++) {
					JSONObject jsonObject = (JSONObject) json.opt(i);

					strs += jsonObject.getString("q_text_content");
				}
				Log.i(Tag, strs);
			} else {
				Log.i(Tag, "HttpGet Error");
			}
		} catch (Exception e) {
			Log.i(Tag, "Exception");
			e.printStackTrace();
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
