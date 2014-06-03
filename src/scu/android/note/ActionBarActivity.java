package scu.android.note;

import java.util.Iterator;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.packet.VCard;

import scu.android.application.MyApplication;
import scu.android.db.ChatRecord;
import scu.android.db.DbManager2;
import scu.android.ui.AccountSettingActivity;
import scu.android.ui.ActivityMultiRoom;
import scu.android.ui.SearchFriendActivity;
import scu.android.ui.SearchGroupResultActivity;
import scu.android.ui.TabViewPagerAdapter;
import scu.android.util.TimeRender;
import scu.android.util.XmppTool;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
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
	
	ProgressDialog pd,searchGroupPd ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
	
//		pd = new ProgressDialog(this);
//		pd.setTitle("温馨提示");
//		pd.setMessage("正在登陆");
		handler.sendEmptyMessage(5);
		searchGroupPd = new ProgressDialog(this);
		searchGroupPd.setTitle("温馨提示");
		searchGroupPd.setMessage("正在搜索");
		
		// 创建Tab
		setupTest1();
		setupTest2();
		setupTest3();
		setupTest4();
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//		getActionBar().setDisplayShowTitleEnabled(false);
//		getActionBar().setDisplayShowHomeEnabled(false);
		
		
//		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_bar));
//		setTitle(R.layout);
		// 创建 view pager
		mViewPager = (ViewPager)findViewById(R.id.pager);
		getFragmentManager();

		mViewPager.setAdapter(new TabViewPagerAdapter(getSupportFragmentManager()));
		mViewPager.setOnPageChangeListener(new TestPagerListener());
		mViewPager.setCurrentItem(TAB_INDEX_TAB_2);
	}
	
	public void initialListener(){
		db = new DbManager2(this);
		ChatManager cm = XmppTool.getConnection().getChatManager();
		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) 
			{
				chat.addMessageListener(new MessageListener() {
					@Override
					public void processMessage(Chat chat2, Message message)
					{
						Log.v("--tags--", "--tags-form--"+message.getFrom());
						Log.v("--tags--", "--tags-message--"+message.getBody());
						
						if(message.getFrom().contains("@"+((MyApplication)getApplication()).hostName))
						{
							
							String[] args = new String[] { message.getFrom(), message.getBody(), TimeRender.getDate(), "IN" };
							
							
							android.os.Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.obj = args;
							handler.sendMessage(msg);
							
							 Intent intent = new Intent();
							 intent.setAction("cn.abel.action.broadcast");
							   
							   //要发送的内容
							 intent.putExtra("author", args[0]+"|"+args[1]+"|"+args[2]+"|"+args[3]);
							   
							   //发送 一个无序广播
							 sendBroadcast(intent);
//							if(handler2 !=null){
//								handler2.sendMessage(msg);
//							}
						}
						else
						{
							
							String[] args = new String[] { message.getFrom(), message.getBody(), TimeRender.getDate(), "IN" };
							
							
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
	
	public void showPopup(View v) {
	    PopupMenu popup = new PopupMenu(this, v);
	    popup.setOnMenuItemClickListener(new PopupItemClickListener());
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.add_popup_menu, popup.getMenu());
	    popup.show();
	}
	private class PopupItemClickListener implements OnMenuItemClickListener{

		@Override
		public boolean onMenuItemClick(MenuItem item) {
		    switch (item.getItemId()) {
		        case R.id.popup_menu_option_add_friend:
//		            archive(item);
		        	System.out.println("popup_menu_option_add_friend");
		        	startActivity(new Intent(ActionBarActivity.this,SearchFriendActivity.class));
		            return true;
		  
		        case R.id.popup_menu_option_add_group:
		        	
		        	final EditText et2 = new EditText(ActionBarActivity.this);
		        	new AlertDialog.Builder(ActionBarActivity.this).setTitle("添加群").setMessage("请输入群名称").setView(et2).setPositiveButton("确定", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if(et2.getText().toString().equals("")){
								Toast.makeText(ActionBarActivity.this, "群名称不能为空", Toast.LENGTH_SHORT).show();
								
							}
							else {
								android.os.Message msg = handler.obtainMessage();
								msg.what = 6;
								msg.obj = et2.getText().toString();
								handler.sendMessage(msg);
							}
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).create().show();
		        	return true;
		        	
		        case R.id.popup_menu_option_create_group:
		            Toast.makeText(getApplicationContext(), "add", Toast.LENGTH_SHORT).show();
		        	final EditText et = new EditText(ActionBarActivity.this);
		        	new AlertDialog.Builder(ActionBarActivity.this).setTitle("创建群").setMessage("请输入房间名").setView(et).setPositiveButton("创建", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if(et.getText().toString().equals("")){
								Toast.makeText(ActionBarActivity.this, "房间名不能为空", Toast.LENGTH_SHORT).show();
								
							}
							else {
								Intent intent = new Intent(ActionBarActivity.this, ActivityMultiRoom.class);
								intent.putExtra("jid", et.getText().toString() + "@conference"+"."+((MyApplication)getApplication()).hostName);
								intent.putExtra("action", "create");
								startActivity(intent);
							}
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).create().show();
		        	
		            return true;
		        default:
		            return false;
		    }
		}

		
	}

	
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg){
			switch(msg.what){
			case 1:
				final String[] args = (String[]) msg.obj;
//				if(((MyApplication)getActivity().getApplication()).currentActivity.equals("main")){
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
//				}
				 addRecordToDb(args);
				
				break;
				
			case 2:  //登陆成功
//				if(pd.isShowing()){
//					pd.dismiss();
//				}
				VCard vCard = new VCard();
				try {
					vCard.load(XmppTool.getConnection());
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if("".equals(vCard.getNickName()) || null == vCard.getNickName()){
					System.out.println("昵称是空的");
					vCard.setNickName("快乐的汤姆猫");
				}
				((MyApplication)getApplication()).vCard = vCard;
				System.out.println("登陆成功");
				break;
			case 3:
				if(pd.isShowing()){
					pd.dismiss();
				}
//				System.out.println("获取头像失败");
				break;
			case 4:
				
				break;
			case 5:
//				pd.show();
				new Thread(new Runnable() {				
					public void run() {
						
						
						try {
							

							XmppTool.getConnection().login(((MyApplication)getApplication()).userName,((MyApplication)getApplication()).passWord);
//						             新建presence对象״̬
							Presence presence = new Presence(Presence.Type.available);
							XmppTool.getConnection().sendPacket(presence);
							
							System.out.println("正在登陆");
							handler.sendEmptyMessage(2);
							((MyApplication)getApplication()).roster = XmppTool.getConnection().getRoster();
								
							((MyApplication)getApplication()).entries = ((MyApplication)getApplication()).getAllEntries();
							
							initialListener();
							
						}
						catch (XMPPException e) 
						{
							XmppTool.closeConnection();
							
//							handler.sendEmptyMessage(2);
						}					
					}
				}).start();
				break;
			case 6:
//				pd.show();
				String roomName = (String)msg.obj;
				if(checkResult(roomName)){
					Intent intent = new Intent();
					intent.putExtra("roomName",roomName);
					intent.setClass(ActionBarActivity.this,SearchGroupResultActivity.class);
					startActivity(intent);
				}
				else {
					Toast.makeText(getApplicationContext(), "未找到该群", Toast.LENGTH_SHORT).show();
				}
				break;
			case 7:
				break;
				default:
					break;
			}
			super.handleMessage(msg);
			
		}

	};
	public void initList(){
		
	}
	private void setupTest1(){
	
		Tab tab = this.getActionBar().newTab();
//		tab.setContentDescription("Tab 1");
	
//		tab.setCustomView(R.id.);	
		
		tab.setText("消息");
		
		tab.setTabListener(mTabListener);
		getActionBar().addTab(tab);
	}
	
	 @Override
	    public boolean onMenuItemSelected(int featureId, MenuItem item) {
	        switch (item.getItemId()) {
	        case R.id.home_add_btn:

	        	View v = findViewById(R.id.home_add_btn);
	        	showPopup(v);

	            break;
	        case R.id.home_search_btn:
	            Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
	            break;
	        case R.id.home_settings_btn:
	        	Intent intent = new Intent();
	        	intent.setClass(this,AccountSettingActivity.class);
				
//				intent.setClass(this,LoginAndRegistActivity.class);
				startActivity(intent);
//	            Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
	            break;
	           
	        }        
	        return false;
	    }

	private void setupTest2(){
		Tab tab = this.getActionBar().newTab();
//		tab.setContentDescription("Tab 2");
		tab.setText("破题");
		tab.setTabListener(mTabListener);
		getActionBar().addTab(tab);
	}

	private void setupTest3(){
		Tab tab = this.getActionBar().newTab();
//		tab.setContentDescription("Tab 3");
		tab.setText("通讯录");
		tab.setTabListener(mTabListener);
		getActionBar().addTab(tab);
	}

	private void setupTest4(){
		Tab tab = this.getActionBar().newTab();
//		tab.setContentDescription("Tab 4");
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

	class TestPagerListener implements OnPageChangeListener{
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
		XmppTool.closeConnection();
    	System.exit(0);
	}
	
	
	public void addRecordToDb(String[] args){
//		if(((MyApplication)getActivity().getApplication()).currentActivity.equals("main")){
			ChatRecord chatRecord = new ChatRecord();
			String s = args[0].toString().contains("@")?args[0].toString().split("@")[0]:args[0].toString();
			chatRecord.setAccount(s);
			chatRecord.setContent(args[1]);
			chatRecord.setFlag("in");
			chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
			chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
			chatRecord.setType("0");
			System.out.println("写入数据库成功，内容为： "+args[1]);
			db.insertRecord(chatRecord);
			
//		}
		
		
	}
	
	
	
	public boolean checkResult(String name){
		
		ServiceDiscoveryManager discoManager = ServiceDiscoveryManager
				.getInstanceFor(XmppTool.getConnection());

		// 获得指定XMPP实体的项目
		// 这个例子获得与在线目录服务相关的项目
		DiscoverItems discoItems;
		try {
			discoItems = discoManager
					.discoverItems("conference"+"."+((MyApplication)getApplication()).hostName);
			// 获得被查询的XMPP实体的要查看的项目
			
			Iterator it = discoItems.getItems();
			// 显示远端XMPP实体的项目
			while (it.hasNext()) {
				DiscoverItems.Item item = (DiscoverItems.Item) it.next();
				if(item.getName().equals(name)){
					
					return true;
				}
				else continue;
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
}
