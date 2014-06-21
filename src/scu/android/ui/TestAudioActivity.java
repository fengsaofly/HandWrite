package scu.android.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import scu.android.application.MyApplication;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

public class TestAudioActivity extends Activity {
	ImageView publish_state_add_imgView,volume,question_add_showrecord_imgview;
	TextView second = null;
	TextView testInclude = null;
	// Button voice = null;
	// Button play = null;
	 MediaRecorder mr = null;
	String path = null;
	MediaPlayer voiceMp = null;
//	private SoundMeter mSensor;
	 PopupWindow pop;
	 View view,view2;
//	 ViewhideView;
	 AnimationDrawable animaition;
	 private long startVoiceT, endVoiceT;
	 LinearLayout popup_recode_lay = null,popup_camera_lay = null,popup_imgpicker_lay = null,popup_handwrite_lay = null;
	 LinearLayout rcChat_popup = null,yourVocie_lay = null;
	 RelativeLayout record_pressbtn_lay = null,play_lay = null;
	 Button record_pressbtn = null;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.question_add_layout);
		initial();
	}

	public void initial() {
//		hideView  = (View)findViewById(R.id.hideLine);
//		mSensor = new SoundMeter();
		// voice = (Button)findViewById(R.id.voice);
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				System.out.println("进入handler");
				// TODO Auto-generated method stub
//				LayoutInflater inflater2 = LayoutInflater.from(TestAudioActivity.this);
//				 view2 = inflater2.inflate(R.layout.voice_rcd_hint_window, null);
//				 volume = (ImageView)view2.findViewById(R.id.volume);
				switch(msg.what){
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
		publish_state_add_imgView = (ImageView) findViewById(R.id.publish_state_add_imgView);
		rcChat_popup = (LinearLayout)findViewById(R.id.rcChat_popup);
		record_pressbtn_lay = (RelativeLayout)findViewById(R.id.record_pressbtn_lay);
		record_pressbtn = (Button)findViewById(R.id.record_pressbtn);
		second = (TextView)findViewById(R.id.volume);
		play_lay = (RelativeLayout)findViewById(R.id.play_lay);
		yourVocie_lay = (LinearLayout)findViewById(R.id.yourVocie_lay);
		question_add_showrecord_imgview = (ImageView)findViewById(R.id.question_add_showrecord_imgview);
//		question_add_showrecord_imgview.setImageResource(R.anim.playvoice); 
		LayoutInflater inflater = LayoutInflater.from(this);
//		LayoutInflater inflater2 = LayoutInflater.from(this);
		// 引入窗口配置文件

		 view = inflater.inflate(R.layout.question_popup_layout, null);
		 popup_recode_lay = (LinearLayout)view.findViewById(R.id.popup_record_lay);
		 popup_camera_lay = (LinearLayout)view.findViewById(R.id.popup_camera_lay);
		 popup_imgpicker_lay = (LinearLayout)view.findViewById(R.id.popup_imgpicker_lay);
		 popup_handwrite_lay = (LinearLayout)view.findViewById(R.id.popup_handwrite_lay);
//		 view2 = inflater2.inflate(R.layout.voice_rcd_hint_window, null);
		 volume = (ImageView)findViewById(R.id.volume2);
		 testInclude = (TextView)findViewById(R.id.testInclude);
		// 创建PopupWindow对象

		pop = new PopupWindow(view,
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, false);

		

//		// 需要设置一下此参数，点击外边可消失
//
		pop.setBackgroundDrawable(new BitmapDrawable());

		// 设置点击窗口外边窗口消失

		pop.setOutsideTouchable(true);

		// 设置此参数获得焦点，否则无法点击

		pop.setFocusable(true);
		
		
		// play = (Button)findViewById(R.id.play);
		record_pressbtn.setOnTouchListener(new OnTouchListener() {
		
		 @Override
		 public boolean onTouch(View v, MotionEvent event) {
		
		 // TODO Auto-generated method stub
		 switch (event.getAction()) {
		
		 case MotionEvent.ACTION_DOWN:
		 {
		 rcChat_popup.setVisibility(View.VISIBLE);
		 startVoiceT = System.currentTimeMillis();
		 //按住事件发生后执行代码的区域
		  mr = new MediaRecorder();
		 File file2 = new
		 File(MyApplication.getSDCardPath()+"/ConquerQustion"+"/"+((MyApplication)getApplication()).userName+"/"+"Audio");
		
		 if(!file2.exists())
		 file2.mkdirs();
		 SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		 String time = df.format(new Date());
		
		 File file = new File(file2.getAbsolutePath()+ "/"+time+ ".amr");
		
		 //
		 Toast.makeText(getApplicationContext(),
		 "正在录音，录音文件在"+file.getAbsolutePath(), Toast.LENGTH_LONG)
		 .show();
		
		 path = file.getAbsolutePath();
		 System.out.println("路径： "+ path);
//		 start(path);
		
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
		 }
		 case MotionEvent.ACTION_MOVE:
		 {
		 //移动事件发生后执行代码的区域
		 break;
		 }
		 case MotionEvent.ACTION_UP:
		 {
		endVoiceT = System.currentTimeMillis();
		  if (mr != null) {
		  mr.stop();
		  mr.release();
		  mr = null;
		  }
	     stop();
		 int seconds = (int)(endVoiceT - startVoiceT)/1000;
		 if(seconds<1){
			 seconds = 1;
		 }
		 yourVocie_lay.setVisibility(View.VISIBLE);
		 second.setText(""+seconds);
		     
		
		 rcChat_popup.setVisibility(View.GONE);
		 Toast.makeText(getApplicationContext(), "录音完毕",
		 Toast.LENGTH_LONG).show();
//		 play.setVisibility(View.VISIBLE);
		
		  
		 //松开事件发生后执行代码的区域
		 break;
		 }
		
		 default:
		
		 break;
		 }
		 return false;
		 }
		 });
		//
		play_lay.setOnClickListener(new OnClickListener() {
		
		 @Override
		 public void onClick(View arg0) {
		 // TODO Auto-generated method stub
		 try {
		 // voiceMp = new MediaPlayer();
		  if (voiceMp != null) {
		  voiceMp.reset();
		  voiceMp.release();
		  voiceMp = null;
		  }
		 voiceMp = new MediaPlayer();
		 voiceMp.setDataSource(path);
		 
		 voiceMp.prepare();
		 voiceMp.setLooping(false);
		 voiceMp.start();
		 voiceMp.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				voiceMp.release();
				  voiceMp = null;
				  animaition.stop();
				  question_add_showrecord_imgview.setImageResource(R.drawable.question_add_record_icon_default); 
			}
		});
		 question_add_showrecord_imgview.setImageResource(R.anim.playvoice); 
		 animaition = (AnimationDrawable)question_add_showrecord_imgview.getDrawable();   
//		 最后，就可以启动动画了，代码如下：   

		 //是否仅仅启动一次？ 

		 animaition.setOneShot(false);   

		 if(animaition.isRunning())//是否正在运行？ 

		 {   
		 animaition.stop();//停止 

		 }   
		 animaition.start();//启动
		 } catch (IllegalStateException e1) {
		 // TODO Auto-generated catch block
		 e1.printStackTrace();
		 } catch (IOException e1) {
		 // TODO Auto-generated catch block
		 e1.printStackTrace();
		 }
		 }
		 });
	}
	
	public void myOnclick(View v){
		switch(v.getId()){
		case R.id.publish_state_add_imgView:
			 if(pop.isShowing()) { 
			 pop.dismiss();
			 }
			 else{
			 int[] location = new int[2];  
		     view.getLocationOnScreen(location); 
//			pop.showAsDropDown(hideView);  
			pop.showAtLocation(view, Gravity.BOTTOM, location[0], location[1]-pop.getHeight());
//			pop.show
			 }
			break;
		case R.id.popup_record_lay:
//			rcChat_popup.setVisibility(View.VISIBLE);
			if(pop.isShowing()) { 
				 pop.dismiss();
				 }
			record_pressbtn_lay.setVisibility(View.VISIBLE);
			Toast.makeText(TestAudioActivity.this, "点击了popup_recode_lay", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.popup_camera_lay:
			Toast.makeText(TestAudioActivity.this, "点击了popup_camera_lay", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.popup_imgpicker_lay:
			Toast.makeText(TestAudioActivity.this, "点击了popup_imgpicker_lay", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.popup_handwrite_lay:
			Toast.makeText(TestAudioActivity.this, "点击了popup_recode_lay", Toast.LENGTH_SHORT).show();
			break;
			
			default:break;
		}
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
			System.out.println("amp: "+amp);
//			updateDisplay(amp);
			Message msg = mHandler.obtainMessage();
			msg.what = (int)amp;
			mHandler.sendMessage(msg);
			
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start() {
//		mr.start();
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
//		mr.stop();
		volume.setImageResource(R.drawable.amp1);
	}

//	private void updateDisplay(double signalEMA) {
//		
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
	
	

}
