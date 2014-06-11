package scu.android.ui;

import org.jivesoftware.smackx.packet.VCard;

import scu.android.application.MyApplication;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.note.R;



public class AccountSettingActivity extends Activity{
	ImageView setting_value_avatar;
	RelativeLayout setting_modify_avatar,setting_modify_nickname,setting_modify_zone,setting_modify_gender,setting_modify_grade,setting_modify_sign;
	TextView setting_value_nickname,setting_value_zone,setting_value_gender,setting_value_grade,setting_value_sign;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings_usercenter);
		initial();
	}
	
	public void initial(){
		setting_modify_avatar = (RelativeLayout)findViewById(R.id.setting_modify_avatar);
		setting_modify_nickname = (RelativeLayout)findViewById(R.id.setting_modify_nickname);
		setting_modify_zone = (RelativeLayout)findViewById(R.id.setting_modify_zone);
		setting_modify_gender = (RelativeLayout)findViewById(R.id.setting_modify_gender);
		setting_modify_grade = (RelativeLayout)findViewById(R.id.setting_modify_grade);
		setting_modify_sign = (RelativeLayout)findViewById(R.id.setting_modify_sign);
		
		setting_value_avatar = (ImageView)findViewById(R.id.setting_value_avatar);
		setting_value_nickname = (TextView)findViewById(R.id.setting_value_nickname);
		setting_value_zone = (TextView)findViewById(R.id.setting_value_zone);
		setting_value_gender = (TextView)findViewById(R.id.setting_value_gender);
		setting_value_grade = (TextView)findViewById(R.id.setting_value_grade);
		setting_value_sign = (TextView)findViewById(R.id.setting_value_sign);
		VCard vcard = ((MyApplication)getApplication()).getUserVcard(XmppTool.getConnection(),((MyApplication)getApplication()).userName);
		if(vcard!=null){
			setting_value_avatar.setImageBitmap(((MyApplication)getApplication()).getUserImage(XmppTool.getConnection(), ((MyApplication)getApplication()).userName));
			setting_value_nickname.setText(vcard.getNickName());
			setting_value_zone.setText(vcard.getAddressFieldHome("zone"));
			setting_value_gender.setText(vcard.getFirstName());
			setting_value_grade.setText(vcard.getMiddleName());
			setting_value_sign.setText(vcard.getLastName());
		}

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initial();
	}
	
	public void myOnclick(View v){
		
		Intent intent = new Intent();
		switch(v.getId()){
		case R.id.setting_modify_avatar:
			intent.setClass(AccountSettingActivity.this,ModifyAvatarActivity.class);
			break;
		case R.id.setting_modify_nickname:
			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
			intent.putExtra("type", "nickName");
			break;
		case R.id.setting_modify_zone:
			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
			intent.putExtra("type", "zone");
			break;
		case R.id.setting_modify_gender:
			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
			intent.putExtra("type", "gender");
			break;
		case R.id.setting_modify_grade:
			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
			intent.putExtra("type", "grade");
			break;
		case R.id.setting_modify_sign:
			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
			intent.putExtra("type", "sign");
			break;
		}
		startActivity(intent);
		
		
//		switch(v.getId()){
//		case R.id.top_left_back_btn:
//			System.out.println("按了返回键");
//			finish();
//			break;
//		case R.id.photo:
//			
//			intent.putExtra("type", 1);
////			intent.setClass(this, SelectIconActivity.class);
//			startActivityForResult(intent, 420);
//			break;
//		
//		
//		case R.id.login_btn:
//			intent.setClass(this, LoginActivity.class);
//			startActivity(intent);
//			break;
//		case R.id.regist_btn:
//			intent.setClass(this, RegisterActivity.class);
//			startActivity(intent);
//			break;
//		}
	}
	
	
	
	
//	 Handler handler = new Handler(){
//			public void handleMessage(Message msg){
//				
//				switch(msg.what){
//				case 0:
//					
//					Toast.makeText(AccountSettingActivity.this, "修改成功", 3).show();
////					SharedPreferences.Editor editor = sp.edit();
////					editor.putString("city", account_settings_city_value.getText().toString());
////					editor.putString("sign", account_settings_sign.getText().toString());
////					editor.commit();
//					finish();
//					break;
//				case 1:
//					Toast.makeText(AccountSettingActivity.this, "修改失败", 3).show();
//					
//					break;
//				
//					default:
//						break;
//					
//				}
//				super.handleMessage(msg);
//			}
//		};

}
