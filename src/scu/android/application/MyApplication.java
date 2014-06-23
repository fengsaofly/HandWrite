package scu.android.application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MyApplication extends Application {
	// public static String hostIp = "218.244.144.212";
	// public String hostName = "handwriteserver";

	public static String hostIp = "192.168.1.116";
	public static String hostName = "dolphin0520-pc";
	public static final String IS_ONLINE = "is_online";
	public String userName = "jalsary";
	public String passWord = "123456";
	public String nickName = "";

	public List<RosterGroup> groups = new ArrayList<RosterGroup>();
	public Roster roster;
	public List<RosterEntry> entries;
	public XMPPConnection mConnection;
	public static boolean loginFlag = false;
	public int firstIn = 1;

	public static VCard vCard = null;
	public SharedPreferences sp = null;

	// LoginConfig loginConfig;
	public  List<Map<String,Object>> allContactsVcard = new ArrayList<Map<String,Object>>();
/**
 * 
 * 下面的代码是跟服务器交互相关的接口
 */
	public List<Map<String, Object>> cmds = new ArrayList<Map<String, Object>>();
	public boolean threadRunFlag = false;
	public static boolean threadFlag = true;
	public String tkn = "";
	public int uid = 0;
	public final String preURI = "http://" + hostIp
			+ "/dblockerF/mapi/userservice/";
	public Thread communicateWithServerThread = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String url = "";
			Handler handler = null;
			JSONObject job = new JSONObject();
			HttpParams params = new BasicHttpParams();
			// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
			HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
			HttpConnectionParams.setSoTimeout(params, 20000);
			HttpClient client = new DefaultHttpClient();
			// client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
			// 10000);
			// client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
			// 10000);
			// Socket超时设置60s
			client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
					10000);
			// 连接超时60s
			client.getParams().setIntParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT, 10000);
			while (threadFlag) {

				if (cmds.size() != 0) {
					System.out.println("cmds: ");
					for (Map<String, Object> item : cmds) {
						System.out.println("item: " + item);
					}
					Map<String, Object> item = cmds.get(0); // 发送请求
					handler = (Handler) item.get("handler");
					job = (JSONObject) item.get("json");
					threadRunFlag = true;
					try {
						switch (Integer.parseInt(item.get("type").toString())) {
						case 0:
							/*
							 * 
							 * 
							 * /login case0
							 * 
							 * @param json_in {phonenumber,password}
							 * 
							 * @parm json_out {tkn,msg,credits,uid} (msg还是之前的定义)
							 * 
							 * /register case1
							 * 
							 * @param json_in
							 * {phonenumber,name,password,jobtitle}
							 * 
							 * @param json_out {msg}
							 * 
							 * 后面的请求都得带上 tkn+uid
							 * 
							 * /correct case2
							 * 
							 * @param json_in {phonenumber password} (新密码)
							 * 
							 * @param json_out {msg}
							 * 
							 * /imageDownload case3
							 * 
							 * /creditCalc case4
							 * 
							 * /creditLookup case5
							 * 
							 * /downloadLookup case6
							 * 
							 * /images case7
							 * 
							 * /feedback case 8
							 */

							url = preURI + "login";

							break;
						case 1:
							url = preURI + "register";
							break;
						case 2:
							url = preURI + "correct";
							break;
						case 3:
							url = preURI + "imageDownload";
							break;

						case 4:
							url = preURI + "creditCalc";
							break;
						case 5:

							url = preURI + "creditLookup";

							break;
						case 6:
							url = preURI + "downloadLookup";
							break;

						case 7:
							url = preURI + "images";
							break;

						case 8:
							url = preURI + "feedback";
							break;

						case 9:
							url = preURI + "resetpasswd";
							break;

						default:
							break;
						}

						HttpPost httpPost = new HttpPost(url);
						Header[] header = { new BasicHeader("tkn", tkn),
								new BasicHeader("uid", "" + uid) };
						httpPost.setHeaders(header);

						httpPost.setParams(params);
						httpPost.addHeader("Content-Type", "application/json");
						// StringEntity s = new StringEntity(job.toString(),
						// HTTP.UTF_8);

						httpPost.setEntity(new StringEntity(job.toString(),
								HTTP.UTF_8));
						// httpPost.setEntity(new
						// UrlEncodedFormEntity(job.toString(),HTTP.UTF_8));

						HttpResponse response = client.execute(httpPost);
						if (response.getStatusLine().getStatusCode() == 200) { // 处理结果
							// 获取返回的数据
							String resultStr = EntityUtils.toString(
									response.getEntity(), "UTF-8");
							System.out.println("结果：" + resultStr);
							JSONObject jo = new JSONObject(resultStr);
							String msg = jo.getString("msg");
							switch (Integer.parseInt(item.get("type")
									.toString())) {

							case 0: // 处理登陆返回的结果

								if ("error".equals(msg)) {
									Message message = handler.obtainMessage();
									message.what = 2;
									message.obj = "服务器认证失败!";
									handler.sendMessage(message);
								} else {
									String tkn2 = jo.getString("tkn");
									int credit = jo.getInt("credits");
									int uid2 = Integer.parseInt(jo
											.getString("uid"));
									int rank2 = jo.getInt("rank");
									String name = jo.getString("name");
//									nickName = name;
									// balance = balance2;
									tkn = tkn2;
									uid = uid2;
//									credits = credit;
//									rank = rank2;

									Message message = handler.obtainMessage();
									message.what = 1;

									handler.sendMessage(message);
								}
								break;
							case 1:// 注册
								if (!"error".equals(msg)) {
									handler.sendEmptyMessage(1);
								}

								else {
									handler.sendEmptyMessage(2);
								}

								break;
							case 2: // 修改密码
								if (!"error".equals(msg)) {
									handler.sendEmptyMessage(1);
								}

								else {
									handler.sendEmptyMessage(2);
								}
								break;
							// case 3: // 充值
							// if (!"error".equals(msg)) {
							// handler.sendEmptyMessage(1);
							// }
							//
							// else {
							// handler.sendEmptyMessage(2);
							// }
							// break;
							case 4: // 查询
								if (!"error".equals(msg)) {
//									int credit = jo.getInt("credits");
//									credits = credit;
									handler.sendEmptyMessage(1);

								} else {

									handler.sendEmptyMessage(2);
								}
								//
								break;
							// case 5:
							// if ("error".equals(msg)) {
							// Message message = handler
							// .obtainMessage();
							// message.what = 2;
							// message.obj = "访问服务器失败!";
							// handler.sendMessage(message);
							// } else {
							//
							// String records = jo
							// .getString("records");
							// rechargeRecords.clear();
							// JSONArray ja = new JSONArray(records);
							// for (int i = 0; i < ja.length(); i++) {
							// RechargeRecord rr = new RechargeRecord();
							// rr.setR_amount(ja.getJSONObject(i)
							// .getDouble("r_amount"));
							// rr.setR_id(ja.getJSONObject(i)
							// .getInt("r_id"));
							// rr.setR_time(ja.getJSONObject(i)
							// .getString("r_time"));
							// rr.setC_runnum(ja.getJSONObject(i)
							// .getString("c_runnum"));
							// rr.setC_state(ja.getJSONObject(i)
							// .getString("c_state"));
							// rr.setShop_name(ja.getJSONObject(i)
							// .getString("shop_name"));
							// rr.setShop_addr(ja.getJSONObject(i)
							// .getString("shop_addr"));
							// rr.setCustom(ja.getJSONObject(i)
							// .getString("custom"));
							// rechargeRecords.add(rr);
							// }
							//
							// Message message = handler
							// .obtainMessage();
							// message.what = 3;
							//
							// handler.sendMessage(message);
							// }
							// break;
							// case 6:
							// break;

							case 7:// 查看所有图片
								if (!"error".equals(msg)) {
									Message msg2 = handler.obtainMessage();
									msg2.what = 1;
									// downLoadInfo.clear();
									// downLoadInfo = null;
//									downLoadInfo = new ArrayList<Map<String, Object>>();
//									String images = jo.getString("images");
//									JSONArray ja = new JSONArray(images);
//									for (int i = 0; i < ja.length(); i++) {
//										String image = ja.getJSONObject(i)
//												.getString("name");
//										String ads = ja.getJSONObject(i)
//												.getString("ads");
//										String shop_url = ja.getJSONObject(i)
//												.getString("shop_url");
//										String disp_prefer = ja
//												.getJSONObject(i).getString(
//														"disp_prefer");
//										int cnt_left = ja.getJSONObject(i)
//												.getInt("cnt_left");
//										int keep_hour = ja.getJSONObject(i)
//												.getInt("keep_hours");
//										int reward = ja.getJSONObject(i)
//												.getInt("reward");
//										String dead_date = ja.getJSONObject(i)
//												.getString("dead_date");
//										Map<String, Object> info = new HashMap<String, Object>();
//
//										info.put("imagePath", image);
//										info.put("image", image);// 图片url
//										info.put("cnt_left", cnt_left);// 图片剩余数
//										info.put("dead_date", dead_date);// 图片截止时间
//										info.put("reward", reward);// 赚多少钱
//										info.put("keep_hour", keep_hour);// 需要使用多少时间
//										info.put("image_flag", 0);// 是否是图片
//										info.put("ads", ads);// 商家信息
//										info.put("url", shop_url);// 链接
//										info.put("disp_prefer", disp_prefer);// 日期显示位置
//										System.out.println("left: " + cnt_left);
//										System.out.println("image: " + image);
//										downLoadInfo.add(info);

//									}
//									msg2.obj = downLoadInfo;
									handler.sendMessage(msg2);

								}

								else {
									handler.sendEmptyMessage(2);
								}
								break;
							case 8:// 反馈
								if (!"error".equals(msg)) {
									handler.sendEmptyMessage(1);
								}

								else {
									handler.sendEmptyMessage(2);
								}

								break;

							case 9:
								if (!"error".equals(msg)) {
									handler.sendEmptyMessage(1);
								}

								else {
									handler.sendEmptyMessage(2);
								}
								break;
							default:
								break;

							}
						} else { // 返回值不等于200
							Message msg = handler.obtainMessage();
							msg.what = 2;

							handler.sendMessage(msg);
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						Message msg = handler.obtainMessage();
						msg.what = 2;
						msg.obj = e.getMessage().toString();
						handler.sendMessage(msg);
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						Message msg = handler.obtainMessage();
						msg.what = 2;
						msg.obj = e.getMessage().toString();
						handler.sendMessage(msg);
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Message msg = handler.obtainMessage();
						msg.what = 2;
						msg.obj = e.getMessage().toString();
						handler.sendMessage(msg);
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						Message msg = handler.obtainMessage();
						msg.what = 2;
						msg.obj = e.getMessage().toString();
						handler.sendMessage(msg);
						e.printStackTrace();
					}
					cmds.remove(0);
					threadRunFlag = false;
				}

				// cmds.clear();

				else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						Message msg = handler.obtainMessage();
						msg.what = 2;
						msg.obj = e.getMessage().toString();
						handler.sendMessage(msg);

						e.printStackTrace();
					}
				}
			}
		}
	});

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// initial();
		communicateWithServerThread.start();
		initImageLoader();
		
		// XmppConnectionManager.getInstance().init();
		System.out.println("oncreate   ");
		sp = getSharedPreferences("poti", MODE_PRIVATE);

	}

	@Override
	public void onTerminate() { // 在真机中永远不会被调用
		// TODO Auto-generated method stub
		threadFlag = false;
		super.onTerminate();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:

				Toast.makeText(MyApplication.this, "登陆成功", Toast.LENGTH_SHORT)
						.show();
				System.out.println("登陆成功   ");
				loginFlag = true;

				break;
			case 2:
				System.out.println("登陆失败   ");
				Toast.makeText(MyApplication.this, "登陆失败", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	

	public boolean addUser(String userName, String name) {
		try {
			Roster roster = this.roster;
			roster.createEntry(userName, name, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 返回所有组信息 <RosterGroup>
	 * 
	 * @return List(RosterGroup)
	 */
	public List<RosterGroup> getGroups() {
		Roster roster = this.roster;
		List<RosterGroup> groupsList = new ArrayList<RosterGroup>();
		Collection<RosterGroup> rosterGroup = roster.getGroups();
		Iterator<RosterGroup> i = rosterGroup.iterator();
		while (i.hasNext()) {

			groupsList.add(i.next());

		}
		this.groups = groupsList;

		return groupsList;
	}

	/**
	 * 返回相应(groupName)组里的所有用户<RosterEntry>
	 * 
	 * @return List(RosterEntry)
	 */
	public List<RosterEntry> getEntriesByGroup(String groupName) {
		Roster roster = this.roster;
		List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
		RosterGroup rosterGroup = roster.getGroup(groupName);
		Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext())
			EntriesList.add(i.next());

		return EntriesList;
	}

	/**
	 * 返回所有用户信息 <RosterEntry>
	 * 
	 * @return List(RosterEntry)
	 */
	public List<RosterEntry> getAllEntries() {
		Roster roster = this.roster;
		List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();
		Collection<RosterEntry> rosterEntry = roster.getEntries();
		Iterator<RosterEntry> i = rosterEntry.iterator();
		while (i.hasNext())
			EntriesList.add(i.next());
		this.entries = EntriesList;
		return EntriesList;
	}

	/**
	 * 添加一个好友到分组
	 * 
	 * @param roster
	 * @param userName
	 * @param name
	 * @return
	 */
	public boolean addUser(String userName, String name, String groupName) {
		try {
			Roster roster = this.roster;
			roster.createEntry(userName, name, new String[] { groupName });
			this.groups = getGroups();
			this.entries = getAllEntries();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除一个好友
	 * 
	 * @param roster
	 * @param userName
	 * @return
	 */
	public boolean removeUser(String userName) {
		try {
			Roster roster = this.roster;
			if (userName.contains("@")) {
				userName = userName.split("@")[0];
			}
			RosterEntry entry = roster.getEntry(userName);
			System.out.println("删除好友:" + userName);
			System.out.println("User: " + (roster.getEntry(userName) == null));
			roster.removeEntry(entry);
			this.groups = getGroups();
			this.entries = getAllEntries();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 删除一个组
	 */
	public boolean removeGroup(String groupName) {
		Roster roster = this.roster;
		this.groups = getGroups();
		this.entries = getAllEntries();
		return false;
	}

	public boolean searchUsers(XMPPConnection connection, String serverDomain,
			String userName) throws XMPPException {
		// List<UserBean> results = new ArrayList<UserBean>();
		System.out.println("查询开始..............." + connection.getHost()
				+ connection.getServiceName());

		UserSearchManager usm = new UserSearchManager(connection);

		Form searchForm = usm.getSearchForm(serverDomain);
		Form answerForm = searchForm.createAnswerForm();
		answerForm.setAnswer("Username", true);
		answerForm.setAnswer("search", userName);
		ReportedData data = usm.getSearchResults(answerForm, serverDomain);

		Iterator<Row> it = data.getRows();
		Row row = null;
		// UserBean user = null;
		while (it.hasNext()) {
			// user = new UserBean();
			row = it.next();
			// user.setUserName(row.getValues("Username").next().toString());
			// user.setName(row.getValues("Name").next().toString());
			// user.setEmail(row.getValues("Email").next().toString());
			System.out.println(row.getValues("Username").next());
			System.out.println(row.getValues("Name").next());
			System.out.println(row.getValues("Email").next());
			return true;
			// results.add(user);
			// 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
		}

		return false;
	}

	public static String getSDCardPath() {
		String cmd = "cat /proc/mounts";
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			BufferedInputStream in = new BufferedInputStream(p.getInputStream());
			BufferedReader inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				// 获得命令执行后在控制台的输出信息
				// LOG.i("CommonUtil:getSDCardPath", lineStr);
				if (lineStr.contains("sdcard")
						&& lineStr.contains(".android_secure")) {
					String[] strArray = lineStr.split(" ");
					if (strArray != null && strArray.length >= 5) {
						String result = strArray[1].replace("/.android_secure",
								"");
						return result;
					}
				}
				// 检查命令是否执行失败。
				if (p.waitFor() != 0 && p.exitValue() == 1) {
					// p.exitValue()==0表示正常结束，1：非正常结束
					// LOG.e("CommonUtil:getSDCardPath", "命令执行失败!");
				}
			}
			inBr.close();
			in.close();
		} catch (Exception e) {
			// LOG.e("CommonUtil:getSDCardPath", e.toString());

			return Environment.getExternalStorageDirectory().getPath();
		}

		return Environment.getExternalStorageDirectory().getPath();
	}

	public static ByteArrayInputStream getUserImage(XMPPConnection connection,
			String user) {
		ByteArrayInputStream bais = null;
		try {
			VCard vcard = new VCard();
			// 加入这句代码，解决No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
					new org.jivesoftware.smackx.provider.VCardProvider());

			vcard.load(connection, user + "@" + connection.getServiceName());

			if (vcard == null || vcard.getAvatar() == null)
				return null;
			bais = new ByteArrayInputStream(vcard.getAvatar());

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bais == null)
			return null;
		// Bitmap bm = BitmapFactory.decodeStream(bais);
		return bais;
	}

	/**
	 * 修改用户头像
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public void changeImage(XMPPConnection connection, File f)
			throws XMPPException, IOException {

		VCard vcard = new VCard();
		vcard.load(connection, userName + "@" + connection.getServiceName());

		byte[] bytes;

		bytes = getFileBytes(f);
		String encodedImage = StringUtils.encodeBase64(bytes);
		vcard.setAvatar(bytes, encodedImage);
		vcard.setEncodedImage(encodedImage);
		vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage
				+ "</BINVAL>", true);

		// ByteArrayInputStream bais = new ByteArrayInputStream(
		// vcard.getAvatar());
		// Image image = ImageIO.read(bais);
		// ImageIcon ic = new ImageIcon(image);

		// vcard.setMiddleName(vcard.getMiddleName());
		// System.out.println("vcard.getMiddleName(): "+vcard.getMiddleName());
		//
		// vcard.setNickName(vcard.getNickName());
		// System.out.println("vcard.getNickName(): "+vcard.getNickName());
		//
		// vcard.setFirstName(vcard.getFirstName());
		//
		// vcard.setAddressFieldHome("zone", vcard.getAddressFieldHome("zone"));
		//
		//
		// vcard.setLastName(vcard.getLastName());
		vcard.save(connection);

	}

	private static byte[] getFileBytes(File file) throws IOException {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			int bytes = (int) file.length();
			byte[] buffer = new byte[bytes];
			int readBytes = bis.read(buffer);
			if (readBytes != buffer.length) {
				throw new IOException("Entire file not read");
			}
			return buffer;
		} finally {
			if (bis != null) {
				bis.close();
			}
		}
	}

	public static VCard getUserVcard(XMPPConnection connection, String user) {
		VCard vcard = new VCard();
		try {

			// 加入这句代码，解决No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
					new org.jivesoftware.smackx.provider.VCardProvider());

			vcard.load(connection, user + "@" + connection.getServiceName());

			if (vcard == null)
				return null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return vcard;
	}

	public boolean saveArray(List<Map<String, Object>> list) { // 向sharedpreferences里面存list

		SharedPreferences.Editor mEdit = sp.edit();

		mEdit.putInt("multiNumber_", list.size());

		for (int i = 0; i < list.size(); i++) {
			mEdit.remove("roomName_" + i);
			mEdit.putString("roomName_" + i, list.get(i).get("roomName")
					.toString());
			mEdit.remove("roomNumber_" + i);
			mEdit.putInt("roomNumber_" + i,
					(Integer) list.get(i).get("roomNumber"));
			mEdit.remove("roomOwner_" + i);
			mEdit.putString("roomOwner_" + i, list.get(i).get("roomOwner")
					.toString());
			mEdit.remove("roomNoti_" + i);
			mEdit.putString("roomNoti_" + i, list.get(i).get("roomNoti")
					.toString());
			mEdit.remove("roomCurrentPeople_" + i);
			mEdit.putInt("roomCurrentPeople_" + i,
					(Integer) list.get(i).get("roomCurrentPeople"));
			mEdit.remove("roomTotlePeople_" + i);
			mEdit.putInt("roomTotlePeople_" + i,
					(Integer) list.get(i).get("roomTotlePeople"));

		}

		return mEdit.commit();
	}

	public void loadArray(List<Map<String, Object>> list) { // 取数据

		list.clear();
		int size = sp.getInt("multiNumber_", 0);

		for (int i = 0; i < size; i++) {

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("roomName", sp.getString("roomName_" + i, "null"));
			map.put("roomNumber", sp.getInt("roomNumber_" + i, 30));
			map.put("roomOwner", sp.getString("roomOwner_" + i, "admin"));
			map.put("roomNoti", sp.getString("roomNoti_" + i, "暂时没有群公告"));
			map.put("roomCurrentPeople",
					sp.getInt("roomCurrentPeople_" + i, 15));
			map.put("roomTotlePeople", sp.getInt("roomTotlePeople_" + i, 30));

			list.add(map);

		}

	}

	public static void exitRoom(String name) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// loadArray(list);
	}

	// 初始化图片加载器
	public void initImageLoader() {
		File cacheDir = StorageUtils.getOwnCacheDirectory(
				getApplicationContext(),
				"ConquerQuestion/CurrentUser/image/Cache");
		// Get singletone instance of ImageLoader
		ImageLoader imageLoader = ImageLoader.getInstance();
		// Create configuration for ImageLoader (all options are optional, use
		// only those you really want to customize)
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.memoryCacheExtraOptions(400, 400)
				// max width, max height.discCacheExtraOptions(400, 400,
				// CompressFormat.JPEG, 75) // Can slow ImageLoader, use it
				// carefully (Better don't use it)
				.threadPoolSize(5)
				.threadPriority(Thread.NORM_PRIORITY - 1)
				.denyCacheImageMultipleSizesInMemory()
				.offOutOfMemoryHandling()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache implementation
				.discCache(new UnlimitedDiscCache(cacheDir))
				// You can pass your own disc cache implementation
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.imageDownloader(
						new URLConnectionImageDownloader(5 * 1000, 20 * 1000))
				// connectTimeout (5 s), readTimeout (20 s)
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.enableLogging().build();
		// Initialize ImageLoader with created configuration. Do it once on
		// Application start.
		imageLoader.init(config);
	}

	public void setUserVCard(XMPPConnection con, int tag,
			String modifyNomal_value) throws XMPPException, IOException {

		// setting_value_nickname.setText(vcard.getNickName());
		// setting_value_zone.setText(vcard.getAddressFieldHome("zone"));
		// setting_value_gender.setText(vcard.getFirstName());
		// setting_value_grade.setText(vcard.getMiddleName());
		// setting_value_sign.setText(vcard.getLastName());
		ByteArrayInputStream bais = getUserImage(con, userName);
		VCard vcard = new VCard();
		try {

			// 加入这句代码，解决No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
					new org.jivesoftware.smackx.provider.VCardProvider());

			vcard.load(con, userName + "@" + con.getServiceName());

		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (tag) {
		case 1: // grade
			vcard.setMiddleName(modifyNomal_value);
			break;
		case 2: // nickName
			vcard.setNickName(modifyNomal_value);
			break;
		case 3: // gender
			vcard.setFirstName(modifyNomal_value);
			break;
		case 4: // zone
			vcard.setAddressFieldHome("zone", modifyNomal_value);
			break;
		case 5: // sign
			vcard.setLastName(modifyNomal_value);
			break;

		}

		// vcard.setAvatar(vcard.getAvatar());
		vcard.save(con);

		updateAvatar(bais, con);

	}

	public void updateAvatar(ByteArrayInputStream bais, XMPPConnection con)
			throws XMPPException, IOException {
		VCard vcard = new VCard();
		vcard.load(con, userName + "@" + con.getServiceName());

		byte[] bytes;

		bytes = input2byte(bais);
		String encodedImage = StringUtils.encodeBase64(bytes);
		vcard.setAvatar(bytes, encodedImage);
		vcard.setEncodedImage(encodedImage);
		vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage
				+ "</BINVAL>", true);

		vcard.save(con);
	}

	public static final byte[] input2byte(ByteArrayInputStream inStream)
			throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}
	
	
	/**
	 * Bitmap转化为drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	/**
	 * Drawable 转 bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}
	}
	
	
	public  Map<String,Object> getFriendVcard(String friendName){
		for(Map<String,Object> item : allContactsVcard){
			if(item.get("friend_name").equals(friendName)){
				return item;
			
			}
			else continue;
			
		}
		return null;
	}

}
