package scu.android.ui;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;

import scu.android.application.MyApplication;
import scu.android.db.ChatRecord;
import scu.android.db.DbManager2;
import scu.android.util.ChatMsgEntity;
import scu.android.util.ChatMsgViewAdapter;
import scu.android.util.TimeRender;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.note.R;

public class ChatMainActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private Button mBtnSend;
	private TextView mBtnRcd,currentChat = null;
	private Button mBtnBack;
	ImageButton right_btn = null;
	private EditText mEditTextContent;
	private RelativeLayout mBottom;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private boolean isShosrt = false;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;
	private ImageView img1, sc_img1;
//	private SoundMeter mSensor;
	private View rcChat_popup;
	private LinearLayout del_re;
	private ImageView chatting_mode_btn, volume;
	private boolean btn_vocie = false;
	private int flag = 1;
	private Handler mHandler = new Handler();
	private String voiceName;
	String chatContact = "";
	Chat newchat;
	private long startVoiceT, endVoiceT;
	MediaPlayer mp;
	private Cursor cursor;
	private DbManager2 db;
	MyBroadcastReciver myBroadcastReciver;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.abel.action.broadcast");
        myBroadcastReciver = new MyBroadcastReciver();
        this.registerReceiver( myBroadcastReciver, intentFilter);
//		((MyApplication)getApplication()).currentActivity="chat";
		//((MyApplication)getApplication()).handler=this.handler;
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Intent intent = getIntent();
		db = new DbManager2(this);
		chatContact = intent.getStringExtra("currentContact");
		
		ChatManager cm = XmppTool.getConnection().getChatManager();
		
		newchat = cm.createChat(chatContact+"@"+((MyApplication)getApplication()).hostName, null);
	
		
//		cm.addChatListener(new ChatManagerListener() {
//			@Override
//			public void  chatCreated(Chat chat, boolean able) 
//			{
//				chat.addMessageListener(new MessageListener() {
//					@Override
//					public void processMessage(Chat chat2, Message message)
//					{
//						Log.v("--tags--", "--tags-form--"+message.getFrom());
//						Log.v("--tags--", "--tags-message--"+message.getBody());
//						
//						if(message.getFrom().contains(chatContact+"@"+((MyApplication)getApplication()).hostName))
//						{
//							
//							String[] args = new String[] { message.getFrom(), message.getBody(), TimeRender.getDate(), "IN" };
//							
//							
//							android.os.Message msg = handler.obtainMessage();
//							msg.what = 1;
//							msg.obj = args;
//							handler.sendMessage(msg);
//						}
//						else
//						{
//							
//							String[] args = new String[] { message.getFrom(), message.getBody(), TimeRender.getDate(), "IN" };
//							
//							
//							android.os.Message msg = handler.obtainMessage();
//							msg.what = 1;
//							msg.obj = args;
//							handler.sendMessage(msg);
//						}
//						
//					}
//				});
//			}
//		});
		initView();

		initData();
	}
	
//	public class Msg {
//		String userid;
//		String msg;
//		String date;
//		String from;
//
//		public Msg(String userid, String msg, String date, String from) {
//			this.userid = userid;
//			this.msg = msg;
//			this.date = date;
//			this.from = from;
//		}
//	}
	
	private class MyBroadcastReciver extends BroadcastReceiver {  
		  public void onReceive(Context context, Intent intent) {
		   String action = intent.getAction();
		   if(action.equals("cn.abel.action.broadcast")) {
		    String author = intent.getStringExtra("author");
		    
		    //在控制台显示接收到的广播内容
		    System.out.println("author==>"+author);
		    String p1 = author.toString().split("\\|")[0];
		    String p2 = author.toString().split("\\|")[1];
		    String p3 = author.toString().split("\\|")[2];
		    String p4 = author.toString().split("\\|")[3];
		    System.out.println("p1: "+p1+"   p2:"+p2+"  p3:"+p3+"  p4:"+p4);
		    String[] args =author.split("\\|");
			
			
			android.os.Message msg = handler.obtainMessage();
			msg.what = 1;
			msg.obj = args;
			handler.sendMessage(msg);
		    //在android端显示接收到的广播内容
		    //Toast.makeText(ChatMainActivity.this, author, 1).show();
		    
		    //在结束时可取消广播
		    //MainActivity.this.unregisterReceiver(this);
		   }
	 }
	}

	public void initView() {
		
		currentChat = (TextView)findViewById(R.id.currentChat);
		currentChat.setText(chatContact);
		right_btn = (ImageButton)findViewById(R.id.right_btn);
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		mBtnBack.setOnClickListener(this);
		chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
//		volume = (ImageView) this.findViewById(R.id.volume);
//		rcChat_popup = this.findViewById(R.id.rcChat_popup);
//		img1 = (ImageView) this.findViewById(R.id.img1);
//		sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
//		del_re = (LinearLayout) this.findViewById(R.id.del_re);
//		voice_rcd_hint_rcding = (LinearLayout) this
//				.findViewById(R.id.voice_rcd_hint_rcding);
//		voice_rcd_hint_loading = (LinearLayout) this
//				.findViewById(R.id.voice_rcd_hint_loading);
//		voice_rcd_hint_tooshort = (LinearLayout) this
//				.findViewById(R.id.voice_rcd_hint_tooshort);
//		mSensor = new SoundMeter();
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		
		//语音文字切换按钮
		chatting_mode_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

//				if (btn_vocie) {
//					mBtnRcd.setVisibility(View.GONE);
//					mBottom.setVisibility(View.VISIBLE);
//					btn_vocie = false;
//					chatting_mode_btn
//							.setImageResource(R.drawable.chatting_setmode_msg_btn);
//
//				} else {
//					mBtnRcd.setVisibility(View.VISIBLE);
//					mBottom.setVisibility(View.GONE);
//					chatting_mode_btn
//							.setImageResource(R.drawable.chatting_setmode_voice_btn);
//					btn_vocie = true;
//				}
			}
		});
		mBtnRcd.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				//按下语音录制按钮时返回false执行父类OnTouch
				return false;
			}
		});
		
		right_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(ChatMainActivity.this, ContactDetailActivity.class);
//				intent.putExtra("contactName", chatContact);
//				startActivity(intent);
			}
		});
		
		
	}
	


//	private String[] msgArray = new String[] { "有人就有恩怨","有恩怨就有江湖","人就是江湖","你怎么退出？ ","生命中充满了巧合","两条平行线也会有相交的一天。"};
//
//	private String[] dataArray = new String[] { "2012-10-31 18:00",
//			"2012-10-31 18:10", "2012-10-31 18:11", "2012-10-31 18:20",
//			"2012-10-31 18:30", "2012-10-31 18:35"};
//	private final static int COUNT = 6;
//
	public void initData() {
//		for (int i = 0; i < COUNT; i++) {
//			ChatMsgEntity entity = new ChatMsgEntity();
//			entity.setDate(dataArray[i]);
//			if (i % 2 == 0) {
//				entity.setName("白富美");
//				entity.setMsgType(true);
//			} else {
//				entity.setName("高富帅");
//				entity.setMsgType(false);
//			}
//
//			entity.setText(msgArray[i]);
//			mDataArrays.add(entity);
		mDataArrays = new ArrayList<ChatMsgEntity>();
		cursor = db.readRecord(chatContact, TimeRender.getDate().split(" ")[0]);
		cursor.moveToPosition(-1);
		while(cursor.moveToNext()){
			System.out.println("1: "+cursor.getString(1)+"2: "+cursor.getString(2)+"3: "+cursor.getString(3));
			if(cursor.getString(4).equals("in"))
			mDataArrays.add(new ChatMsgEntity(cursor.getString(1), cursor.getString(2), cursor.getString(3), true));
			else 
				mDataArrays.add(new ChatMsgEntity(((MyApplication)getApplication()).userName, cursor.getString(2), cursor.getString(3), false));
		}
		
		mAdapter = new ChatMsgViewAdapter(this, mDataArrays,chatContact);
		mListView.setAdapter(mAdapter);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			finish();
			break;
		}
	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(TimeRender.getDate());
			entity.setName(((MyApplication)getApplication()).userName);
			entity.setMsgType(false);
			entity.setText(contString);
			
			try {
				
				newchat.sendMessage(contString);

			} 
			catch (XMPPException e)
			{
				e.printStackTrace();
			}

			mDataArrays.add(entity);//增加一个信息
			mAdapter.notifyDataSetChanged();//更新视图

			mEditTextContent.setText("");//清空输入框

			mListView.setSelection(mListView.getCount() - 1);//设置当前listView的选中行为最后一行
//			if(((MyApplication)getApplication()).currentActivity.equals("chat")){
				ChatRecord chatRecord = new ChatRecord();
				chatRecord.setAccount(chatContact);
				chatRecord.setContent(contString);
				chatRecord.setFlag("out");
				chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
				chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
				chatRecord.setType("0");
				db.insertRecord(chatRecord);
//			}
			
		}
	}
	
	
	
	public  Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) 
		{
							
			switch (msg.what) {
			case 1:
//				if(((MyApplication)getApplication()).currentActivity.equals("chat")){
//				 mp = MediaPlayer.create(ChatMainActivity.this, R.raw.messagewarn);
//					mp.setLooping(false);
//					
//					mp.start();
//					 mp.setOnCompletionListener(new OnCompletionListener() {
//							
//							@Override
//							public void onCompletion(MediaPlayer arg0) {
//								// TODO Auto-generated method stub
//								
//								mp.release();
//								
//							}
//					    });
//				}
				System.out.println("收到消息，内容为：  "+msg.obj.toString());
				String[] args = (String[]) msg.obj;
				String s = args[0].toString().contains("@")?args[0].toString().split("@")[0]:args[0].toString();
				mDataArrays.add(new ChatMsgEntity(s, args[2], args[1], true));
				
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount() - 1);
				
//				if(((MyApplication)getApplication()).currentActivity.equals("chat")){
//					ChatRecord chatRecord = new ChatRecord();
//					
//					chatRecord.setAccount(args[0].replace(s, "").replace("/spark",""));
//					chatRecord.setContent(args[1]);
//					chatRecord.setFlag("in");
//					chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
//					chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
//					db.insertRecord(chatRecord);
//				}
				break;			
			case 2:
				
				break;
			case 3:
				
				break;
			case 4:
				
				break;
			case 5:
//				
				break;
			default:
				break;
			}
		};
	};

	

//	//按下语音录制按钮时
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//
//		if (!Environment.getExternalStorageDirectory().exists()) {
//			Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
//			return false;
//		}
//
//		if (btn_vocie) {
//			System.out.println("1");
//			int[] location = new int[2];
//			mBtnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
//			int btn_rc_Y = location[1];
//			int btn_rc_X = location[0];
//			int[] del_location = new int[2];
//			del_re.getLocationInWindow(del_location);
//			int del_Y = del_location[1];
//			int del_x = del_location[0];
//			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
//				if (!Environment.getExternalStorageDirectory().exists()) {
//					Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
//					return false;
//				}
//				System.out.println("2");
//				if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {//判断手势按下的位置是否是语音录制按钮的范围内
//					System.out.println("3");
//					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
//					rcChat_popup.setVisibility(View.VISIBLE);
//					voice_rcd_hint_loading.setVisibility(View.VISIBLE);
//					voice_rcd_hint_rcding.setVisibility(View.GONE);
//					voice_rcd_hint_tooshort.setVisibility(View.GONE);
//					mHandler.postDelayed(new Runnable() {
//						public void run() {
//							if (!isShosrt) {
//								voice_rcd_hint_loading.setVisibility(View.GONE);
//								voice_rcd_hint_rcding
//										.setVisibility(View.VISIBLE);
//							}
//						}
//					}, 300);
//					img1.setVisibility(View.VISIBLE);
//					del_re.setVisibility(View.GONE);
//					startVoiceT = SystemClock.currentThreadTimeMillis();
//					voiceName = startVoiceT + ".amr";
//					start(voiceName);
//					flag = 2;
//				}
//			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {//松开手势时执行录制完成
//				System.out.println("4");
//				mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
//				if (event.getY() >= del_Y
//						&& event.getY() <= del_Y + del_re.getHeight()
//						&& event.getX() >= del_x
//						&& event.getX() <= del_x + del_re.getWidth()) {
//					rcChat_popup.setVisibility(View.GONE);
//					img1.setVisibility(View.VISIBLE);
//					del_re.setVisibility(View.GONE);
//					stop();
//					flag = 1;
//					File file = new File(android.os.Environment.getExternalStorageDirectory()+"/"
//									+ voiceName);
//					if (file.exists()) {
//						file.delete();
//					}
//				} else {
//
//					voice_rcd_hint_rcding.setVisibility(View.GONE);
//					stop();
//					endVoiceT = SystemClock.currentThreadTimeMillis();
//					flag = 1;
//					int time = (int) ((endVoiceT - startVoiceT) / 1000);
//					if (time < 1) {
//						isShosrt = true;
//						voice_rcd_hint_loading.setVisibility(View.GONE);
//						voice_rcd_hint_rcding.setVisibility(View.GONE);
//						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
//						mHandler.postDelayed(new Runnable() {
//							public void run() {
//								voice_rcd_hint_tooshort
//										.setVisibility(View.GONE);
//								rcChat_popup.setVisibility(View.GONE);
//								isShosrt = false;
//							}
//						}, 500);
//						return false;
//					}
//					ChatMsgEntity entity = new ChatMsgEntity();
//					entity.setDate(TimeRender.getDate());
//					entity.setName("高富帅");
//					entity.setMsgType(false);
//					entity.setTime(time+"\"");
//					entity.setText(voiceName);
//					mDataArrays.add(entity);
//					mAdapter.notifyDataSetChanged();
//					mListView.setSelection(mListView.getCount() - 1);
//					rcChat_popup.setVisibility(View.GONE);
//
//				}
//			}
//			if (event.getY() < btn_rc_Y) {//手势按下的位置不在语音录制按钮的范围内
//				System.out.println("5");
//				Animation mLitteAnimation = AnimationUtils.loadAnimation(this,
//						R.anim.cancel_rc);
//				Animation mBigAnimation = AnimationUtils.loadAnimation(this,
//						R.anim.cancel_rc2);
//				img1.setVisibility(View.GONE);
//				del_re.setVisibility(View.VISIBLE);
//				del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
//				if (event.getY() >= del_Y
//						&& event.getY() <= del_Y + del_re.getHeight()
//						&& event.getX() >= del_x
//						&& event.getX() <= del_x + del_re.getWidth()) {
//					del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
//					sc_img1.startAnimation(mLitteAnimation);
//					sc_img1.startAnimation(mBigAnimation);
//				}
//			} else {
//
//				img1.setVisibility(View.VISIBLE);
//				del_re.setVisibility(View.GONE);
//				del_re.setBackgroundResource(0);
//			}
//		}
//		return super.onTouchEvent(event);
//	}

//	private static final int POLL_INTERVAL = 300;
//
//	private Runnable mSleepTask = new Runnable() {
//		public void run() {
//			stop();
//		}
//	};
//	private Runnable mPollTask = new Runnable() {
//		public void run() {
////			double amp = mSensor.getAmplitude();
////			updateDisplay(amp);
//			mHandler.postDelayed(mPollTask, POLL_INTERVAL);
//
//		}
//	};

//	private void start(String name) {
////		mSensor.start(name);
//		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
//	}
//
//	private void stop() {
//		mHandler.removeCallbacks(mSleepTask);
//		mHandler.removeCallbacks(mPollTask);
////		mSensor.stop();
//		volume.setImageResource(R.drawable.amp1);
//	}
//
//	private void updateDisplay(double signalEMA) {
//		
//		switch ((int) signalEMA) {
//		case 0:
//		case 1:
//			volume.setImageResource(R.drawable.amp1);
//			break;
//		case 2:
//		case 3:
//			volume.setImageResource(R.drawable.amp2);
//			
//			break;
//		case 4:
//		case 5:
//			volume.setImageResource(R.drawable.amp3);
//			break;
//		case 6:
//		case 7:
//			volume.setImageResource(R.drawable.amp4);
//			break;
//		case 8:
//		case 9:
//			volume.setImageResource(R.drawable.amp5);
//			break;
//		case 10:
//		case 11:
//			volume.setImageResource(R.drawable.amp6);
//			break;
//		default:
//			volume.setImageResource(R.drawable.amp7);
//			break;
//		}
//	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		cursor.close();
		db.close();
		this.unregisterReceiver(myBroadcastReciver);
		super.onDestroy();
	}

	public void head_xiaohei(View v) { // 标题栏 返回按钮

	}
}