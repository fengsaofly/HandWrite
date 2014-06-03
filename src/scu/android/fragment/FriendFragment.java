package scu.android.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.RosterEntry;

import scu.android.application.MyApplication;
import scu.android.ui.ChatMainActivity;
import scu.android.ui.MultiRoomListActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.demo.note.R;





@SuppressLint("NewApi")
public class FriendFragment extends Fragment 

{
	

	ListView friendListView = null;
//	ListView multiListView = null;
    public List<RosterEntry> entries;
    List<Map<String,Object>> friendData = null;
//    List<Map<String,Object>> multiData = null;
    SimpleAdapter friendAdapter = null;
//    SimpleAdapter multiAdapter = null;
    private ProgressDialog pd;
//    List<DiscoverItems.Item> items = new ArrayList<DiscoverItems.Item>();
    public Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 10:
				pd.dismiss();
				if(friendAdapter ==null){
//					multiAdapter = new SimpleAdapter(getActivity(), multiData, R.layout.friend_item, new String[]{"friend_icon","friend_name"},new int[]{R.id.friend_icon,R.id.friend_name} );
//					multiListView.setAdapter(multiAdapter);
					
					friendAdapter = new SimpleAdapter(getActivity(), friendData, R.layout.friend_item, new String[]{"friend_icon","friend_name"},new int[]{R.id.friend_icon,R.id.friend_name} );
					friendListView.setAdapter(friendAdapter);
				}else{
//					multiAdapter.notifyDataSetChanged();
//					multiListView.invalidate();
					friendAdapter.notifyDataSetChanged();
					friendListView.invalidate();
				}
				
				break;
			}
		}
	};
    

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
		pd = new ProgressDialog(getActivity());
		pd.setTitle("提示");
		pd.setMessage("正在更新列表");
		pd.show();
		View view = inflater.inflate(R.layout.fragment_friends, container,false);
		friendListView = (ListView)view.findViewById(R.id.friendListView);
//		multiListView = (ListView)view.findViewById(R.id.multiListView);
		
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
		
		friendData = new ArrayList<Map<String,Object>>(); 
//		multiData = new ArrayList<Map<String,Object>>();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				/**
				 * 添加聊天室列表
				 */
				
//				addMultiRoom();
				
				/**
				 * 添加好友列表
				 */
				addChatList();
				Message msg = new Message();
				msg.what = 10;
				handler.sendMessage(msg);
			}
		}).start();
		
		
		
		
		
		friendListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(position!=0){
				Intent intent = new Intent();
				intent.setClass(getActivity(), ChatMainActivity.class);
				intent.putExtra("currentContact", friendData.get(position).get("friend_name").toString());
				startActivity(intent);
				}
				else{
					Intent intent = new Intent(getActivity(),
							MultiRoomListActivity.class);
					
					startActivity(intent);
				}
			}
		});
		
		
		friendListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				final int p = position;
				if(position!=0){
					new AlertDialog.Builder(getActivity()).setMessage("确认删除好友"+friendData.get(position).get("friend_name").toString()+"吗").setTitle("系统提示")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							if(((MyApplication)getActivity().getApplication()).removeUser(friendData.get(p).get("friend_name").toString())){
								Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
								friendData.remove(p);
								friendAdapter.notifyDataSetChanged();
								friendListView.invalidate();
							}
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							
						}
					}).create().show();
				}
				return false;
			}
		});
		
//		multiListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View view,
//					int position, long id) {
//				// 获取用户，跳到会议
//				DiscoverItems.Item room = items.get(position);
//				Intent intent = new Intent(getActivity(),
//						ActivityMultiRoom.class);
//				intent.putExtra("jid", room.getEntityID());
//				intent.putExtra("action", "join");
//				startActivity(intent);
//			}
//		});
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
	
	
//	public void addMultiRoom(){
//		// 获得与XMPPConnection相关的ServiceDiscoveryManager
//				ServiceDiscoveryManager discoManager = ServiceDiscoveryManager
//						.getInstanceFor(XmppTool.getConnection());
//
//				// 获得指定XMPP实体的项目
//				// 这个例子获得与在线目录服务相关的项目
//				DiscoverItems discoItems;
//				try {
//					discoItems = discoManager
//							.discoverItems("conference"+"."+((MyApplication)getActivity().getApplication()).hostName);
//					// 获得被查询的XMPP实体的要查看的项目
//					Iterator it = discoItems.getItems();
//					// 显示远端XMPP实体的项目
//					while (it.hasNext()) {
//						DiscoverItems.Item item = (DiscoverItems.Item) it.next();
//						Map<String,Object> map = new HashMap<String, Object>();
//						map.put("friend_icon", R.drawable.actionbar_icon);
//						map.put("friend_name",item.getName());
//						items.add(item);
//						
//						multiData.add(map);
//					}
//				} catch (XMPPException e) {
//					e.printStackTrace();
//				}
//	}


	public void addChatList(){
		Map<String,Object> map2 = new HashMap<String, Object>();
		map2.put("friend_icon", R.drawable.group_icon);
		map2.put("friend_name","群聊");
		friendData.add(map2);
		for(int i=0;i<entries.size();i++){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("friend_icon", R.drawable.actionbar_icon);
			map.put("friend_name",entries.get(i).getName());
			
			System.out.println("entries.get(i).getName(): "+entries.get(i).getName());
			if(!(entries.get(i).getName()==null||entries.get(i).getName().equals("")))
				friendData.add(map);
		}
	}

}
