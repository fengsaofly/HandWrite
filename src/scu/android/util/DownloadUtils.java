package scu.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.StrictMode;
import android.util.Log;

/**
 * 文件和参数下载
 * 
 * @author YouMingyang
 * 
 */
public class DownloadUtils {

	public static final String Tag = "DownloadUtils";

	public static JSONArray get(String url) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 100);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);
		HttpClient client = new DefaultHttpClient(cm, params);
		StringBuilder str = new StringBuilder();
		HttpGet httpGet = new HttpGet(url);
		BufferedReader buffer = null;
		int statusCode = 0;
		try {

			HttpResponse httpRes = client.execute(httpGet);
			httpRes = client.execute(httpGet);
			statusCode = httpRes.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				buffer = new BufferedReader(new InputStreamReader(httpRes
						.getEntity().getContent(), Charset.forName("UTF-8")));
				for (String s = buffer.readLine(); s != null; s = buffer
						.readLine()) {
					str.append(s);
				}
				final JSONObject result = new JSONObject(
						AppUtils.decodeString(str.toString()));
				final int status = result.getInt("status");
				final String message = result.get("message").toString();
				Log.d(Tag, "status=" + status + "|messge=" + message);
				if (0 == status) {
					return result.getJSONArray("content");
				}
			} else {
				Log.e(Tag, "StatusCode:" + statusCode);
			}
		} catch (Exception e) {
			Log.e(Tag, e.getMessage());
		} finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return null;
	}
}
