package scu.android.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;

import scu.android.application.MyApplication;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

public class SearchFriendActivity extends Activity{
	EditText search_input = null;
	TextView edit_delete = null,search_bt = null;
	Handler mHandler;
	ProgressDialog pd ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_search);
		initial();
	}
	
	private static final int POLL_INTERVAL = 300;
	private Runnable mPollTask = new Runnable() {
		public void run() {
			
			Message msg = mHandler.obtainMessage();
			msg.what = -1;
			mHandler.sendMessage(msg);
			
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};
	
	public void initial(){
		pd = new ProgressDialog(this);
		pd.setTitle("正在查询");
		pd.setMessage("请耐心等候");
		search_input = (EditText)findViewById(R.id.search_input);
		edit_delete = (TextView)findViewById(R.id.edit_delete);
		search_bt = (TextView)findViewById(R.id.search_bt);
		
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case -1:
					if(search_input.getText().toString().length()!=0){
						edit_delete.setVisibility(View.VISIBLE);
					}
					else edit_delete.setVisibility(View.INVISIBLE);
					break;
				case 0: //用户不存在
					if(pd.isShowing()){
						pd.dismiss();
					}
					Toast.makeText(SearchFriendActivity.this, "用户不存在", 3).show();
					break;
				case 1:   //用户在线/不在线
					if(pd.isShowing()){
						pd.dismiss();
					}
					
					Intent intent = new Intent();
					intent.putExtra("name", search_input.getText().toString());
					intent.setClass(SearchFriendActivity.this, SearchResultActivity.class);
				    startActivity(intent);
				    finish();
					break;
//				case 2://用户不在线
//					if(pd.isShowing()){
//						pd.dismiss();
//					}
//					
//					
//					break;
				case 3:
					Toast.makeText(SearchFriendActivity.this, "搜索异常，请稍后尝试", 3).show();
					break;
					default:
						break;
				}
				
				
				super.handleMessage(msg);
			}
			
		};
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}
	
	public void myOnclick(View v){
		switch(v.getId()){
		
		case R.id.edit_delete:
			search_input.setText("");
			edit_delete.setVisibility(View.INVISIBLE);
			
			break;
		case R.id.search_bt:
			
			if(search_input.getText().toString().equals("")){
				Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
				
			}
			else if(inEntities(search_input.getText().toString())){
				Toast.makeText(this, "该用户已经是你的好友", Toast.LENGTH_SHORT).show();
			}
			else{
				pd.show();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String strUrl = "http://"+((MyApplication)getApplication()).hostIp+":9090/plugins/presence/status?jid="+search_input.getText().toString()+"@"+((MyApplication)getApplication()).hostName+"&type=xml";
					System.out.println("strUrl"+strUrl);
				    short            shOnLineState    = 0;    //-不存在-
				    
				    try
				    {
				        URL             oUrl     = new URL(strUrl);
				    URLConnection     oConn     = oUrl.openConnection();
				    oConn.setConnectTimeout(10*1000);
				    if(oConn!=null)
				    {
				        BufferedReader     oIn = new BufferedReader(new InputStreamReader(oConn.getInputStream()));
				        if(null!=oIn)
				        {
				            String strFlag = oIn.readLine();
				            oIn.close();
				            
				            if(strFlag.indexOf("type=\"unavailable\"")>=0)
				            {
				                shOnLineState = 1;
				            }
				            if(strFlag.indexOf("type=\"error\"")>=0)
				            {
				                shOnLineState = 0;
				            }
				            else if(strFlag.indexOf("priority")>=0 || strFlag.indexOf("id=\"")>=0)
				            {
				                shOnLineState = 1;
				            }
				        }
				        Message msg = mHandler.obtainMessage();
				        msg.what = shOnLineState;
				        mHandler.sendMessage(msg);
				       
				    }
				    }
				    catch(Exception e)
				    {   
				    	e.printStackTrace();
				    	 Message msg = mHandler.obtainMessage();
					        msg.what = 3;
					        mHandler.sendMessage(msg);
				    }
				}
			}).start();
			}
			break;
			default:
				break;
			
		}
		
	}
	
	public boolean inEntities(String name){
		 List<RosterEntry> entries = ((MyApplication)getApplication()).getAllEntries();
		 System.out.println("entries.size()"+entries.size());
		 if(entries==null||entries.size()==0){
			 return false;
		 }
		 else{
			 for(RosterEntry entity:entries){
				 if(entity.getName().equals(name)){
					 return true;
				 }
				 else continue;
			 }
		 }
		
		 return false;
	}
	

}
