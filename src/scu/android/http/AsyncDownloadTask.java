package scu.android.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
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

import scu.android.entity.Question;
import scu.android.entity.Resource;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * 异步下载
 * 
 * @author YouMingyang
 * 
 */
public class AsyncDownloadTask extends
		AsyncTask<AsyncHttpParams, Integer, JSONArray> {
	public static final String TAG = "AsyncDownloadTask";
	public static Context context;
	private AsyncHttpParams params;

	@Override
	protected JSONArray doInBackground(AsyncHttpParams... asyncHttpParams) {
		this.params = asyncHttpParams[0];
		AppUtils.setStrictMode();
		final String url = params.getBaseUrl();
		JSONArray result = null;
		result = get(url);
		return result;
	}

	@Override
	protected void onPostExecute(JSONArray result) {
		final Intent intent = new Intent(params.getAction());
		try {
			intent.putExtra("action", "download");
			if (result != null) {
				Bundle data = new Bundle();
				if (params.getAction().equals(Constants.QUESTIONS)) {
					data.putSerializable("contents", parseQuestions(result));
				}
				intent.putExtras(data);
				Log.d(TAG, "success");
			} else {
				Log.e(TAG, "failed");
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		} finally {
			context.sendBroadcast(intent);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	/**
	 * 配置安全httpclient,自动关闭
	 * 
	 * @return
	 */
	public HttpClient configHttpClient() {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 100);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,
				schemeRegistry);

		return new DefaultHttpClient(cm, params);
	}

	/**
	 * 获取数据
	 * 
	 * @param url
	 * @return
	 */
	public JSONArray get(final String baseUrl) {
		final String url = baseUrl + "?"
				+ URLEncodedUtils.format(params.getTextParams(), "UTF-8");
		HttpGet httpGet = new HttpGet(url);
		HttpClient client = configHttpClient();
		StringBuilder str = new StringBuilder();
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
				Log.d(TAG, "status=" + status + "|messge=" + message);
				if (0 == status) {
					return result.getJSONArray("content");
				}
			} else {
				Log.e(TAG, "StatusCode:" + statusCode);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
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

	/**
	 * 解析出返回结果中的问题
	 * 
	 * @param contents
	 * @return
	 */
	public ArrayList<Question> parseQuestions(final JSONArray contents) {
		ArrayList<Question> questions = new ArrayList<Question>();
		try {
			// @SuppressWarnings("unchecked")
			// Iterator<String> keys = contents.keys();
			// while (keys.hasNext()) {
			// JSONObject result = (JSONObject) contents.get(keys.next());
			// long qId = Long.parseLong(result.getString("id"));
			// long qUser = Long.parseLong(result.getString("q_user"));
			// String qTitle = result.getString("q_title");
			// String qTextContent = result.getString("q_text_content");
			// long qResource = Long.parseLong(result.getString("q_resource"));
			// Date createdTime = new Date(
			// result.getLong("created_time") * 1000);
			// System.out.println(result.getLong("created_time"));
			// int qState = Integer.parseInt(result.getString("q_state"));
			// String qGrade = result.getString("q_grade");
			// String qSubject = result.getString("q_subject");
			// ArrayList<Resource> sResources = new ArrayList<Resource>();
			// ArrayList<Resource> lResources = new ArrayList<Resource>();
			// JSONArray spath = result.getJSONArray("resource_spath");
			// JSONArray lpath = result.getJSONArray("resource_lpath");
			// for (int j = 0; j < spath.length(); j++) {
			// sResources.add(new Resource(spath.get(j).toString()));
			// }
			// for (int j = 0; j < lpath.length(); j++) {
			// lResources.add(new Resource(lpath.get(j).toString()));
			// }
			// Question question = new Question(qId, qTitle, qUser,
			// qTextContent, qResource, createdTime, qState, qGrade,
			// qSubject);
			// question.setsResources(sResources);
			// question.setResources(lResources);
			// questions.add(question);
			// }
			for (int i = 0; i < contents.length(); i++) {
				JSONObject result = contents.optJSONObject(i);
				long qId = Long.parseLong(result.getString("id"));
				long qUser = Long.parseLong(result.getString("q_user"));
				String qTitle = result.getString("q_title");
				String qTextContent = result.getString("q_text_content");
				long qResource = Long.parseLong(result.getString("q_resource"));
				Date createdTime = new Date(
						result.getLong("created_time") * 1000);
				int qState = Integer.parseInt(result.getString("q_state"));
				String qGrade = result.getString("q_grade");
				String qSubject = result.getString("q_subject");
				long resourceId = Long.parseLong(result
						.getString("resource_id"));
				JSONArray spath = result.getJSONArray("resource_spath");
				JSONArray lpath = result.getJSONArray("resource_lpath");
				ArrayList<Resource> resources = new ArrayList<Resource>();
				for (int j = 0; j < lpath.length(); j++) {
					resources.add(new Resource(resourceId, spath.get(j)
							.toString(), lpath.get(j).toString()));
				}
				Question question = new Question(qId, qTitle, qUser,
						qTextContent, qResource, createdTime, qState, qGrade,
						qSubject);
				question.setResources(resources);
				questions.add(question);
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
		return questions;
	}
}
