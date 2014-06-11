package scu.android.ui;

import org.jivesoftware.smack.XMPPException;

import scu.android.application.MyApplication;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.demo.note.R;

public class ModifyNomalActivity extends Activity{
	TextView modifyNomal_title = null,modifyNomal_desc = null,modify_back = null,modify_save = null;
	EditText modifyNomal_value = null;
	String s1 = "",s2 = "";
	int tag = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.modify_nomal);
		initial();
	}
	
	public void initial(){
		modifyNomal_title = (TextView)findViewById(R.id.modifyNomal_title);
		modifyNomal_desc = (TextView)findViewById(R.id.modifyNomal_desc);
		modify_back = (TextView)findViewById(R.id.modify_back);
		modify_save = (TextView)findViewById(R.id.modify_save);
		modifyNomal_value = (EditText)findViewById(R.id.modifyNomal_value);
		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		if(type.equals("grade")){
			s1 = "年级";
			s2 = "请填写你所在的年级";
			tag = 1;
		}
		else if(type.equals("nickName")){
			s1 = "昵称";
			s2 = "请填写你的昵称";
			tag =2;
		}
		
		else if(type.equals("gender")){
			s1 = "性别";
			s2 = "请填写你的性别（男/女）";
			tag = 3;
		}
		
		else if(type.equals("zone")){
			s1 = "地区";
			s2 = "请填写你所在的城市";
			tag = 4;
		}
		
		else {
			s1 = "个性签名";
			s2 = "请填写你的个性签名";
			tag = 5;
		}
		
		modifyNomal_title.setText(s1);
		modifyNomal_desc.setText(s2);
		
		
	}
	
	public void myOnclick(View v){
		switch(v.getId()){
		case R.id.modify_back:
			finish();
			break;
		case R.id.modify_save:
			((MyApplication)getApplication()).setUserVCard(XmppTool.getConnection(),tag,modifyNomal_value.getText().toString());
			finish();
			break;
		}
	}

}
