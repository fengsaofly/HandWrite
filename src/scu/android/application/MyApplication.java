package scu.android.application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.search.UserSearchManager;

import scu.android.util.XmppTool;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
//	public static String hostIp = "218.244.144.212";
//	public String hostName = "handwriteserver";

	 public static String hostIp = "192.168.1.116";
	 public String hostName = "dolphin0520-pc";

	public String userName = "jalsary";
	public String passWord = "123456";

	public List<RosterGroup> groups = new ArrayList<RosterGroup>();
	public Roster roster;
	public List<RosterEntry> entries;
	public XMPPConnection mConnection;
	public boolean loginFlag = false;
	public int firstIn = 1;

	public static VCard vCard = null;
	public SharedPreferences sp = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// initial();
		initImageLoader();
		System.out.println("oncreate   ");
		sp = getSharedPreferences("poti", MODE_PRIVATE);

	}

	public void initial() {
		// mConnection = XmppTool.getConnection();
		new Thread(new Runnable() {
			public void run() {

				try {

					XmppTool.getConnection().login(userName, passWord);
					// 新建presence对象״̬
					Presence presence = new Presence(Presence.Type.available);
					XmppTool.getConnection().sendPacket(presence);

					System.out.println("正在登陆");
					handler.sendEmptyMessage(1);
					roster = XmppTool.getConnection().getRoster();

					entries = getAllEntries();

				} catch (XMPPException e) {
					XmppTool.closeConnection();

					handler.sendEmptyMessage(2);
				}
			}
		}).start();
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
	public Map<String, String> iconMap;
	public Map<String, String> cityMap;
	public Map<String, String> signMap;

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
	  
//	        ByteArrayInputStream bais = new ByteArrayInputStream(vcard.getAvatar());  
//	        FormatTools.getInstance().InputStream2Bitmap(bais);  
	  
	        vcard.save(connection);  
	    }  

	public static Bitmap getUserImage(XMPPConnection connection,
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
		Bitmap bitmap = BitmapFactory.decodeStream(bais);
		
		return bitmap;
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

	public static void sendFile(String userName, String passWord, String user,
			File file) {
		try {
			XMPPConnection connection = XmppTool.getConnection();
			System.out.println("发送文件开始" + file.getName());
			FileTransferManager transfer = new FileTransferManager(connection);
			String destination = user + "/spark";
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
				//.memoryCacheExtraOptions(200, 200)
				// max width, max height.discCacheExtraOptions(400, 400,
				// CompressFormat.JPEG, 75) // Can slow ImageLoader, use it
				// carefully (Better don't use it)
				.threadPoolSize(10)
				.threadPriority(Thread.NORM_PRIORITY - 1)
				.denyCacheImageMultipleSizesInMemory()
				.offOutOfMemoryHandling()
				.memoryCache(new UsingFreqLimitedMemoryCache(4 * 1024 * 1024))
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
   
	
	
	 public static VCard getUserVCard(XMPPConnection connection) throws XMPPException {
		  VCard vcard = new VCard();
		  vcard.load(connection);
		  System.out.println(vcard.getField("sex"));
		  System.out.println(vcard.getField("DESC"));
		  System.out.println(vcard.getEmailHome());
		  System.out.println(vcard.getOrganization());
		  System.out.println(vcard.getNickName());
		  System.out.println(vcard.getPhoneWork("PHONE"));
		  System.out.println(vcard.getProperty("DESC"));
		  System.out.println(vcard.getAvatar());
		  return vcard;
		 }
		 
		 
		 public static VCard getUserVCard(XMPPConnection connection, String userJid)
		   throws XMPPException {
		  VCard vcard = new VCard();
		  vcard.load(connection,userJid);
		  System.out.println(vcard.getOrganization());
		  System.out.println(vcard.getField("sex"));
		  System.out.println(vcard.getNickName());
		  System.out.println(vcard.getAvatar());
		  System.out.println(vcard.getField("DESC"));
		  return vcard;
		 }
		 public static void setUserVCard(XMPPConnection connection,int type,String value)
		   throws XMPPException {
		  VCard vCard = new VCard();
		  vCard.load(connection);
		  switch(type){
		  case 1:
			  vCard.setOrganization(value);
			  break;
		  case 2:
			  vCard.setNickName(value);
			  break;
		  case 3:
			  vCard.setField("sex", value);
			  break;
		  case 4:
			  vCard.setEmailHome(value);
			  break;
		  case 5:
			  vCard.setEmailWork(value);
			  break;
		  }
		 
		  vCard.save(connection);
		  System.out.println("添加成功");
		 }
		 
	

}
