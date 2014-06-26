package scu.android.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import scu.android.entity.AsyncHttpParams;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

public class AsyncUploadTask extends
		AsyncTask<AsyncHttpParams, Integer, JSONObject> {
	public static final String TAG = "AsyncUploadTask";
	public static Context context;

	@Override
	protected JSONObject doInBackground(AsyncHttpParams... asyncHttpParams) {
		final String url = asyncHttpParams[0].getUrl();
		final HashMap<String, String> params = asyncHttpParams[0].getParams();
		final HashMap<String, File> files = asyncHttpParams[0].getFiles();
		JSONObject result = null;
		try {
			result = upload(url, params, files);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if (result != null) {
			try {
				final long qId = Long.parseLong(result.getString("q_id"));
				final long qResource = Long.parseLong(result
						.getString("resource_id"));
				final Intent intent = new Intent(
						Constants.UPLOAD_QUESTION_SUCCESS);
				intent.putExtra("qId", qId);
				intent.putExtra("qResource", qResource);
				context.sendBroadcast(intent);
				Log.d(TAG, "[uploadQuestion] success");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.d(TAG, "[uploadQuestion] failed");
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	public JSONObject upload(final String url,
			final Map<String, String> params, final Map<String, File> files)
			throws IOException {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		HttpURLConnection conn = null;
		DataOutputStream outStream = null;
		try {
			URL uri = new URL(url);
			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(5 * 1000); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}

			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			InputStream in = null;
			// 发送文件数据
			if (files != null) {
				for (Map.Entry<String, File> file : files.entrySet()) {
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\"q_resources[]\"; filename=\""
							+ file.getKey() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="
							+ CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());
					InputStream is = new FileInputStream(file.getValue());
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
					is.close();
					outStream.write(LINEND.getBytes());
				}
				// 请求结束标志
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND)
						.getBytes();
				outStream.write(end_data);
				outStream.flush();
				// 得到响应码
				int res = conn.getResponseCode();
				in = conn.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder response = new StringBuilder();
				String str = null;
				while ((str = reader.readLine()) != null) {
					response.append(str);
				}
				JSONObject result = new JSONObject(
						AppUtils.decodeString(response.toString()));
				final int status = result.getInt("status");
				final String message = result.get("message").toString();
				Log.d(TAG, "status=" + status + "|messge=" + message);
				if (res == 200) {
					return result.getJSONObject("newIDs");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outStream != null)
				try {
					outStream.close();
				} catch (IOException e) {
					Log.e(TAG, "[post] " + e.getMessage());
				}
			if (conn != null)
				conn.disconnect();
		}
		return null;
	}

}