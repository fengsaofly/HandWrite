package scu.android.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

public class RegisterActivity extends Activity{
	TextView back = null,registerButton = null;
	ImageView iconImage = null;
	Button selectIconButton = null;
	AutoCompleteTextView emailValue = null;
	EditText userNameValue = null,userPassValue1 = null;
	boolean selectFlag=false;
	String selectIconName="";
	ProgressDialog pd = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_layout);
		initial();
	}
	
	public void initial(){
		pd = new ProgressDialog(this);
		pd.setTitle("正在注册");
		pd.setMessage("请等候");
		pd.setCancelable(true);
		back = (TextView)findViewById(R.id.buttonBack);
		registerButton = (TextView)findViewById(R.id.registerButton);
		iconImage = (ImageView)findViewById(R.id.iconImage);
		selectIconButton = (Button)findViewById(R.id.selectIconButton);
		emailValue = (AutoCompleteTextView)findViewById(R.id.emailValue);
		userNameValue = (EditText)findViewById(R.id.userNameValue);
		userPassValue1 = (EditText)findViewById(R.id.userPassValue1);
	}
	
	public void myOnclick(View v){
		switch(v.getId()){
		case R.id.buttonBack:
			finish();
			break;
		case R.id.registerButton:
			if(emailValue.getText().toString().equals("")||userNameValue.getText().toString().equals("")||userNameValue.getText().toString().equals("")){
				Toast.makeText(this, "请将信息填写完整", 3).show();
			}
			else if(userNameValue.getText().toString().toCharArray().length>10||userPassValue1.getText().toString().toCharArray().length>10){
				Toast.makeText(this, "用户名或密码过长（10位）", 3).show();
			}
			else {
				pd.show();
				new Thread(new Runnable() {				
					public void run() {
						
						regist(userNameValue.getText().toString(), userPassValue1.getText().toString());
//						case 0:
//							Toast.makeText(RegisterActivity.this, "服务器未响应", 3).show();
//							break;
//						case 1:
//							Toast.makeText(RegisterActivity.this, "恭喜你，注册成功", 3).show();
//							finish();
//							break;
//						case 2:
//							Toast.makeText(RegisterActivity.this, "用户已存在", 3).show();
//							break;
//						case 3:
//							Toast.makeText(RegisterActivity.this, "注册失败", 3).show();
//							break;
//							default:
//								break;
						
									
					}
				}).start();
			}
			break;
		case R.id.selectIconButton:
//			Intent intent = new Intent(this,SelectIconActivity.class);
//			intent.putExtra("type", 0);
//			startActivityForResult(intent, 100);
			break;
			
			default:
				break;
				
		}
		
	}
	
	 @Override  
	    protected void onActivityResult(int requestCode, int resultCode, Intent data)  
	    {  
	        //可以根据多个请求代码来作相应的操作  
	        if(20==resultCode)  
	        {  
	        	
	        	selectFlag = true;
	        	selectIconName = data.getExtras().getString("iconName");
	        	Resources resources = getResources();
    			int indentify = resources.getIdentifier(getPackageName()+":drawable/"+selectIconName, null, null);
	        	iconImage.setImageResource(indentify);
	        	
	        }  
	        super.onActivityResult(requestCode, resultCode, data);  
	    } 
	 
	 
	 public  boolean checkEmail(String email)
	 
	         {// 验证邮箱的正则表达式
	 
		 boolean flag = false;
		  try{
		
		   String check =  "[a-zA-Z_]{1,}[0-9]{0,}@(([a-zA-z0-9]-*){1,}\\.){1,3}[a-zA-z\\-]{1,}";
//				   "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		   Pattern regex = Pattern.compile(check);
		   Matcher matcher = regex.matcher(email);
		   flag = matcher.matches();
		  }catch(Exception e){
//		   Log.error("验证邮箱地址错误", e);
		   flag = false;
		  }
		  
		  return flag;
	
	         }
	 
	 
		/**
		 * 注册
		 * 
		 * @param account 注册帐号
		 * @param password 注册密码
		 * @return 1、注册成功 0、服务器没有返回结果2、这个账号已经存在3、注册失败
		 */
		public int regist(String account, String password) {
			int result2 = -1;
			if (XmppTool.getConnection() == null)
				result2= 0;
			Registration reg = new Registration();
			reg.setType(IQ.Type.SET);
			reg.setTo(XmppTool.getConnection().getServiceName());
			reg.setUsername(account);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
			reg.setPassword(password);
			reg.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
			PacketFilter filter = new AndFilter(new PacketIDFilter(
					reg.getPacketID()), new PacketTypeFilter(IQ.class));
			PacketCollector collector = XmppTool.getConnection()
					.createPacketCollector(filter);
			XmppTool.getConnection().sendPacket(reg);
			IQ result = (IQ) collector.nextResult(SmackConfiguration
					.getPacketReplyTimeout());
			// Stop queuing results
			collector.cancel();// 停止请求results（是否成功的结果）
			if (result == null) {
				Log.e("RegistActivity", "No response from server.");
				result2= 0;
			} else if (result.getType() == IQ.Type.RESULT) {
				result2= 1;
			} else { // if (result.getType() == IQ.Type.ERROR)
				if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
					Log.e("RegistActivity", "IQ.Type.ERROR: "
							+ result.getError().toString());
					result2= 2;
				} else {
					Log.e("RegistActivity", "IQ.Type.ERROR: "
							+ result.getError().toString());
					result2= 3;
				}
			}
			handler.sendEmptyMessage(result2);
			return result2;
		}
		
		Handler handler = new Handler(){
			public void handleMessage(Message msg){
				if(pd.isShowing())
					pd.dismiss();
				switch(msg.what){
				case 0:
					Toast.makeText(RegisterActivity.this, "服务器未响应", 3).show();
					break;
				case 1:
					Toast.makeText(RegisterActivity.this, "恭喜你，注册成功", 3).show();
					String picName = "";
					if(selectFlag)
						picName = selectIconName;
					else 
						picName = "dota_1";
//					UploadIconThread uit = new UploadIconThread(RegisterActivity.this, RegisterActivity.this, handler, userNameValue.getText().toString(), picName);
//					uit.start();
//					finish();
					break;
				case 2:
					Toast.makeText(RegisterActivity.this, "用户已存在", 3).show();
					break;
				case 3:
					Toast.makeText(RegisterActivity.this, "注册失败", 3).show();
					break;
				case 4:
					System.out.println("添加到sina云服务器成功");
					default:
						break;
					
				}
				super.handleMessage(msg);
			}
		};



}
