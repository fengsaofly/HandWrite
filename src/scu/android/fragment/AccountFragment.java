package scu.android.fragment;




import org.jivesoftware.smackx.packet.VCard;

import scu.android.application.MyApplication;
import scu.android.ui.AccountSettingActivity;
import scu.android.ui.LoginActivity;
import scu.android.ui.RegisterActivity;
import scu.android.util.XmppTool;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demo.note.R;



public class AccountFragment extends Fragment{
	
	RelativeLayout myAcccountLoginAndRegistLayout = null,layLogout = null,laySet = null,accountSetting = null;
	ImageView photo = null;
	TextView uid = null,member = null;
	Button regist_btn = null,login_btn = null;
	SharedPreferences sp  = null;

	public static AccountFragment newInstance(){
		AccountFragment detail= new AccountFragment();
		
  	    return detail;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	     super.onActivityCreated(savedInstanceState);
	     
	     
		
	     
	 
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_question, container,false);
		myAcccountLoginAndRegistLayout = (RelativeLayout)view.findViewById(R.id.myAcccountLoginAndRegistLayout);
		layLogout = (RelativeLayout)view.findViewById(R.id.layLogout);
		accountSetting = (RelativeLayout)view.findViewById(R.id.accountSetting);
		laySet = (RelativeLayout)view.findViewById(R.id.laySet);
		photo = (ImageView)view.findViewById(R.id.photo);
		photo.setImageBitmap(((MyApplication)getActivity().getApplication()).getUserImage(XmppTool.getConnection(), ((MyApplication)getActivity().getApplication()).userName));
		
		regist_btn = (Button)view.findViewById(R.id.regist_btn);
		login_btn = (Button)view.findViewById(R.id.login_btn);
		uid = (TextView)view.findViewById(R.id.uid);
		member = (TextView)view.findViewById(R.id.member);
		
//		sp = getActivity().getSharedPreferences("bnj", getActivity().MODE_PRIVATE);
//		String sign = sp.getString("sign", "");
		VCard vcard = ((MyApplication)getActivity().getApplication()).getUserVcard(XmppTool.getConnection(),((MyApplication)getActivity().getApplication()).userName);
		 
		member.setText("个性签名: "+vcard.getLastName());
		
		if(((MyApplication)getActivity().getApplication()).loginFlag==false){
			accountSetting.setVisibility(View.GONE);
		}
		
		else{
			myAcccountLoginAndRegistLayout.setVisibility(View.INVISIBLE);
			uid.setText("UID: "+((MyApplication)getActivity().getApplication()).userName);
			
				 try{
				 int indentify = getActivity().getResources().getIdentifier(getActivity().getPackageName()+":drawable/"+((MyApplication)getActivity().getApplicationContext()).iconMap.get(((MyApplication)getActivity().getApplication()).userName), null, null);
        			if(indentify>0){ 
        				photo.setImageDrawable((getActivity().getResources().getDrawable(indentify)));
        				
        			}
        			
				 }catch(NullPointerException e){
//					 map.put("find_icon", getActivity().getResources().getDrawable(R.drawable.dota_1));
				 }
			
			
			
		}
		layLogout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MyApplication)getActivity().getApplication()).loginFlag=false;
				XmppTool.closeConnection();
				myAcccountLoginAndRegistLayout.setVisibility(View.VISIBLE);
				member.setText("个性签名: ");
				uid.setText("UID: ");
				photo.setImageDrawable((getActivity().getResources().getDrawable(R.drawable.actionbar_icon)));
				accountSetting.setVisibility(View.GONE);
			}
		});
		
		laySet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),AccountSettingActivity.class));
//				getActivity().finish();
			}
		});
		regist_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),RegisterActivity.class));
				getActivity().finish();
			}
		});
		login_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			startActivity(new Intent(getActivity(),LoginActivity.class));
				getActivity().finish();
			}
		});
	      return view;
	  } 
	
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 1:

				break;
			case 2:
				
				

				break;
			
			}
			super.handleMessage(msg);
		}
		
	};

}
