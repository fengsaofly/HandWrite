package scu.android.ui;

import java.io.IOException;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import scu.android.application.MyApplication;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	
	String mySexSelect = "男";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings_usercenter);
		initial();
	}
	
	public void initial(){
		setting_modify_avatar = (RelativeLayout)findViewById(R.id.lay_avatar);
		setting_modify_nickname = (RelativeLayout)findViewById(R.id.lay_nickname);
		setting_modify_zone = (RelativeLayout)findViewById(R.id.lay_region);
		setting_modify_gender = (RelativeLayout)findViewById(R.id.lay_sex);
		setting_modify_grade = (RelativeLayout)findViewById(R.id.lay_career);
		setting_modify_sign = (RelativeLayout)findViewById(R.id.lay_sign);
		
		setting_value_avatar = (ImageView)findViewById(R.id.tag_avatar_val);
		setting_value_nickname = (TextView)findViewById(R.id.tag_nickname_val);
		setting_value_zone = (TextView)findViewById(R.id.tag_region_val);
		setting_value_gender = (TextView)findViewById(R.id.tag_sex_val);
		setting_value_grade = (TextView)findViewById(R.id.tag_career_val);
		setting_value_sign = (TextView)findViewById(R.id.tag_sign_val);
		VCard vcard = ((MyApplication)getApplication()).getUserVcard(XmppTool.getConnection(),((MyApplication)getApplication()).userName);
		if(vcard!=null){
			System.out.println("vcard.getNickName:"+vcard.getNickName());
			if(vcard.getNickName()!=null&&(!vcard.getNickName().equals(""))&&(!vcard.getNickName().equals("null"))){
				setting_value_nickname.setText(vcard.getNickName());
			}
			
			setting_value_zone.setText(vcard.getAddressFieldHome("zone"));
			setting_value_gender.setText(vcard.getFirstName());
			mySexSelect = vcard.getFirstName();
			setting_value_grade.setText(vcard.getMiddleName());
			setting_value_sign.setText(vcard.getLastName());
		}
		Bitmap bm = BitmapFactory.decodeStream(((MyApplication)getApplication()).getUserImage(XmppTool.getConnection(), ((MyApplication)getApplication()).userName));
		if(bm!=null)
		setting_value_avatar.setImageBitmap(bm);

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
		case R.id.lay_avatar:
			intent.setClass(AccountSettingActivity.this,ModifyAvatarActivity.class);
			startActivity(intent);
			break;
		case R.id.lay_nickname:
			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
			intent.putExtra("type", "nickName");
			startActivity(intent);
			break;
		case R.id.lay_region:
			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
			intent.putExtra("type", "zone");
			startActivity(intent);
			break;
		case R.id.lay_sex:
//			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
//			intent.putExtra("type", "gender");
			   AlertDialog.Builder  malertdialog =new AlertDialog.Builder(this);
			      malertdialog.setTitle("请选择性别");
			      if(mySexSelect.equals("男")){
			    	  malertdialog.setSingleChoiceItems(R.array.alterdialog_items,0, malertdialoglistener);
			      }
			      else if(mySexSelect.equals("女")){
			    	  malertdialog.setSingleChoiceItems(R.array.alterdialog_items,1, malertdialoglistener);
			      }
//			      malertdialog.setMultiChoiceItems(R.array.alterdialog_items,new boolean[] {false,false,false,false},  multialertlisten);
			      malertdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
						setting_value_gender.setText(mySexSelect);
						try {
							((MyApplication)getApplication()).setUserVCard(XmppTool.getConnection(),3,mySexSelect);
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			      });
			      malertdialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				});
			      malertdialog.show();
			
			break;
		case R.id.lay_career:
			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
			intent.putExtra("type", "grade");
			startActivity(intent);
			break;
		case R.id.lay_sign:
			intent.setClass(AccountSettingActivity.this, ModifyNomalActivity.class);
			intent.putExtra("type", "sign");
			startActivity(intent);
			break;
		}
		
		
		

	}
	
	
	 private DialogInterface.OnClickListener malertdialoglistener=
		     new DialogInterface.OnClickListener()
		    {
		     public void onClick(DialogInterface dialog, int whichButton) {

		      String[] malertmeg=getResources().getStringArray(R.array.alterdialog_items);
		      
		      System.out.println("你选择了："+malertmeg[whichButton]);  
		      mySexSelect = malertmeg[whichButton];
		      /* User clicked OK so do some stuff */
		        }
		    };
	
//		    private DialogInterface.OnMultiChoiceClickListener multialertlisten=
//		    		  
//		    	     new DialogInterface.OnMultiChoiceClickListener()
//		    	    {
//		    	     public void onClick(DialogInterface dialog, int whichButton,boolean isChecked) {
//		    	            String mchoice="";
//		    	      String[] malertmeg=getResources().getStringArray(R.array.alterdialog_items);
//		    	      if (isChecked)
//		    	      {
//		    	    	  if ( mchoice.indexOf(malertmeg[whichButton])<=0)
//		    	    		  mchoice+=malertmeg[whichButton]+',';
//		    	       }
//		    	      System.out.println("mchoice:  "+mchoice);
////		    	      setTitle("你选择了："+mchoice);  
//		    	      
//		    	      /* User clicked OK so do some stuff */
//		    	        }
//		    	    };
	
	
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
