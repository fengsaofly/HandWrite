package scu.android.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import scu.android.application.MyApplication;
import scu.android.db.DbManager2;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.demo.note.R;

public class NewFriendsActivity extends Activity {
	ListView newFriendListView = null;
	SimpleAdapter adapter = null;
	List<Map<String, Object>> data = null;
	DbManager2 db = null;
	Cursor cursor = null;
	
	Chat newchat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_friends);
		initial();

	}

	public void initial(){
		

		
		System.out.println("进入newfriendActivity...");
		data = new ArrayList<Map<String,Object>>();
		db = new DbManager2(this);
		cursor = db.readFriendReq();
		cursor.moveToPosition(-1);
		while(cursor.moveToNext()){
			
			
			System.out.println("cursor.getString(1)//account "+cursor.getString(1));
			System.out.println("cursor.getString(3)//content "+cursor.getString(3));
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("new_friend_name", cursor.getString(1));
			map.put("new_friend_id", cursor.getInt(0));
			if(cursor.getString(3).equals("subscribe")){
				map.put("new_friend_state", "请求添加你为好友");
			}
			else if(cursor.getString(3).equals("subscribed")){
				map.put("new_friend_state", "对方同意了你的添加请求");
			}
			else{
				map.put("new_friend_state", "对方拒绝添加或删除你为好友");
			}
			
			
			data.add(map);
			
		}
		newFriendListView = (ListView)findViewById(R.id.newFriendListView);
		
		
		adapter = new SimpleAdapter(NewFriendsActivity.this, 
				data, 
				R.layout.new_friends_item,
				new String[]{"new_friend_name","new_friend_state"},
				new int[]{R.id.new_friend_name,R.id.new_friend_state});
		newFriendListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				final int p = position;
				if(data.get(position).get("new_friend_state").equals("请求添加你为好友")){
					new AlertDialog.Builder(NewFriendsActivity.this).setMessage("确定添加"+data.get(position).get("new_friend_name")+"为你的好友吗")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
					
//							if(((MyApplication)getApplication()).addUser(data.get(p).get("new_friend_name").toString(), data.get(p).get("new_friend_name").toString(),"my friend")){
//								Toast.makeText(NewFriendsActivity.this, "添加好友成功", 3).show();
////								finish();
//								((MyApplication)getApplication()).getAllEntries();
//								
//								Presence presence = new Presence(
//					                    Presence.Type.subscribed);//同意是subscribed   拒绝是unsubscribe
//												
//										System.out.println("presence.setTo: "+data.get(p).get("new_friend_name").toString());
//										System.out.println("presence.setFrom: "+MyApplication.userName+"@"+MyApplication.hostName);
//					                    presence.setTo(data.get(p).get("new_friend_name").toString());//接收方jid
//					                    presence.setFrom(MyApplication.userName+"@"+MyApplication.hostName);//发送方jid
//					                    XmppTool.getConnection().sendPacket(presence);//connection是你自己的XMPPConnection链接
//					                    Map<String,Object> map2 = new HashMap<String, Object>();
//					                    map2.put("friend_avatar", getResources().getDrawable(R.drawable.default_avatar));
//					    				map2.put("friend_name", data.get(p).get("new_friend_name").toString());
//					    				map2.put("friend_nickName", data.get(p).get("new_friend_name").toString());
//					    				map2.put("friend_carrer", "");
//					    				map2.put("friend_gender", "");
//					    				map2.put("friend_zone", "");
//					    				map2.put("friend_sign", "");
//					    				((MyApplication)getApplication()).allContactsVcard.add(map2);
//							}
//							else{
//								Toast.makeText(NewFriendsActivity.this, "添加好友失败", 3).show();
//							}
//                    		((MyApplication)getApplication()).getAllEntries();
//                    		finish();
							
							
//							String s = data.get(p).get("new_friend_name").toString();
//							if(!s.contains("Smack"))
//								s+="/Smack";
//							sendSubscribe(Presence.Type.subscribed, s);
////							sendSubscribe(Presence.Type.subscribe, data.get(p).get("new_friend_name").toString());
//							db.deleteFriendReq((Integer)data.get(p).get("new_friend_id"));
							
							String s = data.get(p).get("new_friend_name").toString();
							ChatManager cm = XmppTool.getConnection().getChatManager();
							newchat = cm.createChat(s+ "@"
									+ ((MyApplication) getApplication()).hostName, null);
							
							try {

								newchat.sendMessage("subscribed");

							} catch (XMPPException e) {
								e.printStackTrace();
							}
							
							if(((MyApplication)getApplication()).addUser(data.get(p).get("new_friend_name").toString(), data.get(p).get("new_friend_name").toString(),"my friend")){
							Toast.makeText(NewFriendsActivity.this, "添加好友成功", 3).show();
//							finish();
							((MyApplication)getApplication()).getAllEntries();
							
							
//				                    Map<String,Object> map2 = new HashMap<String, Object>();
//				                    map2.put("friend_avatar", getResources().getDrawable(R.drawable.default_avatar));
//				    				map2.put("friend_name", data.get(p).get("new_friend_name").toString());
//				    				map2.put("friend_nickName", data.get(p).get("new_friend_name").toString());
//				    				map2.put("friend_carrer", "");
//				    				map2.put("friend_gender", "");
//				    				map2.put("friend_zone", "");
//				    				map2.put("friend_sign", "");
//				    				((MyApplication)getApplication()).allContactsVcard.add(map2);
						}
						else{
							Toast.makeText(NewFriendsActivity.this, "添加好友失败", 3).show();
						}
							
							db.deleteFriendReq((Integer)data.get(p).get("new_friend_id"));
							finish();
						}
					}).setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
//					Presence presence = new Presence(
//                    Presence.Type.unsubscribe);//同意是subscribed   拒绝是unsubscribe
//					System.out.println("presence.setTo: "+data.get(p).get("new_friend_name").toString());
//					System.out.println("presence.setFrom: "+MyApplication.userName+"@"+MyApplication.hostName);
//                    presence.setTo(data.get(p).get("new_friend_name").toString());//接收方jid
//                    presence.setFrom(MyApplication.userName+"@"+MyApplication.hostName);//发送方jid
//                    XmppTool.getConnection().sendPacket(presence);//connection是你自己的XMPPConnection链接
//                    finish();
//							String s = data.get(p).get("new_friend_name").toString();
//							if(!s.contains("Smack"))
//								s+="/Smack";
//							sendSubscribe(Presence.Type.unsubscribe,s );
//							db.deleteFriendReq((Integer)data.get(p).get("new_friend_id"));
							
							String s = data.get(p).get("new_friend_name").toString();
							ChatManager cm = XmppTool.getConnection().getChatManager();
							newchat = cm.createChat(s+ "@"
									+ ((MyApplication) getApplication()).hostName, null);
							
							try {

								newchat.sendMessage("unsubscribe");

							} catch (XMPPException e) {
								e.printStackTrace();
							}
							db.deleteFriendReq((Integer)data.get(p).get("new_friend_id"));
							finish();
						}
					}).create().show();
				}
			}
		});
		
		
		newFriendListView.setAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (db != null) {
			db.close();
		}
		if (cursor != null) {
			cursor.close();
		}
		super.onDestroy();
	}
	
	
//	protected void sendSubscribe(Presence.Type type, String to) {
//		Presence presence = new Presence(type);
//		presence.setTo(to);
//		XmppTool.getConnection()
//				.sendPacket(presence);
//	}

}
