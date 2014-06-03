package scu.android.ui;

import org.jivesoftware.smackx.packet.VCard;

import scu.android.application.MyApplication;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
//import com.android.ui.AddUserActivity;

public class SearchResultActivity extends Activity{
	TextView friend_name = null,tag_zone = null,tag_sign = null,search_result_add = null,search_result_complain = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_search_result);
		initial();
	}
	
	public void initial(){
		friend_name = (TextView)findViewById(R.id.friend_name);
		Intent intent = getIntent();
		String name = intent.getStringExtra("name");
		friend_name.setText(name);
		tag_zone = (TextView)findViewById(R.id.tag_zone);
		VCard vard = ((MyApplication)getApplication()).getUserVcard(XmppTool.getConnection(), name);
		tag_zone.setText(vard.getAddressFieldHome("四川成都"));
		search_result_add= (TextView)findViewById(R.id.search_result_add);
		search_result_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(((MyApplication)getApplication()).addUser(friend_name.getText().toString(), friend_name.getText().toString(),"my friend")){
					Toast.makeText(SearchResultActivity.this, "添加好友成功", 3).show();
					finish();
					((MyApplication)getApplication()).getAllEntries();
				}
				else{
					Toast.makeText(SearchResultActivity.this, "添加好友失败", 3).show();
				}
			}
		});
	}

}
