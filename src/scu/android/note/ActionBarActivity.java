package scu.android.note;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import scu.android.activity.IssueQuestionActivity;
import scu.android.application.MyApplication;
import scu.android.db.ChatRecord;
import scu.android.db.DbManager2;
import scu.android.fragment.QuestionsFragment;
import scu.android.ui.AccountSettingActivity;
import scu.android.ui.TabViewPagerAdapter;
import scu.android.util.TimeRender;
import scu.android.util.XmppTool;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.demo.note.R;

public class ActionBarActivity extends FragmentActivity {

	public final static int TAB_INDEX_TAB_1 = 0;
	public final static int TAB_INDEX_TAB_2 = 1;
	public final static int TAB_INDEX_TAB_3 = 2;
	public final static int TAB_INDEX_TAB_4 = 3;
	public final static int TAB_COUNT = 4;
	DbManager2 db;
	private ViewPager mViewPager;
	MediaPlayer mp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		// new Thread(new Runnable() {
		// public void run() {
		//
		// try {
		//
		// XmppTool.getConnection().login(
		// ((MyApplication) getApplication()).userName,
		// ((MyApplication) getApplication()).passWord);
		// // 新建presence对象״̬
		// Presence presence = new Presence(Presence.Type.available);
		// XmppTool.getConnection().sendPacket(presence);
		//
		// System.out.println("正在登陆");
		// // handler.sendEmptyMessage(1);
		// ((MyApplication) getApplication()).roster = XmppTool
		// .getConnection().getRoster();
		//
		// ((MyApplication) getApplication()).entries = ((MyApplication)
		// getApplication())
		// .getAllEntries();
		//
		// initialListener();
		//
		// } catch (XMPPException e) {
		// XmppTool.closeConnection();
		//
		// // handler.sendEmptyMessage(2);
		// }
		// }
		// }).start();

		// 创建Tab
		setupTest1();
		setupTest2();
		setupTest3();
		setupTest4();
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// getActionBar().setDisplayShowTitleEnabled(false);
		// getActionBar().setDisplayShowHomeEnabled(false);

		// getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_bar));
		// setTitle(R.layout);
		// 创建 view pager
		mViewPager = (ViewPager) findViewById(R.id.pager);
		getFragmentManager();

		mViewPager.setAdapter(new TabViewPagerAdapter(
				getSupportFragmentManager()));
		mViewPager.setOnPageChangeListener(new TestPagerListener());
		mViewPager.setCurrentItem(TAB_INDEX_TAB_2);

		initData();
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
						Log.v("--tags--",
								"--tags-message--" + message.getBody());

						if (message
								.getFrom()
								.contains(
										"@"
												+ ((MyApplication) getApplication()).hostName)) {

							String[] args = new String[] { message.getFrom(),
									message.getBody(), TimeRender.getDate(),
									"IN" };

							android.os.Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.obj = args;
							handler.sendMessage(msg);

							Intent intent = new Intent();
							intent.setAction("cn.abel.action.broadcast");

							// 要发送的内容
							intent.putExtra("author", args[0] + "|" + args[1]
									+ "|" + args[2] + "|" + args[3]);

							// 发送 一个无序广播
							sendBroadcast(intent);
							// if(handler2 !=null){
							// handler2.sendMessage(msg);
							// }
						} else {

							String[] args = new String[] { message.getFrom(),
									message.getBody(), TimeRender.getDate(),
									"IN" };

							android.os.Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.obj = args;
							handler.sendMessage(msg);
						}

					}
				});
			}
		});
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				final String[] args = (String[]) msg.obj;
				// if(((MyApplication)getActivity().getApplication()).currentActivity.equals("main")){
				System.out.println("进入handlerXXX");
				mp = MediaPlayer.create(ActionBarActivity.this,
						R.raw.messagewarn);
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
				// System.out.println("获取头像成功");
				// ((MyApplication)getApplication()).getIconFlag = true;
				break;
			case 3:
				// System.out.println("获取头像失败");
				break;
			case 4:

				break;
			case 5:// 接收附件

				break;
			}
			super.handleMessage(msg);

		}

	};

	public void initList() {

	}

	// 初始化数据
	public void initData() {
		// if (UserDao.getUsers(this).size() == 0) {
		// User user1 = new User(null, "小不点儿", "avatar.jpg", 'M', "高二",
		// "123456789@qq.com", "123456789");
		// User user2 = new User(null, "小米", "avatar.jpg", 'F', "高二",
		// "3322323@qq.com", "232332");
		// int user1_id = (int) UserDao.insertUser(user1, this);
		// int user2_id = (int) UserDao.insertUser(user2, this);
		// Question question = new Question(null, "这道题怎么做???", "question.jpg",
		// Calendar.getInstance(), "1.8千米", null, "高二", "地理", false,
		// user1_id);
		// int question_id = (int) QuestionDao.insertQuestion(question, this);
		// Reply reply = new Reply(null, "不知道", "question.jpg", null,
		// Calendar.getInstance(), user2_id, question_id);
		// ReplyDao.insertRely(reply, this);
		// }
	}

	private void setupTest1() {

		Tab tab = this.getActionBar().newTab();
		// tab.setContentDescription("Tab 1");

		// tab.setCustomView(R.id.);

		tab.setText("消息");

		tab.setTabListener(mTabListener);
		getActionBar().addTab(tab);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home_add_btn:
			Intent aIntent = new Intent(ActionBarActivity.this,
					IssueQuestionActivity.class);
			startActivity(aIntent);
			break;
		case R.id.home_search_btn:
			Toast.makeText(getApplicationContext(), "search",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.home_settings_btn:
			Intent intent = new Intent();
			intent.setClass(this, AccountSettingActivity.class);

			// intent.setClass(this,LoginAndRegistActivity.class);
			startActivity(intent);
			// Toast.makeText(getApplicationContext(), "settings",
			// Toast.LENGTH_SHORT).show();
			break;
		}
		return false;
	}

	private void setupTest2() {
		Tab tab = this.getActionBar().newTab();
		// tab.setContentDescription("Tab 2");
		tab.setText("破题");
		tab.setTabListener(mTabListener);
		getActionBar().addTab(tab);
	}

	private void setupTest3() {
		Tab tab = this.getActionBar().newTab();
		// tab.setContentDescription("Tab 3");
		tab.setText("通讯录");
		tab.setTabListener(mTabListener);
		getActionBar().addTab(tab);
	}

	private void setupTest4() {
		Tab tab = this.getActionBar().newTab();
		// tab.setContentDescription("Tab 4");
		tab.setText("发现");
		tab.setTabListener(mTabListener);
		getActionBar().addTab(tab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu, menu);
		return true;
	}

	private final TabListener mTabListener = new TabListener() {
		private final static String TAG = "TabListener";

		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onTabReselected");
		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onTabSelected()");
			if (mViewPager != null)
				mViewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onTabUnselected()");
		}
	};

	class TestPagerListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageSelected(int arg0) {
			getActionBar().selectTab(getActionBar().getTabAt(arg0));
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		XmppTool.closeConnection();
		System.exit(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if (intent.getAction().equals("scu.android.activity.IssueQuestionActivity")) {
			mViewPager.setCurrentItem(TAB_INDEX_TAB_2);
		}
	}

	public void addRecordToDb(String[] args) {
		// if(((MyApplication)getActivity().getApplication()).currentActivity.equals("main")){
		ChatRecord chatRecord = new ChatRecord();
		String s = args[0].toString().contains("@") ? args[0].toString().split(
				"@")[0] : args[0].toString();
		chatRecord.setAccount(s);
		chatRecord.setContent(args[1]);
		chatRecord.setFlag("in");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("0");
		System.out.println("写入数据库成功，内容为： " + args[1]);
		db.insertRecord(chatRecord);

		// }

	}
}
