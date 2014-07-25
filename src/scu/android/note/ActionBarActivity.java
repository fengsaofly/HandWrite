package scu.android.note;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.VCard;

import scu.android.activity.IssueQuestionActivity;
import scu.android.application.MyApplication;
import scu.android.db.ChatRecord;
import scu.android.db.DbManager2;
import scu.android.ui.AccountSettingActivity;
import scu.android.ui.ActivityMultiRoom;
import scu.android.ui.LoginActivity;
import scu.android.ui.MListView;
import scu.android.ui.QuestionClassifyListAdapter;
import scu.android.ui.SearchFriendActivity;
import scu.android.ui.SearchGroupResultActivity;
import scu.android.ui.TabViewPagerAdapter;
import scu.android.util.Constants;
import scu.android.util.TimeRender;
import scu.android.util.XmppTool;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.demo.note.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ActionBarActivity extends FragmentActivity {

	private PagerSlidingTabStrip tabs;
	private ViewPager mViewPager;
	private SlidingMenu slidingMenu;
	private static String status = null;
	private static String grade = null;
	private static String subject = null;
	private static String nearby = null;

	DbManager2 db;
	MediaPlayer mp;
	// MyBroadcastReciver myBroadcastReciver;
	ProgressDialog pd, searchGroupPd;
	// MenuItem tempItem;
	PopupMenu popup;
	Menu menu = null;
	private ProgressBar pb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		//
		// IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction("cn.abel.action.loginBroadcast");
		// myBroadcastReciver = new MyBroadcastReciver();
		// this.registerReceiver(myBroadcastReciver, intentFilter);

		// pd = new ProgressDialog(this);
		// pd.setTitle("温馨提示");
		// pd.setMessage("正在登陆");
		// handler.sendEmptyMessage(5);

		// getMyVcard();

		if (((MyApplication) getApplication()).loginFlag) {
			initialListener();
		}
		pb = new ProgressBar(this);
		pb.setVisibility(View.GONE);

		searchGroupPd = new ProgressDialog(this);
		searchGroupPd.setTitle("温馨提示");
		searchGroupPd.setMessage("正在搜索");

		setOverflowShowingAlways();
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(new TabViewPagerAdapter(getSupportFragmentManager()));
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(mViewPager);

		setTabsValue();
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setCurrentItem(1);

		initSlidingMenu();
		tabs.setSlidingMenu(slidingMenu);
	}

	private void setTabsValue() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#5cace4"));
		// 设置选中Tab文字的颜色
		tabs.setSelectedTextColor(Color.parseColor("#5cace4"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu, menu);
		MenuItem plus = menu.findItem(R.id.home_add_btn);
		if (plus.getActionProvider() == null)
			plus.setActionProvider(new PlusActionProvider(this));
		MenuItem login = menu.findItem(R.id.popup_menu_option_login);
		if (((MyApplication) getApplication()).loginFlag) {
			login.setIcon(((MyApplication) getApplication()).myIconDrawable);
			login.setTitle(((MyApplication) getApplication()).nickName);
		}
		return true;
	}

	public void initSlidingMenu() {
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setBehindWidth(700);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// slidingMenu.setMenu(R.layout.question_classify_choose);
		View v = LayoutInflater.from(this).inflate(R.layout.question_classify_choose, null);
		MListView list = (MListView) v.findViewById(R.id.question_classify_list);
		list.setAdapter(new QuestionClassifyListAdapter(this));
		// actionbarsherlock与supportv7包冲突，只能定义view啦
		slidingMenu.setMenu(v);
	}

	public void OnClick(View v) {
		switch (v.getId()) {
		case R.id.complete:
			Toast.makeText(this, "完成", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Constants.QUESTIONS);
			intent.putExtra("action", "special");
			intent.putExtra("grade", grade);
			intent.putExtra("subject", subject);
			intent.putExtra("status", status);
			intent.putExtra("nearby", nearby);
			sendBroadcast(intent);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (slidingMenu.isMenuShowing()) {
			slidingMenu.showContent();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.popup_menu_option_login:
			if (((MyApplication) getApplication()).loginFlag == true) {
				// item.setTitle(((MyApplication)getApplication()).userName);
				startActivity(new Intent(ActionBarActivity.this, AccountSettingActivity.class));
			} else {
				// item.setTitle("登陆");
				startActivity(new Intent(ActionBarActivity.this, LoginActivity.class));
				/**
				 * @author YouMingyang
				 */
				// finish();
			}
			break;
		case R.id.home_search_btn:
			Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
			break;
		case R.id.home_settings_btn:
			// View sv = findViewById(R.id.home_settings_btn);
			// showPopup(sv);
			Intent intent = new Intent();
			intent.setClass(this, AccountSettingActivity.class);
			//
			// // intent.setClass(this,LoginAndRegistActivity.class);
			startActivity(intent);
			// Toast.makeText(getApplicationContext(), "settings",
			// Toast.LENGTH_SHORT).show();
			break;
		case R.id.home_add_btn:
			item.setActionProvider(new PlusActionProvider(this));
			break;
		}
		return true;
	}

	public class PlusActionProvider extends ActionProvider {

		private Context context;

		public PlusActionProvider(Context context) {
			super(context);
			this.context = context;
		}

		@Override
		public View onCreateActionView() {
			return null;
		}

		@Override
		public void onPrepareSubMenu(SubMenu subMenu) {

			subMenu.clear();
			subMenu.add(context.getString(R.string.issue_question)).setIcon(R.drawable.issue_question).setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					// if (((MyApplication) getApplication()).loginFlag
					// == false) {
					// changeTologin();
					// // Toast.makeText(ActionBarActivity.this,
					// // "请先登录",
					// // Toast.LENGTH_SHORT).show();
					// } else {
					startActivity(new Intent(ActionBarActivity.this, IssueQuestionActivity.class));
					// }
					return false;
				}
			});

			subMenu.add(context.getString(R.string.add_friend)).setIcon(R.drawable.add_friend).setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					// archive(item);
					if (((MyApplication) getApplication()).loginFlag == false) {
						changeTologin();
						// Toast.makeText(ActionBarActivity.this,
						// "请先登录",
						// Toast.LENGTH_SHORT).show();
					} else {
						startActivity(new Intent(ActionBarActivity.this, SearchFriendActivity.class));
					}
					System.out.println("popup_menu_option_add_friend");
					return false;
				}
			});
			subMenu.add(context.getString(R.string.add_group)).setIcon(R.drawable.add_group).setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if (((MyApplication) getApplication()).loginFlag == false) {
						changeTologin();
						// Toast.makeText(ActionBarActivity.this,
						// "请先登录",
						// Toast.LENGTH_SHORT).show();
					} else {
						final EditText et2 = new EditText(ActionBarActivity.this);
						new AlertDialog.Builder(ActionBarActivity.this).setTitle("添加群").setMessage("请输入群名称").setView(et2).setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method
								// stub
								if (et2.getText().toString().equals("")) {
									Toast.makeText(ActionBarActivity.this, "群名称不能为空", Toast.LENGTH_SHORT).show();

								} else {
									android.os.Message msg = handler.obtainMessage();
									msg.what = 6;
									msg.obj = et2.getText().toString();
									handler.sendMessage(msg);
								}
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method
								// stub

							}
						}).create().show();
					}
					return false;
				}
			});
			subMenu.add(context.getString(R.string.create_group)).setIcon(R.drawable.add_group).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					if (((MyApplication) getApplication()).loginFlag == false) {
						// Toast.makeText(ActionBarActivity.this,
						// "请先登录",
						// Toast.LENGTH_SHORT).show();
						changeTologin();
					} else {
						final EditText et = new EditText(ActionBarActivity.this);
						new AlertDialog.Builder(ActionBarActivity.this).setTitle("创建群").setMessage("请输入房间名").setView(et).setPositiveButton("创建", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method
								// stub
								if (et.getText().toString().equals("")) {
									Toast.makeText(ActionBarActivity.this, "房间名不能为空", Toast.LENGTH_SHORT).show();

								} else {
									Intent intent = new Intent(ActionBarActivity.this, ActivityMultiRoom.class);
									intent.putExtra("jid", et.getText().toString() + "@conference" + "." + ((MyApplication) getApplication()).hostName);
									intent.putExtra("action", "create");
									startActivity(intent);
								}
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method
								// stub

							}
						}).create().show();
					}
					// Toast.makeText(getApplicationContext(), "add",
					// Toast.LENGTH_SHORT).show();
					return false;
				}
			});
		}

		@Override
		public boolean hasSubMenu() {
			return true;
		}
	}

	public void getMyVcard() {
		Map<String, Object> map = new HashMap<String, Object>();
		VCard vcard = ((MyApplication) getApplication()).getUserVcard(XmppTool.getConnection(), ((MyApplication) getApplication()).userName);
		ByteArrayInputStream bais = null;
		bais = ((MyApplication) getApplication()).getUserImage(XmppTool.getConnection(), ((MyApplication) getApplication()).userName);
		if (bais != null) {
			map.put("my_avatar", ((MyApplication) getApplication()).bitmap2Drawable(BitmapFactory.decodeStream(bais)));
		} else
			map.put("my_avatar", ActionBarActivity.this.getResources().getDrawable(R.drawable.default_avatar));

		if (vcard.getNickName() == null || vcard.getNickName().equals("")) {
			map.put("my_nickName", "");
		} else
			map.put("my_nickName", vcard.getNickName());
		System.out.println("vcard.getNickName():  " + vcard.getNickName());
		if (vcard.getMiddleName() == null || vcard.getMiddleName().equals("")) {
			map.put("my_carrer", "");
		} else
			map.put("my_carrer", vcard.getMiddleName());
		System.out.println(" vcard.getMiddleName()" + vcard.getMiddleName());
		if (vcard.getFirstName() == null || vcard.getFirstName().equals("")) {
			map.put("my_gender", "");
		} else
			map.put("my_gender", vcard.getFirstName());
		System.out.println("vcard.getFirstName():" + vcard.getFirstName());
		if (vcard.getAddressFieldHome("zone") == null || vcard.getAddressFieldHome("zone").equals("")) {
			map.put("my_zone", "");
		} else
			map.put("my_zone", vcard.getAddressFieldHome("zone"));
		System.out.println("vcard.getAddressFieldHome():" + vcard.getAddressFieldHome("zone"));
		if (vcard.getLastName() == null || vcard.getLastName().equals("")) {
			map.put("my_sign", "");
		} else
			map.put("my_sign", vcard.getLastName());
		System.out.println("vcard.getLastName():" + vcard.getLastName());
		// map.put("friend_nickName", vcard.getNickName());
		// map.put("friend_carrer", vcard.getMiddleName());
		// map.put("friend_gender", vcard.getFirstName());
		// map.put("friend_zone", vcard.getAddressFieldHome("zone"));
		// map.put("friend_sign", vcard.getLastName());
		((MyApplication) getApplication()).myVcard = map;

	}

	public void initialListener() {
		db = new DbManager2(this);
		ChatManager cm = XmppTool.getConnection().getChatManager();
		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(new MessageListener() {
					@Override
					public void processMessage(Chat chat2, Message message) {
						Log.v("--tags--", "--tags-form--" + message.getFrom());
						Log.v("--tags--", "--tags-message--" + message.getBody());

						if (message.getFrom().contains("@" + ((MyApplication) getApplication()).hostName)) {

							String[] args = new String[] { message.getFrom(), message.getBody(), TimeRender.getDate(), "IN" };

							android.os.Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.obj = args;
							if (!message.getBody().equals("") && !message.getBody().equals("null")) {
								handler.sendMessage(msg);
								Intent intent = new Intent();
								intent.setAction("cn.abel.action.broadcast");

								// 要发送的内容
								intent.putExtra("author", args[0] + "|" + args[1] + "|" + args[2] + "|" + args[3]);

								// 发送 一个无序广播
								sendBroadcast(intent);
							}

							// if(handler2 !=null){
							// handler2.sendMessage(msg);
							// }
						} else {

							String[] args = new String[] { message.getFrom(), message.getBody(), TimeRender.getDate(), "IN" };

							android.os.Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.obj = args;
							if (!message.getBody().equals("") && !message.getBody().equals("null")) {
								handler.sendMessage(msg);
							}
						}

					}
				});
			}
		});

		/**
		 * 
		 * 
		 * 文件监听
		 */

		FileTransferManager transfer = new FileTransferManager(XmppTool.getConnection());
		transfer.addFileTransferListener(new RecFileTransferListener());

		/**
		 * 
		 * 
		 * packet监听
		 */

		// PacketFilter filter = new AndFilter(new PacketTypeFilter(
		// Presence.class));
		// PacketListener listener = new PacketListener() {
		//
		// @Override
		// public void processPacket(Packet packet) {
		// db = new DbManager2(ActionBarActivity.this);
		// Log.i("Presence", "PresenceService------" + packet.toXML());
		// //看API可知道 Presence是Packet的子类
		// if (packet instanceof Presence) {
		// Log.i("Presence", packet.toXML());
		// Presence presence = (Presence) packet;
		// //Presence还有很多方法，可查看API
		// String from = presence.getFrom();//发送方
		// String to = presence.getTo();//接收方
		// System.out.println("发送方： "+from);
		// System.out.println("接收方： "+to);
		// //Presence.Type有7中状态
		//
		// if (presence.getType().equals(Presence.Type.subscribe)) {//好友申请
		// if(to.contains(MyApplication.userName)||to.contains(MyApplication.nickName)){
		// String[] args = new String[] { from,
		// "subscribe", TimeRender.getDate(),
		//
		// "IN" };
		// addFriendReqRecordToDb(args);
		// }
		//
		// System.out.println("好友申请");
		// }
		// else if (presence.getType().equals(
		// Presence.Type.subscribed)) {//同意添加好友
		// if(to.contains(MyApplication.userName)||to.contains(MyApplication.nickName)){
		// String[] args = new String[] { from,
		// "subscribed", TimeRender.getDate(),
		// "IN" };
		// addFriendReqRecordToDb(args);
		// }
		// System.out.println("同意添加好友  ");
		// }
		// else if (presence.getType().equals(
		// Presence.Type.unsubscribe)) {//拒绝添加好友 和 删除好友
		// if(to.contains(MyApplication.userName)||to.contains(MyApplication.nickName)){
		// String[] args = new String[] { from,
		// "unsubscribe", TimeRender.getDate(),
		// "IN" };
		// addFriendReqRecordToDb(args);
		// }
		// System.out.println("拒绝添加好友  和 删除好友 ");
		// }
		//
		// else if (presence.getType().equals(
		// Presence.Type.unavailable)) {//好友下线 要更新好友列表，可以在这收到包后，发广播到指定页面 更新列表
		// System.out.println("好友申请");
		// }
		// else {//好友上线
		//
		// }
		// }
		// if(db!=null){
		// db.close();
		// }
		// }
		// };
		//
		// XmppTool.getConnection().addPacketListener(listener, filter); //注册监听

	}

	private FileTransferRequest request;
	private File file;

	class RecFileTransferListener implements FileTransferListener {
		@Override
		public void fileTransferRequest(FileTransferRequest prequest) {

			File file2 = new File(((MyApplication) getApplication()).getSDCardPath() + "/ConquerQustion" + "/voice/");
			if (!file2.exists())
				file2.mkdirs();
			file = new File(file2.getAbsolutePath() + "/" + prequest.getFileName());

			// file = new
			// File(((MyApplication)getApplication()).getSDCardPath()+"/ConquerQustion"+"/voice/"
			// + prequest.getFileName());
			request = prequest;
			System.out.println("接收到的文件描述：   " + "request.getDescription():" + request.getDescription() + "request.getRequestor(): " + request.getRequestor());
			handler.sendEmptyMessage(5);
		}
	}

	public void changeTologin() {
		Intent intent = new Intent();
		intent.setClass(ActionBarActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				final String[] args = (String[]) msg.obj;
				// if(((MyApplication)getActivity().getApplication()).currentActivity.equals("main")){
				System.out.println("进入handlerXXX");
				mp = MediaPlayer.create(ActionBarActivity.this, R.raw.messagewarn);
				mp.setLooping(false);

				mp.start();
				mp.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {
						// TODO Auto-generated method stub

						mp.release();

					}
				});
				// }
				addRecordToDb(args);

				break;

			case 2:
				// 附件进度条
				if (pb.getVisibility() == View.GONE) {
					pb.setMax(100);
					pb.setProgress(1);
					pb.setVisibility(View.VISIBLE);
				}
				break;
			case 3:
				pb.setProgress(msg.arg1);
				break;
			case 4:
				// Toast.makeText(ActionBarActivity.this,"接收完成",Toast.LENGTH_SHORT).show();
				pb.setVisibility(View.GONE);
				mp = MediaPlayer.create(ActionBarActivity.this, R.raw.messagewarn);
				mp.setLooping(false);

				mp.start();
				mp.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {
						// TODO Auto-generated method stub

						mp.release();

					}
				});
				Intent fileintent = new Intent();
				fileintent.setAction("cn.abel.action.filebroadcast");

				// 要发送的内容

				String[] fileDesc = new String[] { request.getRequestor().toString().split("@")[0], file.getAbsolutePath(), TimeRender.getDate(), "IN" };
				// 发送 一个无序广播

				fileintent.putExtra("fileName", fileDesc[0] + "|" + fileDesc[1] + "|" + fileDesc[2] + "|" + fileDesc[3]);
				sendBroadcast(fileintent);
				addFileRecordToDb(fileDesc);

				break;

			case 5:// 接收附件
				final IncomingFileTransfer infiletransfer = request.accept();
				try {
					infiletransfer.recieveFile(file);
				} catch (XMPPException e) {
					Toast.makeText(ActionBarActivity.this, "接收失败!", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}

				handler.sendEmptyMessage(2);

				Timer timer = new Timer();
				TimerTask updateProgessBar = new TimerTask() {
					public void run() {
						if ((infiletransfer.getStatus() == FileTransfer.Status.error) || (infiletransfer.getStatus() == FileTransfer.Status.refused)
								|| (infiletransfer.getStatus() == FileTransfer.Status.cancelled)) {
							System.out.println("接收文件出错");
							handler.sendEmptyMessage(7);

						} else if ((infiletransfer.getAmountWritten() >= request.getFileSize()) || (infiletransfer.getStatus() == FileTransfer.Status.complete)) {
							System.out.println("接收文件完成");
							handler.sendEmptyMessage(4);
							cancel();
						} else {
							long p = infiletransfer.getAmountWritten() * 100L / infiletransfer.getFileSize();

							android.os.Message message = handler.obtainMessage();
							message.arg1 = Math.round((float) p);
							message.what = 3;
							message.sendToTarget();
							// Toast.makeText(ActionBarActivity.this,"接收完成!",Toast.LENGTH_SHORT).show();
						}
					}
				};
				timer.scheduleAtFixedRate(updateProgessBar, 10L, 10L);
				// //提示框
				// AlertDialog.Builder builder = new
				// AlertDialog.Builder(ActionBarActivity.this);
				//
				// builder.setTitle("附件：")
				// .setCancelable(false)
				// .setMessage("是否接收文件："+file.getName()+"?")
				// .setPositiveButton("接受",
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int id) {
				// try
				// {
				// infiletransfer.recieveFile(file);
				// }
				// catch (XMPPException e)
				// {
				// Toast.makeText(ActionBarActivity.this,"接收失败!",Toast.LENGTH_SHORT).show();
				// e.printStackTrace();
				// }
				//
				// handler.sendEmptyMessage(2);
				//
				// Timer timer = new Timer();
				// TimerTask updateProgessBar = new TimerTask() {
				// public void run() {
				// if ( (infiletransfer.getStatus() ==
				// FileTransfer.Status.error)
				// || (infiletransfer.getStatus() ==
				// FileTransfer.Status.refused)
				// || (infiletransfer.getStatus() ==
				// FileTransfer.Status.cancelled)
				// )
				// {
				// System.out
				// .println("接收文件出错");
				// handler.sendEmptyMessage(7);
				//
				//
				// }
				// else if((infiletransfer.getAmountWritten() >=
				// request.getFileSize())
				// ||(infiletransfer.getStatus() ==
				// FileTransfer.Status.complete)){
				// System.out
				// .println("接收文件完成");
				// handler.sendEmptyMessage(4);
				// cancel();
				// }
				// else
				// {
				// long p = infiletransfer.getAmountWritten() * 100L /
				// infiletransfer.getFileSize();
				//
				// android.os.Message message = handler.obtainMessage();
				// message.arg1 = Math.round((float) p);
				// message.what = 3;
				// message.sendToTarget();
				// //
				// Toast.makeText(ActionBarActivity.this,"接收完成!",Toast.LENGTH_SHORT).show();
				// }
				// }
				// };
				// timer.scheduleAtFixedRate(updateProgessBar, 10L, 10L);
				// dialog.dismiss();
				// }
				// })
				// .setNegativeButton("取消",
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int id)
				// {
				// request.reject();
				// dialog.cancel();
				// }
				// }).show();

				break;
			case 6:
				// pd.show();
				String roomName = (String) msg.obj;
				if (checkResult(roomName)) {
					Intent intent = new Intent();
					intent.putExtra("roomName", roomName);
					intent.setClass(ActionBarActivity.this, SearchGroupResultActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), "未找到该群", Toast.LENGTH_SHORT).show();
				}
				break;
			case 7: // 发送文件失败
				Toast.makeText(ActionBarActivity.this, "接收文件失败", Toast.LENGTH_SHORT).show();
				// if(menu!=null)
				// menu.getItem(0).setTitle(((MyApplication)getApplication()).userName);
				//
				// popup.getMenuInflater().inflate(R.menu.settings_popup_menu,
				// menu);
				break;
			// case 8:
			// if(menu!=null)
			// menu.getItem(0).setTitle("登陆");
			// popup.getMenuInflater().inflate(R.menu.settings_popup_menu,
			// menu);
			// break;
			default:
				break;
			}
			super.handleMessage(msg);

		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// XmppTool.closeConnection();

		if (db != null)
			db.close();
		// System.exit(0);
	}

	public void addRecordToDb(String[] args) {
		// if(((MyApplication)getActivity().getApplication()).currentActivity.equals("main")){
		ChatRecord chatRecord = new ChatRecord();
		String s = args[0].toString().contains("@") ? args[0].toString().split("@")[0] : args[0].toString();

		chatRecord.setAccount(s);
		chatRecord.setContent(args[1]);
		chatRecord.setFlag("in");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("0");
		chatRecord.setIsGroupChat("false");
		chatRecord.setJid("-1");
		chatRecord.setContent_type("nomal");
		System.out.println("写入数据库成功，内容为： " + args[1]);
		db.insertRecord(chatRecord);

		// }

	}

	public void addFriendReqRecordToDb(String[] args) {
		// if(((MyApplication)getActivity().getApplication()).currentActivity.equals("main")){
		ChatRecord chatRecord = new ChatRecord();
		// String s = args[0].toString().contains("@") ?
		// args[0].toString().split(
		// "@")[0] : args[0].toString();

		chatRecord.setAccount(args[0]);
		chatRecord.setContent(args[1]);
		chatRecord.setFlag("in");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("0");
		chatRecord.setIsGroupChat("false");
		chatRecord.setJid("-1");
		chatRecord.setContent_type("nomal");
		System.out.println("写入数据库成功，内容为： " + args[1]);
		db.insertRecord(chatRecord);

		// }

	}

	public void addFileRecordToDb(String[] args) {
		// if(((MyApplication)getActivity().getApplication()).currentActivity.equals("main")){
		ChatRecord chatRecord = new ChatRecord();
		String s = args[0].toString().contains("@") ? args[0].toString().split("@")[0] : args[0].toString();

		chatRecord.setAccount(s);
		chatRecord.setContent(args[1]);
		chatRecord.setFlag("in");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("0");
		chatRecord.setIsGroupChat("false");
		chatRecord.setJid("-1");
		chatRecord.setContent_type("unnomal");
		System.out.println("写入数据库成功，内容为： " + args[1]);
		db.insertRecord(chatRecord);

		// }

	}

	public boolean checkResult(String name) {

		ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(XmppTool.getConnection());

		// 获得指定XMPP实体的项目
		// 这个例子获得与在线目录服务相关的项目
		DiscoverItems discoItems;
		try {
			discoItems = discoManager.discoverItems("conference" + "." + ((MyApplication) getApplication()).hostName);
			// 获得被查询的XMPP实体的要查看的项目

			Iterator it = discoItems.getItems();
			// 显示远端XMPP实体的项目
			while (it.hasNext()) {
				DiscoverItems.Item item = (DiscoverItems.Item) it.next();
				if (item.getName().equals(name)) {

					return true;
				} else
					continue;
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}

		return false;

	}

	// private class MyBroadcastReciver extends BroadcastReceiver {
	// public void onReceive(Context context, Intent intent) {
	// String action = intent.getAction();
	// if (action.equals("cn.abel.action.loginBroadcast")) {
	// Boolean loginFlag = intent.getBooleanExtra("loginFlag",false);
	//
	// // 在控制台显示接收到的广播内容
	// System.out.println("收到广播，loginFlag" + loginFlag);
	// if(loginFlag){
	// handler.sendEmptyMessage(7);
	// }
	// else{
	// handler.sendEmptyMessage(8);
	// }
	//
	//
	// // 在android端显示接收到的广播内容
	// // Toast.makeText(ChatMainActivity.this, author, 1).show();
	//
	// // 在结束时可取消广播
	// // MainActivity.this.unregisterReceiver(this);
	// }
	// }
	// }

	// @Override
	// protected void onResume() {
	// super.onResume();
	// /**
	// * @author YouMingyang
	// */
	// Intent intent = getIntent();
	// if (intent.getAction().equals(
	// "scu.android.activity.IssueQuestionActivity")) {
	// // handler.removeMessages(5);
	//
	// mViewPager.setCurrentItem(TAB_INDEX_TAB_2);
	// }
	// }

}
