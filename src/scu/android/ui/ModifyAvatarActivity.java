package scu.android.ui;

import java.io.File;
import java.io.IOException;

import org.jivesoftware.smack.XMPPException;

import scu.android.application.MyApplication;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.demo.note.R;



public class ModifyAvatarActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.modify_nomal);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				File f = new File(((MyApplication)getApplication()).getSDCardPath()+"/test_download1.jpg");  //测试添加头像
				try {
					((MyApplication) getApplication()).changeImage(XmppTool.getConnection(), f);
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(3000);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			finish();
			super.handleMessage(msg);
		}
		
	};

}
