package scu.android.ui;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
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
	String name = "";
	Chat newchat;
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
		name = intent.getStringExtra("name");
		friend_name.setText(name);
		tag_zone = (TextView)findViewById(R.id.tag_zone);
		VCard vard = ((MyApplication)getApplication()).getUserVcard(XmppTool.getConnection(), name);
		tag_zone.setText(vard.getAddressFieldHome("四川成都"));
		search_result_add= (TextView)findViewById(R.id.search_result_add);
		System.out.println("before:  allContactsVcard.size(): "+((MyApplication)getApplication()).getAllContactsVcard().size());
		search_result_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				/**
				 * 
				 * 发送添加好友请求
				 */
				// TODO Auto-generated method stub
				
//				try {
//					XmppTool.getConnection().getRoster().createEntry(name+"@"+((MyApplication)getApplication()).hostName, name+"@"+((MyApplication)getApplication()).hostName, new String[]{"my friend"});
//					Toast.makeText(SearchResultActivity.this, "请求添加好友成功", Toast.LENGTH_SHORT).show();
//				} catch (XMPPException e) {
//					// TODO Auto-generated catch block
//					Toast.makeText(SearchResultActivity.this, "请求添加好友失败", Toast.LENGTH_SHORT).show();
//					e.printStackTrace();
//				}
				if(((MyApplication)getApplication()).addUser(friend_name.getText().toString(), friend_name.getText().toString(),"my friend")){
					Toast.makeText(SearchResultActivity.this, "添加好友成功", 3).show();
					
					((MyApplication)getApplication()).getAllEntries();
//					 Map<String,Object> map2 = new HashMap<String, Object>();
//	                    map2.put("friend_avatar", getResources().getDrawable(R.drawable.default_avatar));
//	    				map2.put("friend_name", friend_name.getText().toString());
//	    				map2.put("friend_nickName", friend_name.getText().toString());
//	    				map2.put("friend_carrer", "");
//	    				map2.put("friend_gender", "");
//	    				map2.put("friend_zone", "");
//	    				map2.put("friend_sign", "");
//	    				((MyApplication)getApplication()).allContactsVcard.add(map2);
					
					ChatManager cm = XmppTool.getConnection().getChatManager();

					newchat = cm.createChat(friend_name.getText().toString() + "@"
							+ ((MyApplication) getApplication()).hostName, null);
					
					try {

						newchat.sendMessage("subscribe");

					} catch (XMPPException e) {
						e.printStackTrace();
					}


	    				System.out.println("allContactsVcard.size(): "+((MyApplication)getApplication()).getAllContactsVcard().size());
	    				finish();
				}
				else{
					Toast.makeText(SearchResultActivity.this, "添加好友失败", 3).show();
				}
//				sendSubscribe(Presence.Type.subscribe, name+"@"+((MyApplication)getApplication()).hostName+"/Smack");
			}
		});
	}

	
//	protected void sendSubscribe(Presence.Type type, String to) {
//		Presence presence = new Presence(type);
//		presence.setTo(to);
//		XmppTool.getConnection()
//				.sendPacket(presence);
//	}
}
