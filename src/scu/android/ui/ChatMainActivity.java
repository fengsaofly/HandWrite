package scu.android.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;

import scu.android.application.MyApplication;
import scu.android.db.ChatRecord;
import scu.android.db.DbManager2;
import scu.android.util.AppUtils;
import scu.android.util.ChatMsgEntity;
import scu.android.util.ChatMsgViewAdapter;
import scu.android.util.Constants;
import scu.android.util.TimeRender;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.demo.note.R;

public class ChatMainActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private TextView mBtnSend;
	private TextView mBtnRcd, currentChat = null;
	private Button mBtnBack;
	ImageButton right_btn = null;
	private EditText mEditTextContent;
	private LinearLayout mBottom;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private boolean isShosrt = false;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;
	private ImageView img1, sc_img1;
	// private SoundMeter mSensor;

	private LinearLayout del_re;
	private ImageView chatting_mode_btn, volume;
	private boolean btn_vocie = false;
	private int flag = 1;

	private String voiceName;
	String chatContact = "";
	Chat newchat;

	MediaPlayer mp;
	private Cursor cursor;
	private DbManager2 db;
	MyBroadcastReciver myBroadcastReciver;

	/**
	 * 
	 * 
	 * 底部录音相关
	 * 
	 * 
	 */

	// TextView second = null;
	TextView add_record = null;
	TextView testInclude = null;
	TextView add_extras = null;
	MediaRecorder mr = null;
	String path = null;
	MediaPlayer voiceMp = null;
	// PopupWindow pop;
	View extrasView, view2;
	AnimationDrawable animaition;
	private long startVoiceT, endVoiceT;
	// LinearLayout popup_recode_lay = null, popup_camera_lay = null,
	// popup_imgpicker_lay = null, popup_handwrite_lay = null;
	LinearLayout question_popup_layout = null;
	LinearLayout rcChat_popup = null;

	// LinearLayout yourVocie_lay = null;
	// RelativeLayout record_pressbtn_lay = null, play_lay = null;
	Button record_pressbtn = null;
	private Handler mHandler;
	boolean recordFlag = false;

	private View addDoodle;
	private View addHWrite;
	private View addPhotos;
	private View addCamera;
	private String cameraName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("cn.abel.action.broadcast");
		myBroadcastReciver = new MyBroadcastReciver();
		this.registerReceiver(myBroadcastReciver, intentFilter);
		// ((MyApplication)getApplication()).currentActivity="chat";
		// ((MyApplication)getApplication()).handler=this.handler;
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Intent intent = getIntent();
		db = new DbManager2(this);
		chatContact = intent.getStringExtra("currentContact");

		ChatManager cm = XmppTool.getConnection().getChatManager();

		newchat = cm.createChat(chatContact + "@"
				+ ((MyApplication) getApplication()).hostName, null);

		initView();

		initData();

		intialRecord();
	}

	public class MyBroadcastReciver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("cn.abel.action.broadcast")) {
				String author = intent.getStringExtra("author");

				// 在控制台显示接收到的广播内容
				System.out.println("author==>" + author);
				String p1 = author.toString().split("\\|")[0];
				String p2 = author.toString().split("\\|")[1];
				String p3 = author.toString().split("\\|")[2];
				String p4 = author.toString().split("\\|")[3];
				System.out.println("p1: " + p1 + "   p2:" + p2 + "  p3:" + p3
						+ "  p4:" + p4);
				String[] args = author.split("\\|");

				android.os.Message msg = handler.obtainMessage();
				msg.what = 1;
				msg.obj = args;
				handler.sendMessage(msg);
				// 在android端显示接收到的广播内容
				// Toast.makeText(ChatMainActivity.this, author, 1).show();

				// 在结束时可取消广播
				// MainActivity.this.unregisterReceiver(this);
			}
		}
	}

	public class FileReciver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("cn.abel.action.filebroadcast")) {
				String fileName = intent.getStringExtra("fileName");
				// 在控制台显示接收到的广播内容
				System.out.println("author==>" + fileName);
				String p1 = fileName.toString().split("\\|")[0];
				String p2 = fileName.toString().split("\\|")[1];
				String p3 = fileName.toString().split("\\|")[2];
				String p4 = fileName.toString().split("\\|")[3];
				System.out.println("p1: " + p1 + "   p2:" + p2 + "  p3:" + p3
						+ "  p4:" + p4);
				String[] args = fileName.split("\\|");

				android.os.Message msg = handler.obtainMessage();
				msg.what = 2;
				msg.obj = args;
				handler.sendMessage(msg);

			}
		}
	}

	public void intialRecord() {

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				System.out.println("进入handler");
				switch (msg.what) {
				case 0:
				case 1:
					testInclude.setText("1");
					volume.setImageResource(R.drawable.amp1);
					break;
				case 2:
				case 3:
					testInclude.setText("2");
					volume.setImageResource(R.drawable.amp2);

					break;
				case 4:
				case 5:
					testInclude.setText("3");
					volume.setImageResource(R.drawable.amp3);
					break;
				case 6:
				case 7:
					testInclude.setText("4");
					volume.setImageResource(R.drawable.amp4);
					break;
				case 8:
				case 9:
					testInclude.setText("5");
					volume.setImageResource(R.drawable.amp5);
					break;
				case 10:
				case 11:
					testInclude.setText("6");
					volume.setImageResource(R.drawable.amp6);
					break;
				default:
					testInclude.setText("7");
					volume.setImageResource(R.drawable.amp7);
					break;
				}
				super.handleMessage(msg);
			}

		};

		rcChat_popup = (LinearLayout) findViewById(R.id.rcChat_popup);

		question_popup_layout = (LinearLayout) findViewById(R.id.question_popup_layout);
		// record_pressbtn_lay = (RelativeLayout)
		// findViewById(R.id.record_pressbtn_lay);
		add_record = (TextView) findViewById(R.id.add_record);
		record_pressbtn = (Button) findViewById(R.id.record_pressbtn);
		// second = (TextView) findViewById(R.id.volume);
		// play_lay = (RelativeLayout) findViewById(R.id.play_lay);
		// yourVocie_lay = (LinearLayout) findViewById(R.id.yourVocie_lay);

		// question_add_showrecord_imgview.setImageResource(R.anim.playvoice);
		LayoutInflater inflater = LayoutInflater.from(this);
		// LayoutInflater inflater2 = LayoutInflater.from(this);
		// 引入窗口配置文件

		extrasView = inflater.inflate(R.layout.question_popup_layout, null);
		addDoodle = extrasView.findViewById(R.id.popup_doodle_lay);
		addCamera = extrasView.findViewById(R.id.popup_camera_lay);
		addPhotos = extrasView.findViewById(R.id.popup_imgpicker_lay);
		addHWrite = extrasView.findViewById(R.id.popup_handwrite_lay);
//
//		addDoodle.setOnClickListener(this);
//		addCamera.setOnClickListener(this);
//		addPhotos.setOnClickListener(this);
//		addHWrite.setOnClickListener(this);
		// popup_recode_lay = (LinearLayout) view
		// .findViewById(R.id.popup_record_lay);
		// popup_camera_lay = (LinearLayout) view
		// .findViewById(R.id.popup_camera_lay);
		// popup_imgpicker_lay = (LinearLayout) view
		// .findViewById(R.id.popup_imgpicker_lay);
		// popup_handwrite_lay = (LinearLayout) view
		// .findViewById(R.id.popup_handwrite_lay);
		// view2 = inflater2.inflate(R.layout.voice_rcd_hint_window, null);
		volume = (ImageView) findViewById(R.id.volume2);
		testInclude = (TextView) findViewById(R.id.testInclude);
		// 创建PopupWindow对象

		// pop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT, false);
		//
		// // // 需要设置一下此参数，点击外边可消失
		// //
		// pop.setBackgroundDrawable(new ColorDrawable());
		//
		// // 设置点击窗口外边窗口消失
		//
		// pop.setOutsideTouchable(true);
		//
		// // 设置此参数获得焦点，否则无法点击
		//
		// pop.setFocusable(true);

		// play = (Button)findViewById(R.id.play);
		add_record.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// TODO Auto-generated method stub
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:

					System.out.println("按下录音。。。");
					rcChat_popup.setVisibility(View.VISIBLE);
					startVoiceT = System.currentTimeMillis();
					// 按住事件发生后执行代码的区域
					mr = new MediaRecorder();
					File file2 = new File(MyApplication.getSDCardPath()
							+ "/ConquerQustion" + "/"
							+ ((MyApplication) getApplication()).userName + "/"
							+ "Audio");

					if (!file2.exists())
						file2.mkdirs();
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyyMMdd_HHmmss");
					String time = df.format(new Date());

					File file = new File(file2.getAbsolutePath() + "/" + time
							+ ".amr");

					//
					Toast.makeText(getApplicationContext(),
							"正在录音，录音文件在" + file.getAbsolutePath(),
							Toast.LENGTH_LONG).show();

					path = file.getAbsolutePath();
					System.out.println("路径： " + path);
					// start(path);

					mr.setAudioSource(MediaRecorder.AudioSource.MIC);
					mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

					// 设置输出文件
					mr.setOutputFile(file.getAbsolutePath());

					try {
						// 创建文件
						file.createNewFile();
						// 准备录制
						mr.prepare();

						mr.start();
						start();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					// 开始录制

					break;

				case MotionEvent.ACTION_MOVE:
					// 移动事件发生后执行代码的区域
					break;

				case MotionEvent.ACTION_UP:
					System.out.println("s松开录音。。。");
					endVoiceT = System.currentTimeMillis();
					if (mr != null) {
						mr.stop();
						mr.release();
						mr = null;
					}
					stop();
					int seconds = (int) (endVoiceT - startVoiceT) / 1000;
					if (seconds < 1) {
						seconds = 1;
					}
					// yourVocie_lay.setVisibility(View.VISIBLE);
					// second.setText("" + seconds);

					rcChat_popup.setVisibility(View.GONE);
					Toast.makeText(getApplicationContext(), "录音完毕",
							Toast.LENGTH_LONG).show();
					// play.setVisibility(View.VISIBLE);
					recordFlag = true;
					// add_record
					// .setBackgroundDrawable(getResources().getDrawable(R.drawable.question_add_record_icon_default));

					sendFile();

					// 松开事件发生后执行代码的区域
					break;

				default:

					break;
				}
				return true;
			}
		});

		//
		// play_lay.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// try {
		// // voiceMp = new MediaPlayer();
		// if (voiceMp != null) {
		// voiceMp.reset();
		// voiceMp.release();
		// voiceMp = null;
		// }
		// voiceMp = new MediaPlayer();
		// voiceMp.setDataSource(path);
		//
		// voiceMp.prepare();
		// voiceMp.setLooping(false);
		// voiceMp.start();
		// voiceMp.setOnCompletionListener(new OnCompletionListener() {
		//
		// @Override
		// public void onCompletion(MediaPlayer arg0) {
		// // TODO Auto-generated method stub
		// voiceMp.release();
		// voiceMp = null;
		// animaition.stop();
		// // question_add_showrecord_imgview
		// // .setImageResource(R.drawable.question_add_record_icon_default);
		// }
		// });
		// // question_add_showrecord_imgview
		// // .setImageResource(R.anim.playvoice);
		// // animaition = (AnimationDrawable) question_add_showrecord_imgview
		// // .getDrawable();
		// // 最后，就可以启动动画了，代码如下：
		//
		// // 是否仅仅启动一次？
		//
		// animaition.setOneShot(false);
		//
		// if (animaition.isRunning())// 是否正在运行？
		//
		// {
		// animaition.stop();// 停止
		//
		// }
		// animaition.start();// 启动
		// } catch (IllegalStateException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// }
		// });
		//
	}

	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};

	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mr.getMaxAmplitude() / 2700.0;
			System.out.println("amp: " + amp);
			// updateDisplay(amp);
			Message msg = mHandler.obtainMessage();
			msg.what = (int) amp;
			mHandler.sendMessage(msg);

			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start() {
		// mr.start();
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		// mr.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	public void initView() {

		currentChat = (TextView) findViewById(R.id.currentChat);
		currentChat.setText(chatContact);
		right_btn = (ImageButton) findViewById(R.id.right_btn);
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (TextView) findViewById(R.id.chat_send);
		mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBottom = (LinearLayout) findViewById(R.id.chat_input_field);
		mBtnBack.setOnClickListener(this);
		// chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
		// volume = (ImageView) this.findViewById(R.id.volume);
		// rcChat_popup = this.findViewById(R.id.rcChat_popup);
		// img1 = (ImageView) this.findViewById(R.id.img1);
		// sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
		// del_re = (LinearLayout) this.findViewById(R.id.del_re);
		// voice_rcd_hint_rcding = (LinearLayout) this
		// .findViewById(R.id.voice_rcd_hint_rcding);
		// voice_rcd_hint_loading = (LinearLayout) this
		// .findViewById(R.id.voice_rcd_hint_loading);
		// voice_rcd_hint_tooshort = (LinearLayout) this
		// .findViewById(R.id.voice_rcd_hint_tooshort);
		// mSensor = new SoundMeter();
		mEditTextContent = (EditText) findViewById(R.id.input_field);

		// 语音文字切换按钮
		// chatting_mode_btn.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		//
		// // if (btn_vocie) {
		// // mBtnRcd.setVisibility(View.GONE);
		// // mBottom.setVisibility(View.VISIBLE);
		// // btn_vocie = false;
		// // chatting_mode_btn
		// // .setImageResource(R.drawable.chatting_setmode_msg_btn);
		// //
		// // } else {
		// // mBtnRcd.setVisibility(View.VISIBLE);
		// // mBottom.setVisibility(View.GONE);
		// // chatting_mode_btn
		// // .setImageResource(R.drawable.chatting_setmode_voice_btn);
		// // btn_vocie = true;
		// // }
		// }
		// });
		mBtnRcd.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// 按下语音录制按钮时返回false执行父类OnTouch
				return false;
			}
		});

		right_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ChatMainActivity.this,
						ContactDetailActivity.class);
				intent.putExtra("contactName", chatContact);
				startActivity(intent);
			}
		});

		add_extras = (TextView) findViewById(R.id.add_extras);
		add_extras.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// if (pop.isShowing()) {
				// pop.dismiss();
				// } else {
				// int[] location = new int[2];
				// view.getLocationOnScreen(location);
				// // pop.showAsDropDown(hideView);
				// pop.showAtLocation(view, Gravity.BOTTOM, location[0],
				// location[1] - pop.getHeight());
				// // pop.show
				// }

				if (question_popup_layout.getVisibility() == View.VISIBLE) {
					question_popup_layout.setVisibility(View.GONE);
				} else {
					question_popup_layout.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	public void initData() {

		mDataArrays = new ArrayList<ChatMsgEntity>();
		cursor = db.readRecord(chatContact, TimeRender.getDate().split(" ")[0],
				"false");
		cursor.moveToPosition(-1);
		while (cursor.moveToNext()) {
			System.out.println("1: " + cursor.getString(1) + "2: "
					+ cursor.getString(2) + "3: " + cursor.getString(3));
			String textType = "";

			if (cursor.getString(4).equals("in"))
				mDataArrays.add(new ChatMsgEntity(cursor.getString(1), cursor
						.getString(2), cursor.getString(3), true, cursor
						.getString(9)));
			else
				mDataArrays.add(new ChatMsgEntity(
						((MyApplication) getApplication()).userName, cursor
								.getString(2), cursor.getString(3), false,
						cursor.getString(9)));
		}

		mAdapter = new ChatMsgViewAdapter(this, mDataArrays, chatContact, 0);
		mListView.setAdapter(mAdapter);

	}

	public void myOnclick(View v) {
		switch (v.getId()) {
		case R.id.popup_doodle_lay:

			AppUtils.doodleBoard(this);

			break;
		case R.id.popup_camera_lay:

			cameraName = AppUtils.sysCamera(this);

			break;
		case R.id.popup_imgpicker_lay:

			AppUtils.phonePictures(this, 1);

			break;
		case R.id.popup_handwrite_lay:

			AppUtils.hwBoard(this);

			break;
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chat_send:
			send();
			break;
		case R.id.btn_back:
			finish();
			break;

		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final String prefix = "file:///";
		File file = null;
		switch (requestCode) {
		case Constants.SYS_CAMEAR:// 相机
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = cameraName;
				file = new File(imgPath);
				path = imgPath;
			}
			break;
		case Constants.PHONE_PICTURES:// 图库
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> photos = data
						.getStringArrayListExtra("photos");
				file = new File(photos.get(0));
				path = photos.get(0);
			}
			break;
		case Constants.DOODLE_BOARD:// 涂鸦
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("doodlePath");
				file = new File(imgPath);
				path = imgPath;
			}
			break;
		case Constants.HANDWRITE_BOARD:// 手写
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("handwritePath");
				file = new File(imgPath);
				path = imgPath;
			}
			break;
		default:
			break;
		}
		if (file != null) {

			sendFile();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void sendFile() {
		ChatMsgEntity entity = new ChatMsgEntity();
		entity.setDate(TimeRender.getDate());
		entity.setName(((MyApplication) getApplication()).userName);
		entity.setMsgType(false);
		entity.setText(path);
		entity.setTextType("unnomal");

		mDataArrays.add(entity);// 增加一个信息
		mAdapter.notifyDataSetChanged();// 更新视图

		mEditTextContent.setText("");// 清空输入框

		mListView.setSelection(mListView.getCount() - 1);// 设置当前listView的选中行为最后一行

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				((MyApplication) getApplication()).sendFile(chatContact,
						new File(path));
			}
		}).start();

		ChatRecord chatRecord = new ChatRecord();
		chatRecord.setAccount(chatContact);
		chatRecord.setContent(path);
		chatRecord.setFlag("out");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("0");
		chatRecord.setIsGroupChat("false");
		chatRecord.setJid("-1");
		chatRecord.setContent_type("unnomal");
		db.insertRecord(chatRecord);
	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(TimeRender.getDate());
			entity.setName(((MyApplication) getApplication()).userName);
			entity.setMsgType(false);
			entity.setText(contString);
			entity.setTextType("nomal");

			try {

				newchat.sendMessage(contString);

			} catch (XMPPException e) {
				e.printStackTrace();
			}

			mDataArrays.add(entity);// 增加一个信息
			mAdapter.notifyDataSetChanged();// 更新视图

			mEditTextContent.setText("");// 清空输入框

			mListView.setSelection(mListView.getCount() - 1);// 设置当前listView的选中行为最后一行
			// if(((MyApplication)getApplication()).currentActivity.equals("chat")){
			ChatRecord chatRecord = new ChatRecord();
			chatRecord.setAccount(chatContact);
			chatRecord.setContent(contString);
			chatRecord.setFlag("out");
			chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
			chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
			chatRecord.setType("0");
			chatRecord.setIsGroupChat("false");
			chatRecord.setJid("-1");
			chatRecord.setContent_type("nomal");
			db.insertRecord(chatRecord);
			// }

		}
	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:

				System.out.println("收到消息，内容为：  " + msg.obj.toString());
				String[] args = (String[]) msg.obj;
				String s = args[0].toString().contains("@") ? args[0]
						.toString().split("@")[0] : args[0].toString();
				mDataArrays.add(new ChatMsgEntity(s, args[2], args[1], true,
						"nomal"));

				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount() - 1);

				break;
			case 2:
				String[] fileArgs = (String[]) msg.obj;
				String fileFrom = fileArgs[0].toString().contains("@") ? fileArgs[0]
						.toString().split("@")[0] : fileArgs[0].toString();
				mDataArrays.add(new ChatMsgEntity(fileFrom, fileArgs[2],
						fileArgs[1], true, "unnomal"));

				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount() - 1);

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