package scu.android.application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;
import org.jivesoftware.smackx.packet.DiscoverInfo;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import scu.android.dao.DaoMaster;
import scu.android.dao.DaoMaster.OpenHelper;
import scu.android.dao.DaoSession;
import scu.android.dao.Question;
import scu.android.dao.Reply;
import scu.android.dao.Resource;
import scu.android.dao.Story;
import scu.android.dao.User;
import scu.android.dao.UserDao;
import scu.android.http.AsyncDownloadTask;
import scu.android.http.AsyncHttpParams;
import scu.android.http.HttpTools;
import scu.android.util.Constants;
import scu.android.util.XmppTool;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.demo.note.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MyApplication extends Application {
	private static final String TAG = "MyApplication";

//	public static String hostIp = "218.244.144.212";
//	public static String hostName = "handwriteserver";

//	 public static String hostIp = "192.168.1.116";
//	 public static String hostName = "dolphin0520-pc";
	 
	 public static String hostIp = "192.168.1.149";
	 public static String hostName = "handwriteserver";
	 
//	 public static String hostIp = "192.168.1.210";
//	 public static String hostName = "handwriteserver";
	private static ArrayList<Map<String,Object>> allContactsVcard = new ArrayList<Map<String,Object>>();
	public static Map<String,Object> myVcard = new HashMap<String,Object>();
//	public Map<String, Drawable> DrawableOfNameMap;
//	public Map<String, Drawable> DrawableOfNickNameMap;
	public static Drawable myIconDrawable = null;
	public static String nickName = "";
	public static String userName = "jalsary";
	public String passWord = "123456";

	public List<RosterGroup> groups = new ArrayList<RosterGroup>();
	public Roster roster;
	public List<RosterEntry> entries;
	public XMPPConnection mConnection;
	
	public int firstIn = 1; 
	public static boolean loginFlag = false;
	public static final String IS_ONLINE = "is_online";

//	public static VCard myVcard = null;
	public SharedPreferences sp = null;

	private static User loginUser;
	public static boolean isUploading;
	public static long oldId = -1;
	public static long oldResourceId = -1;
	public static int mHeight = 0;  //屏幕高度
//	public static Handler actionBarHandler = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// initial();
//		myIconDrawable = getResources().getDrawable(R.drawable.default_avatar);
		initImageLoader(getApplicationContext());
		// System.out.println("oncreate   ");
		sp = getSharedPreferences("poti", MODE_PRIVATE);
		
		WindowManager mWindowManager = (WindowManager) this  
                .getSystemService(Context.WINDOW_SERVICE);  
        Display mDisplay = mWindowManager.getDefaultDisplay();  
        mHeight = mDisplay.getHeight();
        if(mHeight ==0){
        	mHeight = 30*26;
        }
		
        
        ProviderManager.getInstance().addIQProvider("muc", "YANG", new MUCPacketExtensionProvider());
		
	}
	
	
	
	public class MUCPacketExtensionProvider implements IQProvider {

        @Override
        public IQ parseIQ(XmlPullParser parser) throws Exception {
                int eventType = parser.getEventType();
                MUCInfo info = null;
                while (true) {
                        if (eventType == XmlPullParser.START_TAG) {
//                                if ("room".equals(parser.getName())) {
//                                        String account = parser.getAttributeValue("", "account");
//                                        String room = parser.nextText();
//                                        
//                                        info = new MUCInfo();
//                                        info.setAccount(account);
//                                        info.setRoom(room);
//                                        info.setNickname(account);
//                                        
//                                        System.out.println("info: "+info.toString());
//                                        
////                                        Application.getInstance().addMUCInfo(info);
//                                }
                        	
                        	System.out.println("parser.getText():  "+parser.getText());
                        } else if (eventType == XmlPullParser.END_TAG) {
                                if ("muc".equals(parser.getName())) {
                                        break;
                                }
                        }
                        eventType = parser.next();
                }
                return null;
        }

}
	
	
	/**
	 * @Title: MUCInfo.java
	 * @Package ouc.sei.suxin.android.data.entity
	 * @Description: 用于传输从Server端传输过来的MUC的信息
	 * @author Yang Zhilong
	 * @blog http://blog.csdn.net/yangzl2008
	 * @date 2013年11月27日 上午9:27:25
	 * @version V1.0
	 */
	public class MUCInfo {
	        private String account;
	        private String room;
	        private String nickname;

	        public String getAccount() {
	                return account;
	        }

	        public void setAccount(String account) {
	                this.account = account;
	        }

	        public String getRoom() {
	                return room;
	        }

	        public void setRoom(String room) {
	                this.room = room;
	        }

	        public String getNickname() {
	                return nickname;
	        }

	        public void setNickname(String nickname) {
	                if (nickname.contains("@")) {
	                        this.nickname = nickname.substring(0, account.indexOf("@"));
	                        return;
	                }
	                this.nickname = nickname;
	        }

	        @Override
	        public String toString() {
	                return "MUCInfo [account=" + account + ", room=" + room + ", nickname="
	                                + nickname + "]";
	        }

	}



	

//	public void initial() {
//		// mConnection = XmppTool.getConnection();
//		new Thread(new Runnable() {
//			public void run() {
//
//				try {
//
//					XmppTool.getConnection().login(userName, passWord);
//					// 新建presence对象״̬
//					Presence presence = new Presence(Presence.Type.available);
//					XmppTool.getConnection().sendPacket(presence);
//
//					System.out.println("正在登陆");
//					handler.sendEmptyMessage(1);
//					roster = XmppTool.getConnection().getRoster();
//
//					entries = getAllEntries();
//
//				} catch (XMPPException e) {
//					XmppTool.closeConnection();
//
//					handler.sendEmptyMessage(2);
//				}
//			}
//		}).start();
//	}
//
//	Handler handler = new Handler() {
//		public void handleMessage(Message msg) {
//
//			switch (msg.what) {
//			case 1:
//
//				Toast.makeText(MyApplication.this, "登陆成功", Toast.LENGTH_SHORT)
//						.show();
//				System.out.println("登陆成功   ");
//				loginFlag = true;
//
//				break;
//			case 2:
//				System.out.println("登陆失败   ");
//				Toast.makeText(MyApplication.this, "登陆失败", Toast.LENGTH_SHORT)
//						.show();
//				break;
//			}
//			super.handleMessage(msg);
//		}
//	};
//	public Map<String, String> iconMap;
//	public Map<String, String> cityMap;
//	public Map<String, String> signMap;

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
			
			Map<String,Object> map2 = new HashMap<String, Object>();
            map2.put("friend_avatar", getResources().getDrawable(R.drawable.default_avatar));
			map2.put("friend_name", userName);
			map2.put("friend_nickName", userName);
			map2.put("friend_carrer", "");
			map2.put("friend_gender", "");
			map2.put("friend_zone", "");
			map2.put("friend_sign", "");
			getAllContactsVcard().add(map2);
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
			
			for(int i=0;i<getAllContactsVcard().size();i++){
				if(getAllContactsVcard().get(i).get("friend_name").equals(userName)){
					getAllContactsVcard().remove(i);
					break;
				}
			}
			
			
			
			ChatManager cm = XmppTool.getConnection().getChatManager();
			Chat newchat = cm.createChat(userName+ "@"
					+ hostName, null);
			
			try {

				newchat.sendMessage("unsubscribe");

			} catch (XMPPException e) {
				e.printStackTrace();
			}
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

	public static void changeImage(XMPPConnection connection, File f)
			throws XMPPException, IOException {

		VCard vcard = new VCard();
		vcard.load(connection);

		byte[] bytes;

		bytes = getFileBytes(f);
		String encodedImage = StringUtils.encodeBase64(bytes);
		vcard.setAvatar(bytes);
		vcard.setEncodedImage(encodedImage);
		vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage
				+ "</BINVAL>", true);

		// ByteArrayInputStream bais = new
		// ByteArrayInputStream(vcard.getAvatar());
		// FormatTools.getInstance().InputStream2Bitmap(bais);

		vcard.save(connection);
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

	public static VCard getUserVcard(XMPPConnection connection, String user) {
		VCard vcard = new VCard();
		try {

			// 加入这句代码，解决No VCard for
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
					new org.jivesoftware.smackx.provider.VCardProvider());
			System.out.println( "load..."+ user + "@" + connection.getServiceName());
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

	public  void sendFile(String user,
			File file) {
		try {
			XMPPConnection connection = XmppTool.getConnection();
			System.out.println("发送文件开始" + file.getName());
			FileTransferManager transfer = new FileTransferManager(connection);
			String destination = user +"@"+hostName +"/Smack";
			System.out.println("发送给： "+destination);
			final OutgoingFileTransfer out = transfer
					.createOutgoingFileTransfer(destination);
			System.out.println(connection.getPort());
			if (file.exists()) {
				System.out.println("文件存在");
			}
			long timeOut = 100000;
			long sleepMin = 3000;
			long spTime = 0;
			int rs = 0;
			try {
				byte[] fileData = getFileBytes(file);
				OutputStream os = out.sendFile(file.getName(), fileData.length,
						"you won't like it");
				os.write(fileData);
				os.flush();
				rs = out.getStatus().compareTo(FileTransfer.Status.complete);
				while (rs != 0) {
					System.out
							.println("getStatus" + out.getStatus().toString());
					rs = out.getStatus()
							.compareTo(FileTransfer.Status.complete);
					System.out.println(rs);
					spTime = spTime + sleepMin;
					if (spTime > timeOut) {
						System.out.println("fail" + "文件发送失败");
						return;
					}
					Thread.sleep(sleepMin);
				}
				System.out.println("end send file");
			} catch (XMPPException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

//	public static VCard getUserVCard(XMPPConnection connection)
//			throws XMPPException {
//		VCard vcard = new VCard();
//		vcard.load(connection);
//		System.out.println(vcard.getField("sex"));
//		System.out.println(vcard.getField("DESC"));
//		System.out.println(vcard.getEmailHome());
//		System.out.println(vcard.getOrganization());
//		System.out.println(vcard.getNickName());
//		System.out.println(vcard.getPhoneWork("PHONE"));
//		System.out.println(vcard.getProperty("DESC"));
//		System.out.println(vcard.getAvatar());
//		return vcard;
//	}

//	public static VCard getUserVCard(XMPPConnection connection, String userJid)
//			throws XMPPException {
//		VCard vcard = new VCard();
//		vcard.load(connection, userJid);
//		System.out.println(vcard.getOrganization());
//		System.out.println(vcard.getField("sex"));
//		System.out.println(vcard.getNickName());
//		System.out.println(vcard.getAvatar());
//		System.out.println(vcard.getField("DESC"));
//		return vcard;
//	}

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

	public  final byte[] input2byte(ByteArrayInputStream inStream)
			throws IOException {
		if(inStream!=null){
			
		
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
		}
		else {
			Bitmap bm = drawable2Bitmap(MyApplication.this.getResources().getDrawable(R.drawable.default_avatar));
			return Bitmap2Bytes(bm);
		}
	}
	
	 public static byte[] Bitmap2Bytes(Bitmap bm) {
		          ByteArrayOutputStream baos = new ByteArrayOutputStream();
		          bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		         return baos.toByteArray();
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
	
	
	public Map<String, Object> getFriendVcard(String friendName) {
		for (Map<String, Object> item : getAllContactsVcard()) {
			if (item.get("friend_name").equals(friendName)) {
				return item;

			} else
				continue;

		}
		return null;
	}
	public static void getRoomInfo(List<RoomInfo> result){
		List<String> col = new ArrayList<String>();
		try {
			col = getConferenceServices(XmppTool.getConnection().getServiceName(), XmppTool.getConnection());
			 for (Object aCol : col) {
				 String service = (String) aCol;
				 Collection<HostedRoom> rooms = MultiUserChat.getHostedRooms(XmppTool.getConnection(),service);
				 for(HostedRoom aroom : rooms) {
					 System.out.println(aroom.getName() + " - " +aroom.getJid());
					 RoomInfo roomInfo = MultiUserChat.getRoomInfo(XmppTool.getConnection(),aroom.getJid());
					 
					 if(roomInfo != null) {
						 result.add(roomInfo);
						 System.out.println("roomInfo.getOccupantsCount(): "+roomInfo.getOccupantsCount() + " \nroomInfo.getSubject() : " +roomInfo.getSubject()+"\nroomInfo.getDescription(): "+roomInfo.getDescription()+roomInfo.getRoom());
					 }
				 }
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		return result;
	}
	
	public static List<String> getConferenceServices(String server,XMPPConnection connection) throws Exception{
		List<String> answer = new ArrayList<String>();
		ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);
		DiscoverItems items = discoManager.discoverItems(server);
		for(Iterator<DiscoverItems.Item> it = items.getItems();it.hasNext();){
			DiscoverItems.Item item = (DiscoverItems.Item)it.next();
			if(item.getEntityID().startsWith("conference")||item.getEntityID().startsWith("private")){
				answer.add(item.getEntityID());
			}
			else{
				try{
					
					DiscoverInfo info = discoManager.discoverInfo(item.getEntityID());
					if(info.containsFeature("http://jabber.org/protocol/muc")){
						answer.add(item.getEntityID());
					}
				}catch(XMPPException e){
					e.printStackTrace();
				}
			}
		}
		return answer;
		
	}
	
	public static List<String> getAllMembersInGroup(MultiUserChat muc ){
		List<String> members = new ArrayList<String>();
		Iterator<String> it = muc.getOccupants();
		while(it.hasNext()){
			String name = it.next();
			name = name.substring(name.indexOf("/")+1);
			members.add(name);
		}
		
		return (null!=members&&members.size()!=0)?members:null;
		
		
	}
	
	
	 public int IsUserOnLine(String user) {    //判断用户是否在线
	        String url = "http://"+hostIp+":9090/plugins/presence/status?" +  
	                "jid="+ user +"@"+ hostName +"&type=xml";  
	        int shOnLineState = 0; // 不存在  
	        try {  
	            URL oUrl = new URL(url);  
	            URLConnection oConn = oUrl.openConnection();  
	            if (oConn != null) {  
	                BufferedReader oIn = new BufferedReader(new InputStreamReader(  
	                        oConn.getInputStream()));  
	                if (null != oIn) {  
	                    String strFlag = oIn.readLine();  
	                    oIn.close();  
	                    System.out.println("strFlag"+strFlag);  
	                    if (strFlag.indexOf("type=\"unavailable\"") >= 0) {  
	                        shOnLineState = 2;  
	                    }  
	                    if (strFlag.indexOf("type=\"error\"") >= 0) {  
	                        shOnLineState = 0;  
	                    } else if (strFlag.indexOf("priority") >= 0  
	                            || strFlag.indexOf("id=\"") >= 0) {  
	                        shOnLineState = 1;  
	                    }  
	                }  
	            }  
	        } catch (Exception e) {  
	        	 shOnLineState = 3;  
	            e.printStackTrace();  
	        }  
	  
	        return shOnLineState;  
	    }




	public static ArrayList<Map<String,Object>> getAllContactsVcard() {
		return allContactsVcard;
	}




	public static void setAllContactsVcard(ArrayList<Map<String,Object>> allContactsVcard) {
		MyApplication.allContactsVcard = allContactsVcard;
	}  
	 
	 
//	 public  void sendFile(XMPPConnection connection,  
//	            String user, File file) throws XMPPException, InterruptedException {  
//	          
//	        System.out.println("发送文件开始"+file.getName());  
//	        FileTransferManager transfer = new FileTransferManager(connection);  
//	        System.out.println("发送文件给: "+user+"@"+hostName);  
//	        OutgoingFileTransfer out = transfer.createOutgoingFileTransfer(user+hostName);//  
//	          
//	        out.sendFile(file, file.getName());  
//	          
//	        System.out.println("//////////");  
//	        System.out.println(out.getStatus());  
//	        System.out.println(out.getProgress());  
//	        System.out.println(out.isDone());  
//	          
//	        System.out.println("//////////");  
//	          
//	        System.out.println("发送文件结束");  
//	    }
	 
	/**
	 * @author YouMingyang
	 * @param context
	 *            初始化图片加载器
	 */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"ConquerQuestion/cache");
		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCache(new UnlimitedDiscCache(cacheDir))
				.defaultDisplayImageOptions(displayImageOptions)
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		ImageLoader.getInstance().init(config);
	}

	private static final String BASEURL = "http://192.168.1.149:8000/";

	/**
	 * 上传问题
	 * 
	 * @param context
	 *            activity context
	 * @param question
	 *            上传问题
	 */
	public static void uploadQuestion(final Context context,
			final Question question) {
		final String baseUrl = BASEURL + "question/add";
		final String action = Constants.QUESTIONS;
		final String filePrefix = "file:///";
		LinkedList<NameValuePair> textParams = new LinkedList<NameValuePair>();
		textParams.add(new BasicNameValuePair("q_title", question.getQ_title()));
		textParams.add(new BasicNameValuePair("q_grade", question.getQ_grade()));
		textParams.add(new BasicNameValuePair("q_subject", question.getQ_subject()));
		textParams.add(new BasicNameValuePair("q_text_content", question.getQ_text_content()));
		textParams.add(new BasicNameValuePair("q_user", String.valueOf(question.getQ_user())));
		LinkedList<NameValuePair> fileParams = null;
		List<Resource> resources=question.getResourceList();
		if (resources != null&&resources.size()>0) {
			fileParams = new LinkedList<NameValuePair>();
			for (Resource resource : resources) {
				String filePath = resource.getResource_lpath();
				String name = filePath.substring(filePath.lastIndexOf("/") + 1);
				String value = filePath;
				if (value.startsWith(filePrefix)) {
					value = value.substring(filePrefix.length() + 1);
				}
				fileParams.add(new BasicNameValuePair(name, value));
			}
		}
		final AsyncHttpParams asyncHttpParams = new AsyncHttpParams(baseUrl,textParams, fileParams, action);
		isUploading = true;
		HttpTools.asyncUpload(context, asyncHttpParams);
	}

	/**
	 * 下载问题
	 * 
	 * @return
	 */
	public static void downloadQuestion(final Context context,
			final LinkedList<NameValuePair> params) {
		final String baseUrl = BASEURL + "question/get";
		final String action = Constants.QUESTIONS;
		LinkedList<NameValuePair> textParams = new LinkedList<NameValuePair>();
		textParams.add(new BasicNameValuePair("ak","7244d82a2ef54bfa015a0d7d6f85f372"));
		textParams.addAll(params);
		final AsyncHttpParams asyncHttpParams = new AsyncHttpParams(baseUrl,textParams, null, action);
		HttpTools.asyncDownload(context,asyncHttpParams);
	}

	/**
	 * 下载回复
	 * 
	 * @return
	 */
	public static void downloadReplys(final Context context, final long qId,
			final LinkedList<NameValuePair> params) {
		final String baseUrl = BASEURL + "questionReply/get/" + qId;
		final String action = Constants.REPLYS;
		LinkedList<NameValuePair> textParams = new LinkedList<NameValuePair>();
		textParams.add(new BasicNameValuePair("ak","7244d82a2ef54bfa015a0d7d6f85f372"));
		textParams.addAll(params);
		final AsyncHttpParams asyncHttpParams = new AsyncHttpParams(baseUrl,textParams, null, action);
		HttpTools.asyncDownload(context,asyncHttpParams);
	}

	/**
	 * 上传问题回复
	 * 
	 * @param context
	 * @param reply
	 *            回复
	 */
	public static void uploadReply(final Context context, final Reply reply) {
		final String baseUrl = BASEURL + "questionReply/add";
		final String action = Constants.REPLYS;
		final String filePrefix = "file:///";
		final String url = baseUrl + "/" + reply.getQr_q();
		LinkedList<NameValuePair> textParams = new LinkedList<NameValuePair>();
		textParams.add(new BasicNameValuePair("qr_text", reply.getQr_text()));
		textParams.add(new BasicNameValuePair("qr_user", String.valueOf(reply.getQr_user())));
		LinkedList<NameValuePair> fileParams = null;
		List<Resource> resources=reply.getResourceList();
		if (resources != null&&resources.size()>0) {
			fileParams = new LinkedList<NameValuePair>();
			for (Resource resource : resources) {
				String filePath = resource.getResource_lpath();
				String name = filePath.substring(filePath.lastIndexOf("/") + 1);
				String value = filePath;
				if (value.startsWith(filePrefix)) {
					value = value.substring(filePrefix.length() + 1);
				}
				fileParams.add(new BasicNameValuePair(name, value));
			}
		}
		final AsyncHttpParams asyncHttpParams = new AsyncHttpParams(url,
				textParams, fileParams, action);
		isUploading=true;
		HttpTools.asyncUpload(context, asyncHttpParams);
	}

	
	/**
	 * 上传校园故事
	 */
	public static void uploadStory(final Context context,
			final Story story) {
		final String baseUrl = BASEURL + "story/add";
		final String action = Constants.STORYS;
		LinkedList<NameValuePair> textParams = new LinkedList<NameValuePair>();
		textParams.add(new BasicNameValuePair("s_text", story.getS_text()));
		textParams.add(new BasicNameValuePair("s_user", String.valueOf(story.getS_user())));
		textParams.add(new BasicNameValuePair("s_title", story.getS_title()));
		final AsyncHttpParams asyncHttpParams = new AsyncHttpParams(baseUrl,textParams, null, action);
		isUploading = true;
		HttpTools.asyncUpload(context, asyncHttpParams);
	}

	
	/**
	 * 下载校园故事
	 * 
	 * @return
	 */
	public static void downloadStory(final Context context,
			final LinkedList<NameValuePair> params) {
		final String baseUrl = BASEURL + "story/get";
		final String action = Constants.STORYS;
		LinkedList<NameValuePair> textParams = new LinkedList<NameValuePair>();
		textParams.add(new BasicNameValuePair("ak","7244d82a2ef54bfa015a0d7d6f85f372"));
		textParams.addAll(params);
		final AsyncHttpParams asyncHttpParams = new AsyncHttpParams(baseUrl,textParams, null, action);
		HttpTools.asyncDownload(context,asyncHttpParams);
	}
	
	public static User getCurrentUser(Context context) {
		loginUser = new User(null, 1l, "jalsary", null, null,"assets://avatar.jpg", "robot", null, null, null, null, null,null, null, null, null);
		return loginUser;
	}

	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			OpenHelper helper = new DaoMaster.DevOpenHelper(context,
					"notes-db", null) {
				@Override
				public void onCreate(SQLiteDatabase db) {
					super.onCreate(db);
					final String delTblQuesTrigOne = "CREATE TRIGGER delQuesRes AFTER DELETE ON question BEGIN DELETE FROM resource WHERE resource_id=old.q_resource; END";
					final String delTblQuesTrigTwo = "CREATE TRIGGER delQuesRep AFTER DELETE ON question BEGIN DELETE FROM reply WHERE qr_q=old.q_id and qr_type=0; END";
					final String delTblRepTrigOne = "CREATE TRIGGER delRepRes AFTER DELETE ON reply BEGIN DELETE FROM resource WHERE resource_id=old.qr_resource; END";
					final String delTblRepTrigTwo = "CREATE TRIGGER delRepRep AFTER DELETE ON reply BEGIN DELETE FROM reply WHERE qr_q=old.qr_id and qr_type=1; END";
					
					db.execSQL(delTblQuesTrigOne);
					db.execSQL(delTblQuesTrigTwo);
					db.execSQL(delTblRepTrigOne);
					db.execSQL(delTblRepTrigTwo);
				}

			};
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

}
