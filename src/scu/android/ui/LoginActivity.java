package scu.android.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.VCard;

import scu.android.application.MyApplication;
import scu.android.note.ActionBarActivity;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

public class LoginActivity extends Activity{
	ProgressDialog pd = null;
	TextView back =null,register = null,loginButton = null;
	AutoCompleteTextView userName = null;
	EditText passWord = null;
	CheckBox login_show_password = null;
	SharedPreferences sp = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		Date nowTime = new Date(System.currentTimeMillis());
		  SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		  String retStrFormatNowDate = sdFormatter.format(nowTime);
		  System.out.println("retStrFormatNowDate"+retStrFormatNowDate);
		  final EditText et  = new EditText(this);
//		  new AlertDialog.Builder(this).setTitle("请输入服务器ip地址").setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				// TODO Auto-generated method stub
//				if(et.getText().toString().equals("")){
//					Toast.makeText(LoginActivity.this, "请输入服务器ip地址", 3).show();
//				}
//				else{
//					((MyApplication)getApplication()).hostIp = et.getText().toString();
////					((MyApplication)getApplication()).hostName = et.getText().toString();
//				}
//				
//				System.out.println("ip  "+((MyApplication)getApplication()).hostIp +"------");
//			}
//		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				
//			}
//		}).create().show();
		initial();
		
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			if(pd.isShowing())
				pd.dismiss();
			switch(msg.what){
			case 1:
				
				
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, ActionBarActivity.class);
				intent.putExtra("USERID", "");
				startActivity(intent);
				Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
				((MyApplication)getApplication()).loginFlag = true;
				((MyApplication) getApplication()).roster = XmppTool
						.getConnection().getRoster();

				((MyApplication) getApplication()).entries = ((MyApplication) getApplication())
						.getAllEntries();
				
				VCard vCard = new VCard();
				try {
					vCard.load(XmppTool.getConnection());
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if ("".equals(vCard.getNickName())
						|| null == vCard.getNickName()) {
					System.out.println("昵称是空的");
					vCard.setNickName("快乐的汤姆猫");
				}
				((MyApplication) getApplication()).vCard = vCard;
				finish();
				break;
			case 2:
				
				Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public void myOnclick(View v){
		switch(v.getId()){
		case R.id.buttonBack:
			new AlertDialog.Builder(this).setTitle("确定要退出应用吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					System.exit(0);
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).create().show();
			break;
		case R.id.register_btn:  //注册
			
			startActivity(new Intent(this,RegisterActivity.class));
			break;
		case R.id.loginButton:
			
			final String USERID =userName.getText().toString();
			
			final String PWD = passWord.getText().toString();
			
			pd.show();
			new Thread(new Runnable() {				
				public void run() {
					
					
					try {
						

						XmppTool.getConnection().login(USERID, PWD);
//					             新建presence对象״̬
						Presence presence = new Presence(Presence.Type.available);
						XmppTool.getConnection().sendPacket(presence);
						
						
						handler.sendEmptyMessage(1);
						SharedPreferences.Editor editor = sp.edit();
						editor.putString("account", USERID);
						editor.putString("password", PWD);
						editor.commit();
						((MyApplication)getApplication()).userName = USERID;
						((MyApplication)getApplication()).passWord = PWD;
						finish();
					}
					catch (XMPPException e) 
					{
						XmppTool.closeConnection();
						
						handler.sendEmptyMessage(2);
					}					
				}
			}).start();
			
			break;
		}
	}
	
	public void initial(){
		sp = this.getSharedPreferences("bnj", MODE_PRIVATE);
		String account = sp.getString("account", "");
		String password = sp.getString("password", "");
		back = (TextView)findViewById(R.id.buttonBack);
		register = (TextView)findViewById(R.id.register_btn);
		loginButton = (TextView)findViewById(R.id.loginButton);
		userName = (AutoCompleteTextView)findViewById(R.id.loginUserNameValue);
		userName.setText(account);
		passWord = (EditText)findViewById(R.id.userPassValue);
		passWord.setText(password);
		login_show_password = (CheckBox)findViewById(R.id.login_show_password);
		pd = new ProgressDialog(this);
		pd.setTitle("正在登陆");
		pd.setMessage("请耐心等候");
		pd.setCancelable(false);
		final int type = passWord.getInputType();
		login_show_password.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1){
					
					passWord.setInputType(InputType.TYPE_CLASS_TEXT);
				}
				else passWord.setInputType(type);
			}
		});

	}

}
