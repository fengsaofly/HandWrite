package scu.android.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import scu.android.application.MyApplication;
import scu.android.ui.ChatMainActivity;
import scu.android.util.XmppTool;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.demo.note.R;





@SuppressLint("NewApi")
public class FriendFragment extends Fragment 

{
	

	ListView friendListView = null;
	
    public List<RosterEntry> entries;
    List<Map<String,Object>> data = null;
    SimpleAdapter adapter = null;
    

	public static FriendFragment newInstance(){
		FriendFragment detail= new FriendFragment();
		
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

		System.out.println("oncreateView:  ");
		View view = inflater.inflate(R.layout.fragment_friends, container,false);
		friendListView = (ListView)view.findViewById(R.id.friendListView);
		
		entries = ((MyApplication)getActivity().getApplication()).entries;
		
		while(entries==null){
			entries = ((MyApplication)getActivity().getApplication()).entries;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
//		new Thread(new Runnable() {				
//			public void run() {
//				
//				
//				try {
//					
//
//					XmppTool.getConnection().login("jalsary", "123456");
////				             新建presence对象״̬
//					Presence presence = new Presence(Presence.Type.available);
//					XmppTool.getConnection().sendPacket(presence);
//					
//					System.out.println("正在登陆");
////					handler.sendEmptyMessage(1);
//					((MyApplication)getActivity().getApplication()).roster = XmppTool.getConnection().getRoster();
//						
//					((MyApplication)getActivity().getApplication()).entries = ((MyApplication)getActivity().getApplication()).getAllEntries();
//					
//				}
//				catch (XMPPException e) 
//				{
//					XmppTool.closeConnection();
//					
////					handler.sendEmptyMessage(2);
//				}					
//			}
//		}).start();
		data = new ArrayList<Map<String,Object>>();
		for(int i=0;i<entries.size();i++){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("friend_icon", R.drawable.actionbar_icon);
			map.put("friend_name",entries.get(i).getName());
			System.out.println("entries.get(i).getName(): "+entries.get(i).getName());
			if(!(entries.get(i).getName()==null||entries.get(i).getName().equals("")))
			data.add(map);
		}
		
		adapter = new SimpleAdapter(getActivity(), data, R.layout.friend_item, new String[]{"friend_icon","friend_name"},new int[]{R.id.friend_icon,R.id.friend_name} );
		friendListView.setAdapter(adapter);
		
		friendListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), ChatMainActivity.class);
				intent.putExtra("currentContact", data.get(position).get("friend_name").toString());
				startActivity(intent);
			}
		});
	    return view;
		        
		        
		        
	}

	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		 getActivity().unregisterReceiver( myBroadcastReciver);
//		db.close();
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
//		System.out.println("onResume:  ");
//		if(((MyApplication)getActivity().getApplication()).firstIn!=1){
//			gruops.clear();
//			childs.clear();
//			getListItem(view);
//			adapter.notifyDataSetChanged();
//			
//		}
//		if(((MyApplication)getActivity().getApplication()).firstIn==1)
//			((MyApplication)getActivity().getApplication()).firstIn=0;
//		
//		
		super.onResume();
	}



}
