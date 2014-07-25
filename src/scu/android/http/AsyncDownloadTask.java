package scu.android.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
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

import scu.android.dao.Question;
import scu.android.dao.Reply;
import scu.android.dao.Resource;
import scu.android.db.DBTools;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 异步下载
 * 
 * @author YouMingyang
 * 
 */
public class AsyncDownloadTask implements Runnable {
	public static final String TAG = "AsyncDownloadTask";
	private Context context;
	private AsyncHttpParams params;

	public AsyncDownloadTask(Context context, AsyncHttpParams params) {
		this.context = context;
		this.params = params;
	}

	@Override
	public void run() {
		AppUtils.setStrictMode();
		String url = params.getBaseUrl();
		JSONArray result = get(url);
		Intent intent = new Intent(params.getAction());
		try {
			intent.putExtra("action", "download");
			if (result != null) {
				Log.d(TAG, "success");
				Bundle data=new Bundle();
				if (params.getAction().equals(Constants.QUESTIONS)) {
					data.putSerializable("questions",parseQuestions(result));
				} else if (params.getAction().equals(Constants.REPLYS)) {
					data.putSerializable("replys",parseReplys(result));
				}
				intent.putExtras(data);
			} else {
				Log.e(TAG, "failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.sendBroadcast(intent);

		}
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
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params,schemeRegistry);
		return new DefaultHttpClient(cm, params);
	}

	/**
	 * 获取数据
	 * 
	 * @param url
	 * @return
	 */
	public JSONArray get(final String baseUrl) {
		final String url = baseUrl + "?"+ URLEncodedUtils.format(params.getTextParams(), "UTF-8");
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
				buffer = new BufferedReader(new InputStreamReader(httpRes.getEntity().getContent(), Charset.forName("UTF-8")));
				for (String s = buffer.readLine(); s != null; s = buffer.readLine()) {
					str.append(s);
				}
				Log.d(TAG, AppUtils.decodeString(str.toString()));
				final JSONObject result = new JSONObject(AppUtils.decodeString(str.toString()));
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
			e.printStackTrace();
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
			DBTools mDBTools = DBTools.getInstance(context);
			for (int i = 0; i < contents.length(); i++) {
				JSONObject result = contents.optJSONObject(i);
				long q_id = Long.parseLong(result.getString("id"));
				long q_user = Long.parseLong(result.getString("q_user"));
				String q_title = result.getString("q_title");
				String q_text_content = result.getString("q_text_content");
				long q_resource = Long.parseLong(result.getString("q_resource"));
				long created_time = result.getLong("created_time");
				short q_state = Short.parseShort(result.getString("q_state"));
				String q_grade = result.getString("q_grade");
				String q_subject = result.getString("q_subject");
				String rId = result.getString("resource_id");
				ArrayList<Resource> resources = null;
				if (!rId.equals("null")) {
					long resource_id = Long.parseLong(rId);
					JSONArray spath = result.getJSONArray("resource_spath");
					JSONArray lpath = result.getJSONArray("resource_lpath");
					resources = new ArrayList<Resource>();
					if (lpath != null) {
						for (int j = 0; j < lpath.length(); j++) {
							resources.add(new Resource(null, resource_id, spath.get(j).toString(), lpath.get(j).toString()));
						}
					}
				}
				Question question = new Question(q_id, q_id, q_title,q_text_content, created_time, q_grade, q_subject,q_state, q_user, q_resource);
				questions.add(question);
				if (resources != null) {
					mDBTools.insertDownloadResource(resources);
				}
			}
			mDBTools.insertQuestions(questions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return questions;
	}

	/**
	 * 解析出返回结果中的回复
	 * 
	 * @param contents
	 * @return
	 */
	public ArrayList<Reply> parseReplys(final JSONArray contents) {
		ArrayList<Reply> replys = new ArrayList<Reply>();
		try {
			DBTools mDBTools = DBTools.getInstance(context);
			for (int i = 0; i < contents.length(); i++) {
				JSONObject result = contents.optJSONObject(i);
				long qr_id = Long.parseLong(result.getString("id"));
				long qr_user = Long.parseLong(result.getString("qr_user"));
				String qr_text = result.getString("qr_text");
				long qr_resource = Long.parseLong(result.getString("qr_resource"));
				long created_time = result.getLong("created_time");
				long qr_q = Long.parseLong(result.getString("qr_q"));
				short qr_type = Short.parseShort(result.getString("qr_type"));
				String rrId = result.getString("resource_id");
				ArrayList<Resource> resources = null;
				if (!rrId.equals("null")) {
					long resourceId = Long.parseLong(rrId);
					JSONArray spath = result.getJSONArray("resource_spath");
					JSONArray lpath = result.getJSONArray("resource_lpath");
					resources = new ArrayList<Resource>();
					if (lpath != null) {
						for (int j = 0; j < lpath.length(); j++) {
							resources.add(new Resource(null, resourceId, spath.get(j).toString(), lpath.get(j).toString()));
						}
					}
				}
				Reply reply = new Reply(null, qr_id, qr_text, created_time,qr_type, qr_user, qr_q, qr_resource);
				replys.add(reply);
				if (resources != null) {
					mDBTools.insertDownloadResource(resources);
				}
			}
			mDBTools.insertReplys(replys);
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
		return replys;
	}
}
