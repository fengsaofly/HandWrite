package scu.android.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

import scu.android.application.MyApplication;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

public class GroupDetailActivity extends Activity{
	TextView group_name_val = null,tag_group_maxmember_val = null,group_type_val,tag_group_notice_val,exit = null;
	ArrayList<Map<String,Object>> roomInformationList = null;
	int existPosition = -1;
	String roomType = "";
	String roomCount = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.group_info);
		initial();
	}
	
	
	public void initial(){
		getActionBar().setTitle("群组信息");
		group_name_val = (TextView)findViewById(R.id.group_name_val);
		tag_group_maxmember_val = (TextView)findViewById(R.id.tag_group_maxmember_val);
		group_type_val = (TextView)findViewById(R.id.group_type_val);
		tag_group_notice_val = (TextView)findViewById(R.id.tag_group_notice_val);
		exit = (TextView)findViewById(R.id.submit);
		
		roomInformationList = new ArrayList<Map<String,Object>>();
		((MyApplication)getApplication()).loadArray(roomInformationList);
		
		Intent intent = getIntent();
		List<RoomInfo> rooms = new ArrayList<RoomInfo>();
		((MyApplication)getApplication()).getRoomInfo(rooms);
		final String roomName = intent.getStringExtra("roomName");
		checkExist(roomName);
		String sign = "";
		String jid = roomName+"@conference"+"."+((MyApplication)getApplication()).hostName;
		String ower = "";
		int currentCount = 0;
		for(RoomInfo room:rooms){
			if(room.getRoom().split("@")[0].equals(roomName)){
				sign = room.getDescription();
				currentCount = room.getOccupantsCount();
				roomType = room.getSubject();
				roomCount = room.getOccupantsCount()+"";
//				jid = room.getRoom();
				break;
			}
		}
		
//		MultiUserChat muc = new MultiUserChat(XmppTool.getConnection(),jid );
//		String nickName = MyApplication.nickName;
////				((MyApplication)getApplication()).vCard.getNickName().toString();
//		
//		try {
//			muc.join(nickName);
//			java.util.Collection<Affiliate>  admins = muc.getOwners();
//			
//			if(admins!=null&&admins.size()!=0){
//				
//			
//			for(Affiliate item: admins){
//				ower = item.getJid().split("@")[0];
//			}
//			}
//			
//		} catch (XMPPException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		muc.leave();
		
		
		group_name_val.setText(roomName);
		tag_group_maxmember_val.setText(roomCount);
		group_type_val.setText(roomType);
		tag_group_notice_val.setText(sign);
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				roomInformationList.remove(existPosition);
				SharedPreferences.Editor editor = ((MyApplication)getApplication()).sp.edit();
				editor.clear();
				((MyApplication)getApplication()).saveArray(roomInformationList);
				Toast.makeText(GroupDetailActivity.this, "已退出该群", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		
	}
	
	public Boolean checkExist(String roomName){
		for(int i=0;i<roomInformationList.size();i++){
			Map<String,Object> item = roomInformationList.get(i);
			if(item.get("roomName").equals(roomName)){

				existPosition = i;
				return true;
			}
			else continue;
		}
		return false;
	}

}
