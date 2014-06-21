package scu.android.ui;

import scu.android.application.MyApplication;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;



public class AccountSettingActivity extends Activity{
	String POST_URL = "http://weliao.sinaapp.com/account/modify/";
	
	String POST_AK = "7244d82a2ef54bfa015a0d7d6f85f372";
	
	ImageView account_settings_icon_btn = null;
	ImageView photo;
	TextView account_settings_account_name = null,account_settings_modify_btn = null,uid,top_left_back_btn;
	EditText account_settings_city_value = null,account_settings_sign= null;
	Button regist_btn,login_btn;
	boolean selectFlag=false;
	String selectIconName = "";
	String DEBUG_TAG = "修改个人信息";
//	SharedPreferences sp = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account_settings);
		initial();
	}
	
	public void initial(){
//		sp = this.getSharedPreferences("bnj", MODE_PRIVATE);
		photo = (ImageView)findViewById(R.id.photo);
		top_left_back_btn = (TextView)findViewById(R.id.top_left_back_btn);
		regist_btn = (Button)findViewById(R.id.regist_btn);
		login_btn = (Button)findViewById(R.id.login_btn);
		uid = (TextView)findViewById(R.id.uid);
		uid.setText(((MyApplication)getApplication()).userName);
	

	
		try{
			int indentify = this.getResources().getIdentifier(this.getPackageName()+":drawable/"+((MyApplication)this.getApplicationContext()).iconMap.get(((MyApplication)getApplication()).userName), null, null);
			String city = ((MyApplication)this.getApplicationContext()).cityMap.get(((MyApplication)getApplication()).userName).toString();
			String sign = ((MyApplication)this.getApplicationContext()).signMap.get(((MyApplication)getApplication()).userName).toString();
			account_settings_city_value.setText(city);
			account_settings_sign.setText(sign);
			if(indentify>0){ 
   				photo.setImageDrawable(this.getResources().getDrawable(indentify));
   				
   			}
   			else {
   				photo.setImageDrawable(this.getResources().getDrawable(R.drawable.logo));
   			}
			 }catch(NullPointerException e){
				
			 }

	}
	
	public void myOnclick(View v){
		
		Intent intent = new Intent();
		System.out.println("进入了onclick函数--------");
		
		switch(v.getId()){
		case R.id.top_left_back_btn:
			System.out.println("按了返回键");
			finish();
			break;
		case R.id.photo:
			
			intent.putExtra("type", 1);
//			intent.setClass(this, SelectIconActivity.class);
			startActivityForResult(intent, 420);
			break;
		
		
		case R.id.login_btn:
			intent.setClass(this, LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.regist_btn:
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	
	 @Override  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
	    {  
	        //可以根据多个请求代码来作相应的操作  
	        if(19==resultCode)  
	        {  
	        	
	        	selectFlag = true;
	        	selectIconName = data.getExtras().getString("iconName");
	        	Resources resources = getResources();
 			  int indentify = resources.getIdentifier(getPackageName()+":drawable/"+selectIconName, null, null);
 			account_settings_icon_btn.setImageResource(indentify);
	        	
	        }  
	        super.onActivityResult(requestCode, resultCode, data);  
	    } 
	
	 Handler handler = new Handler(){
			public void handleMessage(Message msg){
				
				switch(msg.what){
				case 0:
					
					Toast.makeText(AccountSettingActivity.this, "修改成功", 3).show();
//					SharedPreferences.Editor editor = sp.edit();
//					editor.putString("city", account_settings_city_value.getText().toString());
//					editor.putString("sign", account_settings_sign.getText().toString());
//					editor.commit();
					finish();
					break;
				case 1:
					Toast.makeText(AccountSettingActivity.this, "修改失败", 3).show();
					
					break;
				
					default:
						break;
					
				}
				super.handleMessage(msg);
			}
		};

}
