package scu.android.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scu.android.application.MyApplication;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

public class SearchGroupResultActivity extends Activity{
	TextView friend_name = null,group_name_val = null,joinInTheRoom = null,group_owner_val = null,tag_sign_val = null,group_members_val = null;
	List<Map<String,Object>> roomInformationList;
	int currentNum = 0,TotleNum = 0;
	boolean existFlag = false;
	int existPosition = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.group_search_result);
		initial();
	}
	
	public void initial(){
		Intent intent = getIntent();
		final String roomName = intent.getStringExtra("roomName");
		friend_name = (TextView)findViewById(R.id.friend_name);
		group_name_val = (TextView)findViewById(R.id.group_name_val);
		joinInTheRoom = (TextView)findViewById(R.id.joinInTheRoom);
		group_owner_val= (TextView)findViewById(R.id.group_owner_val);
		tag_sign_val= (TextView)findViewById(R.id.tag_sign_val);
		group_members_val= (TextView)findViewById(R.id.group_members_val);
		currentNum =Integer.parseInt( group_members_val.getText().toString().replace("(", "").replace(")", "").split("/")[0]);
		TotleNum = Integer.parseInt( group_members_val.getText().toString().replace("(", "").replace(")", "").split("/")[1]);
		System.out.println("currentNum: "+currentNum+"TotleNum: "+TotleNum);
		roomInformationList = new ArrayList<Map<String,Object>>();
		((MyApplication)getApplication()).loadArray(roomInformationList);
		if(checkExist(roomName)){
			joinInTheRoom.setText("退出该群");
		}
		group_name_val.setText(roomName);
		friend_name.setText(((MyApplication)getApplication()).userName);
		joinInTheRoom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(existFlag){  //表示点击了退出该群
					roomInformationList.remove(existPosition);
					SharedPreferences.Editor editor = ((MyApplication)getApplication()).sp.edit();
					editor.clear();
					((MyApplication)getApplication()).saveArray(roomInformationList);
					Toast.makeText(SearchGroupResultActivity.this, "已退出该群", Toast.LENGTH_SHORT).show();
					finish();
					
				}
				else{     //表示点击了加入该群
				Intent intent = new Intent(SearchGroupResultActivity.this, ActivityMultiRoom.class);
				intent.putExtra("jid", roomName + "@conference"+"."+((MyApplication)getApplication()).hostName);
				intent.putExtra("action", "join");
				startActivity(intent);
				Toast.makeText(SearchGroupResultActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("roomName", roomName);
		    	map.put("roomNumber", 635313831);
		    	map.put("roomOwner",group_owner_val.getText().toString());
		    	map.put("roomNoti", tag_sign_val.getText().toString());
		    	map.put("roomCurrentPeople", currentNum);
		    	map.put("roomTotlePeople", currentNum);
		    	roomInformationList.add(map);
				((MyApplication)getApplication()).saveArray(roomInformationList);
				finish();
				}
			}
		});
	}
	
	public Boolean checkExist(String roomName){
		for(int i=0;i<roomInformationList.size();i++){
			Map<String,Object> item = roomInformationList.get(i);
			if(item.get("roomName").equals(roomName)){
				existFlag = true;
				existPosition = i;
				return true;
			}
			else continue;
		}
		return false;
	}

}
