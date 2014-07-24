package scu.android.ui;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;

import scu.android.application.MyApplication;
import scu.android.note.ActionBarActivity;
import scu.android.util.GetAllContactVcardThread;
import scu.android.util.GetOffLineMessageThread;
import scu.android.util.SendIQTestrr;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
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
	public static XMPPConnection mConnection;
	public static Roster mRoster;

	private ArrayList<RosterEntry> mEntries;
	List<RosterEntry> EntriesList;
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
			
			switch(msg.what){
			case 1:
				if(pd.isShowing())
					pd.dismiss();
//				getOffLineMessage();
				
				
//				((MyApplication)getApplication()).loginFlag = true;
//				Intent intent = new Intent();
//				intent.setAction("cn.abel.action.loginBroadcast");
//
//				// 要发送的内容
//				intent.putExtra("loginFlag", true);
//
//				// 发送 一个无序广播
//				sendBroadcast(intent);
				
				
				((MyApplication) getApplication()).roster = XmppTool.getConnection().getRoster();

				((MyApplication) getApplication()).entries = ((MyApplication) getApplication())
						.getAllEntries();
				System.out.println("输出所有的entry： ");
//				boolean flag = false;
				List<Integer> deleteNullItem = new ArrayList<Integer>();
				for(int i=0;i<((MyApplication) getApplication()).entries.size();i++){
//					if(entry.getName().equals("null")){
//						((MyApplication)getApplication()).removeUser("null");
//					}
					if(((MyApplication) getApplication()).entries.get(i).getName()==null){
						deleteNullItem.add(i);
					}
					System.out.println("entry："+((MyApplication) getApplication()).entries.get(i).getName()+"\n");
				}
				for(int i=0;i<deleteNullItem.size();i++){
					((MyApplication) getApplication()).entries.remove(deleteNullItem.get(i));
				}
				
//				((MyApplication) getApplication()).entries = ((MyApplication) getApplication())
//						.getAllEntries();
				((MyApplication)getApplication()).loginFlag=true;
				
				
//				 mConnection = XmppTool.getConnection();
//				 
//				 mEntries = new ArrayList<RosterEntry>();
//				 mRoster = mConnection.getRoster();
//				 ((MyApplication)getApplication()).entries = mEntries;
//				
//				 ((MyApplication)getApplication()).roster = mRoster;
//				
//				 Collection<RosterEntry> entries = mRoster.getEntries();
//				 Iterator<RosterEntry> i = entries.iterator();
//				 EntriesList = new ArrayList<RosterEntry>();
//					while (i.hasNext())
//						EntriesList.add(i.next());
				
				
				new Thread(new Runnable() {  //获取自己的vcard
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					VCard vCard = new VCard();
					ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
							new org.jivesoftware.smackx.provider.VCardProvider());
					try {
						vCard.load(XmppTool.getConnection(), userName.getText().toString() + "@" + MyApplication.hostName);
//						myIconDrawable
						if (vCard == null || vCard.getAvatar() == null)
						((MyApplication)getApplication()).myIconDrawable =  getResources().getDrawable(R.drawable.default_avatar);
						else{
							ByteArrayInputStream bais = new ByteArrayInputStream(vCard.getAvatar());
							((MyApplication)getApplication()).myIconDrawable = ((MyApplication)getApplication()).bitmap2Drawable(BitmapFactory.decodeStream(bais)) ; 
						}
						if ("".equals(vCard.getNickName())
								|| null == vCard.getNickName()) {
							System.out.println("昵称是空的");
							vCard.setNickName("快乐的汤姆猫");
							
						}
//						((MyApplication) getApplication()).vCard = vCard;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}).start();
					

//				/**
//				 * 
//				 * 启动监听好友服务（添加好友请求、同意、好友状态等）
//				 */
//				Intent presenceServiceIntent = new Intent(LoginActivity.this,PresenceService.class);
//				startService(presenceServiceIntent);
				
				
				/**
				 * 
				 * 触发与服务器交互
				 */
				
				try {
					System.out.println("aljglg");
					SendIQTestrr.run();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				GetAllContactVcardThread gacvt = new GetAllContactVcardThread(LoginActivity.this, LoginActivity.this,((MyApplication) getApplication()).entries,handler);
				gacvt.execute();
				
				GetOffLineMessageThread golmt = new GetOffLineMessageThread(LoginActivity.this, LoginActivity.this);
				golmt.execute();
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, ActionBarActivity.class);
				intent.putExtra("USERID", "");
				startActivity(intent);
				
				
				//测试添加好友
//				try {
//					XmppTool.getConnection().getRoster().createEntry("bbb"+"@"+((MyApplication)getApplication()).hostName, "bbb", new String[]{"我的好友"});
//				} catch (XMPPException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				
				finish();
				
				

				
				
				


				
				break;
			case 2:
				if(pd.isShowing())
					pd.dismiss();
				Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
				break;
			case 3:
//				if(pd.isShowing())
//					pd.dismiss();
//				Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
//				
//				System.out.println("进入异步线程。。。");
//				GetOffLineMessageThread golmt = new GetOffLineMessageThread(LoginActivity.this, LoginActivity.this);
//				golmt.execute();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public void myOnclick(View v){
		switch(v.getId()){

		case R.id.register_btn:  //注册
			
			startActivity(new Intent(this,RegisterActivity.class));
			break;
		case R.id.loginButton:
			System.out.println("请再给我出错");
			final String USERID =userName.getText().toString();
			
			final String PWD = passWord.getText().toString();
			
			pd.show();
			new Thread(new Runnable() {				
				public void run() {
					
					
					try {
						
						Presence presence = new Presence(Presence.Type.unavailable);
						XmppTool.getConnection().sendPacket(presence);
						XmppTool.getConnection().login(USERID, PWD);
//						Presence presence = new Presence(Presence.Type.available);
//						XmppTool.getConnection().sendPacket(presence);
						System.out.println("在线登陆：成功。。。");
//						getOffLineMessage();
//					             新建presence对象  ״̬
//						GetOffLineMessageThread golmt = new GetOffLineMessageThread(LoginActivity.this, LoginActivity.this);
//						golmt.execute();
						
						
						SharedPreferences.Editor editor = sp.edit();
						editor.putString("account", USERID);
						editor.putString("password", PWD);
						editor.commit();
						((MyApplication)getApplication()).userName = USERID;
						((MyApplication)getApplication()).passWord = PWD;
						VCard vcard = ((MyApplication)getApplication()).getUserVcard(XmppTool.getConnection(),USERID);
						if(vcard.getNickName()!=null&&(!vcard.getNickName().equals(""))){
							((MyApplication)getApplication()).nickName = vcard.getNickName();
						}
						else ((MyApplication)getApplication()).nickName = USERID;
						
						
						handler.sendEmptyMessage(1);
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
//		back = (TextView)findViewById(R.id.buttonBack);
		register = (TextView)findViewById(R.id.register_btn);
		loginButton = (TextView)findViewById(R.id.loginButton);
		userName = (AutoCompleteTextView)findViewById(R.id.loginUserNameValue);
		userName.setText(account);
		passWord = (EditText)findViewById(R.id.userPassValue);
		passWord.setText(password);
//		login_show_password = (CheckBox)findViewById(R.id.login_show_password);
		pd = new ProgressDialog(this);
		pd.setTitle("正在登陆");
		pd.setMessage("请耐心等候");
		pd.setCancelable(false);
		final int type = passWord.getInputType();
//		login_show_password.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//				// TODO Auto-generated method stub
//				if(arg1){
//					
//					passWord.setInputType(InputType.TYPE_CLASS_TEXT);
//				}
//				else passWord.setInputType(type);
//			}
//		});
		
	}
	

	
	
//	public void getAllContactsVcard(List<RosterEntry> entries){
//		ByteArrayInputStream bais = null;
//		List<Map<String,Object>> allContactsVcard = new ArrayList<Map<String,Object>>();
//		for(RosterEntry item : entries){
//			Map<String,Object> map = new HashMap<String, Object>();
//			if(item.getName()!=null&&(!item.getName().equals(""))&&(!item.getName().equals("null"))){
//				try {
//					VCard vcard = new VCard();
//					// 加入这句代码，解决No VCard for
////					ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
////							new org.jivesoftware.smackx.provider.VCardProvider());
//
////					vcard.load(XmppTool.getConnection(), item.getName() + "@" + ((MyApplication)getApplication()).hostName);
//					vcard = ((MyApplication)getApplication()).getUserVcard(XmppTool.getConnection(),item.getName());
//
//					if (vcard == null || vcard.getAvatar() == null)
//						map.put("friend_avatar", getResources().getDrawable(R.drawable.default_avatar));
//					else{
//					bais = new ByteArrayInputStream(vcard.getAvatar());
//						map.put("friend_avatar",((MyApplication)getApplication()).bitmap2Drawable(BitmapFactory.decodeStream(bais)) ); 
//					}
//					map.put("friend_name", item.getName());
//					if(vcard.getNickName()==null||vcard.getNickName().equals("")){
//						map.put("friend_nickName", "");
//					}
//					else map.put("friend_nickName", vcard.getNickName());
//					
//					if(vcard.getMiddleName()==null||vcard.getMiddleName().equals("")){
//						map.put("friend_carrer", "");
//					}
//					else map.put("friend_carrer", vcard.getMiddleName());
//					
//					if(vcard.getFirstName()==null||vcard.getFirstName().equals("")){
//						map.put("friend_gender", "");
//					}
//					else map.put("friend_gender", vcard.getFirstName());
//					
//					if(vcard.getAddressFieldHome("zone")==null||vcard.getAddressFieldHome("zone").equals("")){
//						map.put("friend_zone", "");
//					}
//					else map.put("friend_zone", vcard.getAddressFieldHome("zone"));
//					
//					if(vcard.getLastName()==null||vcard.getLastName().equals("")){
//						map.put("friend_sign", "");
//					}
//					else map.put("friend_sign",vcard.getLastName());
////					map.put("friend_nickName", vcard.getNickName());
////					map.put("friend_carrer", vcard.getMiddleName());
////					map.put("friend_gender", vcard.getFirstName());
////					map.put("friend_zone", vcard.getAddressFieldHome("zone"));
////					map.put("friend_sign", vcard.getLastName());
//					
//					allContactsVcard.add(map);
//	
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		handler.sendEmptyMessage(3);
//		((MyApplication)getApplication()).allContactsVcard = allContactsVcard;
//		Intent intent = new Intent();
//		intent.setClass(LoginActivity.this, ActionBarActivity.class);
//		intent.putExtra("USERID", "");
//		startActivity(intent);
//		
//		finish();
//		
//		
//	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(pd!=null){
			pd = null;
		}
		super.onDestroy();
	}
	
	
	 

}
