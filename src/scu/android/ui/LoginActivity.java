package scu.android.ui;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.packet.VCard;

import scu.android.application.MyApplication;
import scu.android.note.ActionBarActivity;
import scu.android.util.GetOffLineMessageThread;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
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

		initial();
		
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			if(pd.isShowing())
				pd.dismiss();
			switch(msg.what){
			case 1:
				
//				getOffLineMessage();
				
				Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
				((MyApplication)getApplication()).loginFlag = true;
				((MyApplication) getApplication()).roster = XmppTool.getConnection().getRoster();

				((MyApplication) getApplication()).entries = ((MyApplication) getApplication())
						.getAllEntries();
				System.out.println("所有好友：  ");
				for(RosterEntry entry:((MyApplication) getApplication()).entries){
					System.out.println(entry.getName()+"\n");
				}
				
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
				
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						getAllContactsVcard(((MyApplication) getApplication()).entries);
					}
				}).start();
				
				
				handler.sendEmptyMessage(3);
				
				
				
				
				
				break;
			case 2:
				
				Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				System.out.println("进入异步线程。。。");
				GetOffLineMessageThread golmt = new GetOffLineMessageThread(LoginActivity.this, LoginActivity.this);
				golmt.execute();
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
						
						Presence presence = new Presence(Presence.Type.unavailable);
						XmppTool.getConnection().sendPacket(presence);
						XmppTool.getConnection().login(USERID, PWD);
						System.out.println("离线登陆：成功。。。");
//						getOffLineMessage();
//					             新建presence对象  ״̬
//						GetOffLineMessageThread golmt = new GetOffLineMessageThread(LoginActivity.this, LoginActivity.this);
//						golmt.execute();
						
						handler.sendEmptyMessage(1);
						SharedPreferences.Editor editor = sp.edit();
						editor.putString("account", USERID);
						editor.putString("password", PWD);
						editor.commit();
						((MyApplication)getApplication()).userName = USERID;
						((MyApplication)getApplication()).passWord = PWD;
						((MyApplication)getApplication()).loginFlag=true;
//						finish();
					}
					catch (XMPPException e) 
					{
						XmppTool.closeConnection();
						
						handler.sendEmptyMessage(2);
					}catch(Exception e){
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
	
	public void getOffLineMessage(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				OfflineMessageManager offlineManager = new OfflineMessageManager(XmppTool.getConnection());  
			    try {  
			        Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager  
			                .getMessages();  

//			        System.out.println(offlineManager.supportsFlexibleRetrieval());  
			        System.out.println("离线消息数量: " + offlineManager.getMessageCount());  

			          
			        Map<String,ArrayList<org.jivesoftware.smack.packet.Message>> offlineMsgs = new HashMap<String,ArrayList<org.jivesoftware.smack.packet.Message>>();  
			          
			        while (it.hasNext()) {  
			          org.jivesoftware.smack.packet.Message message = it.next();  
			            System.out  
			                    .println("收到离线消息, Received from 【" + message.getFrom()  
			                            + "】 message: " + message.getBody());  
			            String fromUser = message.getFrom().split("/")[0];  

			            if(offlineMsgs.containsKey(fromUser))  
			            {  
			                offlineMsgs.get(fromUser).add(message);  
			            }else{  
			                ArrayList<org.jivesoftware.smack.packet.Message> temp = new ArrayList<org.jivesoftware.smack.packet.Message>();  
			                temp.add(message);  
			                offlineMsgs.put(fromUser, temp);  
			            }  
			        }  

			        //在这里进行处理离线消息集合......  
			        Set<String> keys = offlineMsgs.keySet();  
			        Iterator<String> offIt = keys.iterator();  
			        while(offIt.hasNext())  
			        {  
			            String key = offIt.next();  
			            ArrayList<org.jivesoftware.smack.packet.Message> ms = offlineMsgs.get(key);  
//			            TelFrame tel = new TelFrame(key);  
//			            ChatFrameThread cft = new ChatFrameThread(key, null);  
//			            cft.setTel(tel);  
//			            cft.start();  
//			            for (int i = 0; i < ms.size(); i++) {  
//			                tel.messageReceiveHandler(ms.get(i));  
//			            }  
			            
			            for(org.jivesoftware.smack.packet.Message msg : ms){
			            	System.out.println("消息：   "+msg.toString());
			            }
			        } 
			        
			  
			          
			          
			        offlineManager.deleteMessages();  
			        
			        System.out.println("获取到了离线消息。。。");
					
			  					Presence presence2 = new Presence(Presence.Type.available);
			  					XmppTool.getConnection().sendPacket(presence2);
			  					
			  					System.out.println("切换为正常登陆状态。。。");
			    } catch (Exception e) {  
			        e.printStackTrace();  
			    } 
			}
		}).start();
		
	}
	
	
	public void getAllContactsVcard(List<RosterEntry> entries){
		ByteArrayInputStream bais = null;
		List<Map<String,Object>> allContactsVcard = new ArrayList<Map<String,Object>>();
		for(RosterEntry item : entries){
			Map<String,Object> map = new HashMap<String, Object>();
			if(item.getName()!=null&&(!item.getName().equals(""))&&(!item.getName().equals("null"))){
				try {
					VCard vcard = new VCard();
					// 加入这句代码，解决No VCard for
					ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
							new org.jivesoftware.smackx.provider.VCardProvider());

					vcard.load(XmppTool.getConnection(), item.getName() + "@" + XmppTool.getConnection().getServiceName());

					if (vcard == null || vcard.getAvatar() == null)
						map.put("friend_avatar", getResources().getDrawable(R.drawable.default_avatar));
					else{
					bais = new ByteArrayInputStream(vcard.getAvatar());
						map.put("friend_avatar",((MyApplication)getApplication()).bitmap2Drawable(BitmapFactory.decodeStream(bais)) ); 
					}
					map.put("friend_name", item.getName());
					if(vcard.getNickName()==null||vcard.getNickName().equals("")){
						map.put("friend_nickName", "");
					}
					else map.put("friend_nickName", vcard.getNickName());
					
					if(vcard.getMiddleName()==null||vcard.getMiddleName().equals("")){
						map.put("friend_carrer", "");
					}
					else map.put("friend_carrer", vcard.getMiddleName());
					
					if(vcard.getFirstName()==null||vcard.getFirstName().equals("")){
						map.put("friend_gender", "");
					}
					else map.put("friend_gender", vcard.getFirstName());
					
					if(vcard.getAddressFieldHome("zone")==null||vcard.getAddressFieldHome("zone").equals("")){
						map.put("friend_zone", "");
					}
					else map.put("friend_zone", vcard.getAddressFieldHome("zone"));
					
					if(vcard.getLastName()==null||vcard.getLastName().equals("")){
						map.put("friend_sign", "");
					}
					else map.put("friend_sign",vcard.getLastName());
//					map.put("friend_nickName", vcard.getNickName());
//					map.put("friend_carrer", vcard.getMiddleName());
//					map.put("friend_gender", vcard.getFirstName());
//					map.put("friend_zone", vcard.getAddressFieldHome("zone"));
//					map.put("friend_sign", vcard.getLastName());
					
					allContactsVcard.add(map);
	

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		((MyApplication)getApplication()).allContactsVcard = allContactsVcard;
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, ActionBarActivity.class);
		intent.putExtra("USERID", "");
		startActivity(intent);
		finish();
		
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(pd!=null){
			pd = null;
		}
		super.onDestroy();
	}
	
	
	 

}
