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
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import scu.android.application.MyApplication;
import scu.android.dao.Question;
import scu.android.dao.Reply;
import scu.android.dao.Resource;
import scu.android.db.DBTools;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AsyncUploadTask implements Runnable {

	private final String TAG = getClass().getName();
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
			final String action = params.getAction();
			String name = null;
			if (action.equals(Constants.QUESTIONS)) {
				name = "q_resources[]";
			} else if (action.equals(Constants.REPLYS)) {
				name = "qr_resources[]";
			}
			for (NameValuePair file : fileParams) {
				StringBuilder param = new StringBuilder();
				param.append(PREFIX);
				param.append(BOUNDARY);
				param.append(LINEND);
				param.append("Content-Disposition: form-data; name=\"" + name
						+ "\"; filename=\"" + file.getName() + "\"" + LINEND);
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
		if (params.getFileParams() != null) {
			addFileParams(dataOutputStream, params.getFileParams());
		}
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
				final int status = result.getInt("status");
				final String message = result.get("message").toString();
				Log.d(TAG, "status=" + status + "|messge=" + message
						+ "\nresponse=" + resStr+"result:\n"+result.toString());
				return result.getJSONObject("newIDs");
			} else {
				Log.e(TAG, "responseCode=" + responseCode);
			}
		} catch (Exception e) {
//			Log.e(TAG, e.getMessage());
			e.printStackTrace();
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
					long q_id = Long.parseLong(result.getString("q_id"));
					intent.putExtra("q_id", q_id);
					long created_time = Long.parseLong(result.getString("created_time"));
					intent.putExtra("created_time", created_time);
					long q_resource = 0;
					if (params.getFileParams() != null&& params.getFileParams().size() > 0) {
						q_resource = Long.parseLong(result.getString("resource_id"));
					}
					intent.putExtra("q_resource", q_resource);
					updateQuestion(q_id, created_time, q_resource);
				} else if (action.equals(Constants.REPLYS)) {
					long qr_id = Long.parseLong(result.getString("qr_id"));
					intent.putExtra("qr_id", qr_id);
					long created_time = Long.parseLong(result.getString("created_time"));
					intent.putExtra("created_time", created_time);
					long qr_resource = 0;
					if (params.getFileParams() != null&& params.getFileParams().size() > 0) {
						qr_resource = Long.parseLong(result.getString("resource_id"));
					}
					intent.putExtra("qr_resource", qr_resource);
					updateReply(qr_id, created_time, qr_resource);
				}else if(action.equals(Constants.STORYS)){
//					long s_id=
				}
				intent.putExtra("result", "success");
			} else {
				if (action.equals(Constants.QUESTIONS)) {
					updateQuestion(0, 0, 0);
				} else if (action.equals(Constants.REPLYS)) {
					updateReply(0, 0, 0);
				}
				intent.putExtra("result", "failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			context.sendBroadcast(intent);
		}
	}

	/**
	 * 更新上传问题
	 * 
	 * @param q_id
	 * @param created_time
	 * @param q_resource
	 */
	public void updateQuestion(final long q_id, final long created_time,
			final long q_resource) {
		DBTools mDBTools = DBTools.getInstance(context);
		Question mQuestion = mDBTools.loadQuestionById(MyApplication.oldId);
		if (mQuestion != null) {
			if (q_id != 0) {
				mQuestion.setQ_state((short) 0);
				mQuestion.setQ_id(q_id);
				mQuestion.setCreated_time(created_time);
				if (q_resource != 0) {
					mQuestion.setQ_resource(q_resource);
					List<Resource> mResources = mDBTools
							.loadResources(MyApplication.oldResourceId);
					for (Resource mResource : mResources) {
						mResource.setResource_id(q_resource);
					}
					mDBTools.updateResources(mResources);
				}
			} else {
				mQuestion.setQ_state((short) 3);
			}
			mDBTools.updateQuestion(mQuestion);
		}
	}

	/**
	 * 更新上传问题回复
	 * 
	 * @param qr_id
	 * @param created_time
	 * @param qr_resource
	 */
	public void updateReply(final long qr_id, final long created_time,
			final long qr_resource) {
		DBTools mDBTools = DBTools.getInstance(context);
		Reply mReply = mDBTools.loadReplyById(MyApplication.oldId);
		if (mReply != null) {
			if (qr_id != 0) {
				mReply.setQr_id(qr_id);
				mReply.setCreated_time(created_time);
				if (qr_resource != 0) {
					mReply.setQr_resource(qr_resource);
					List<Resource> mResources = mDBTools
							.loadResources(MyApplication.oldResourceId);
					for (Resource mResource : mResources) {
						mResource.setResource_id(qr_resource);
					}
					mDBTools.updateResources(mResources);
				}
			}
			mDBTools.updateReply(mReply);
		}
	}
}
