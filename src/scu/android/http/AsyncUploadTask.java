package scu.android.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import org.apache.http.NameValuePair;
import org.json.JSONObject;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AsyncUploadTask implements Runnable {

	private final String TAG = "AsyncUploadTask";
	private final String MULTIPART_FROM_DATA = "multipart/form-data";
	private final String BOUNDARY = java.util.UUID.randomUUID().toString();
	private final String PREFIX = "--";
	private final String LINEND = "\r\n";
	private final String CHARSET = "UTF-8";
	private Context context;
	private AsyncHttpParams params;

	public AsyncUploadTask(Context context, AsyncHttpParams params) {
		this.context = context;
		this.params = params;
	}

	@Override
	public void run() {
		JSONObject result = null;
		try {
			result = upload(params);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sendResult(result);
		}
	}

	/**
	 * 配置网络连接
	 * 
	 * @param url
	 *            目标网址
	 * @return
	 */
	public HttpURLConnection configConn(final String url) {
		HttpURLConnection conn = null;
		try {
			URL uri = new URL(url);
			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(10 * 1000); // 缓存的最长时间
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 配置文本参数
	 * 
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public void addTextParams(DataOutputStream dataOutputStream,
			final LinkedList<NameValuePair> textParams) throws IOException {
		if (textParams != null) {
			StringBuilder param = new StringBuilder();
			for (NameValuePair entry : textParams) {
				param.append(PREFIX);
				param.append(BOUNDARY);
				param.append(LINEND);
				param.append("Content-Disposition: form-data; name=\""
						+ entry.getName() + "\"" + LINEND);
				param.append("Content-Type: text/plain; charset=" + CHARSET
						+ LINEND);
				param.append("Content-Transfer-Encoding: 8bit" + LINEND);
				param.append(LINEND);
				param.append(entry.getValue());
				param.append(LINEND);
			}
			dataOutputStream.write(param.toString().getBytes());
		}
	}

	/**
	 * 添加文件
	 * 
	 * @param files
	 * @throws IOException
	 */
	public void addFileParams(DataOutputStream dataOutputStream,
			final LinkedList<NameValuePair> fileParams) throws IOException {
		if (fileParams != null) {
			for (NameValuePair file : fileParams) {
				StringBuilder param = new StringBuilder();
				param.append(PREFIX);
				param.append(BOUNDARY);
				param.append(LINEND);
				param.append("Content-Disposition: form-data; name=\"q_resources[]\"; filename=\""
						+ file.getName() + "\"" + LINEND);
				param.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				param.append(LINEND);
				dataOutputStream.write(param.toString().getBytes());
				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					dataOutputStream.write(buffer, 0, len);
				}
				is.close();
				dataOutputStream.write(LINEND.getBytes());
			}
		}
	}

	/**
	 * 上传文本和文件
	 * 
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public JSONObject upload(final AsyncHttpParams params) throws IOException {
		JSONObject result = null;
		HttpURLConnection conn = configConn(params.getBaseUrl());
		DataOutputStream dataOutputStream = new DataOutputStream(
				conn.getOutputStream());
		addTextParams(dataOutputStream, params.getTextParams());
		addFileParams(dataOutputStream, params.getFileParams());
		final byte[] ends = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		dataOutputStream.write(ends);
		dataOutputStream.flush();
		try {
			final int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream inStream = conn.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inStream));
				StringBuilder response = new StringBuilder();
				String str = null;
				while ((str = reader.readLine()) != null) {
					response.append(str);
				}
				final String resStr = AppUtils
						.decodeString(response.toString());
				result = new JSONObject(resStr);
				System.out.println(AppUtils.decodeString(response.toString()));
				final int status = result.getInt("status");
				final String message = result.get("message").toString();
				Log.d(TAG, "status=" + status + "|messge=" + message
						+ "\nresponse=" + resStr);
				return result.getJSONObject("newIDs");
			} else {
				Log.e(TAG, "responseCode=" + responseCode);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (dataOutputStream != null)
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					Log.e(TAG, "[post] " + e.getMessage());
				}
			if (conn != null)
				conn.disconnect();
			Log.d(TAG, "[upload] " + "上传结束");
		}
		return result;
	}

	/**
	 * 通知上传结果
	 * 
	 * @param result
	 */
	public void sendResult(final JSONObject result) {
		final String action = params.getAction();
		final Intent intent = new Intent(action);
		intent.putExtra("action", "upload");
		try {
			if (result != null) {
				if (action.equals(Constants.QUESTIONS)) {
					final long qId = Long.parseLong(result.getString("q_id"));
					intent.putExtra("qId", qId);
				} else if (action.equals(Constants.REPLYS)) {
					final long qrId = Long.parseLong(result.getString("qr_id"));
					intent.putExtra("qrId", qrId);
				}
				// final long createdTime=result.getString("created_time");
				// intent.putExtra("createdTime", createdTime);
				if (params.getFileParams().size() > 0) {
					final long resourceId = Long.parseLong(result
							.getString("resource_id"));
					intent.putExtra("resourceId", resourceId);
				}
				intent.putExtra("result", "success");
				Log.d(TAG, "success");
			} else {
				intent.putExtra("result", "failed");
				Log.d(TAG, "failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.sendBroadcast(intent);
		}
	}
}
