package scu.android.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import scu.android.application.MyApplication;
import scu.android.util.XmppTool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.demo.note.R;

public class MultiRoomListActivity extends Activity{
	ListView multiListView = null;
	SimpleAdapter adapter = null;
	List<Map<String,Object>> data = null;
	SharedPreferences sp = null;
	private MultiUserChat muc;
	
	
	boolean existFlag = false;
	int existPosition = -1;
	int tagFlag=-1;
	public Handler handler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {

				switch (msg.what) {
				case 10:
					
					if(adapter ==null){
//						multiAdapter = new SimpleAdapter(getActivity(), multiData, R.layout.friend_item, new String[]{"friend_icon","friend_name"},new int[]{R.id.friend_icon,R.id.friend_name} );
//						multiListView.setAdapter(multiAdapter);
						
						adapter = new SimpleAdapter(MultiRoomListActivity.this, data, R.layout.group_item, new String[]{"roomName"},new int[]{R.id.group_name} );
						multiListView.setAdapter(adapter);
					}else{
//						multiAdapter.notifyDataSetChanged();
//						multiListView.invalidate();
						adapter.notifyDataSetChanged();
						multiListView.invalidate();
					}
					
					break;
				}
			}
		};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.multiroom_list);
		initial();
	}
	
	public void initial(){
		
		multiListView = (ListView)findViewById(R.id.multiListView);
		data = new ArrayList<Map<String,Object>>();
		sp = ((MyApplication)getApplication()).sp;
		
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
				addMultiList();
				Message msg = new Message();
				msg.what = 10;
				handler.sendMessage(msg);
			}
		}).start();
		
		multiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MultiRoomListActivity.this, ActivityMultiRoom.class);
				intent.putExtra("jid", data.get(position).get("roomName") + "@conference"+"."+((MyApplication)getApplication()).hostName);
				intent.putExtra("action", "join");
				startActivity(intent);
				
			}
		});
		
		
		multiListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				String tag = "";
				
				final int p = position;
				muc = new MultiUserChat(XmppTool.getConnection(), data.get(position).get("roomName") + "@conference"+"."+((MyApplication)getApplication()).hostName);
				if(data.get(position).get("roomOwner").equals(((MyApplication)getApplication()).userName)){
					tag = "您是该群的群主，确定解散该群么?";
					tagFlag = 0;
				}
				else {
					tag = "确定退出该群吗?";
					tagFlag = 1;
				}
				new AlertDialog.Builder(MultiRoomListActivity.this).setTitle("温馨提示").setMessage(tag).setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						if(tagFlag==0){
						try {
							muc.destroy("不想要了", data.get(p).get("roomName") + "@conference"+"."+((MyApplication)getApplication()).hostName);
						} catch (XMPPException e) {
							e.printStackTrace();
							Log.i("tag", "销毁失败");
						}
						
						
						Toast.makeText(MultiRoomListActivity.this, "销毁成功", Toast.LENGTH_SHORT).show();
						}
						else{
					
							Toast.makeText(MultiRoomListActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
						}
						
						checkExist(data.get(p).get("roomName").toString());
						data.remove(existPosition);
						SharedPreferences.Editor editor = ((MyApplication)getApplication()).sp.edit();
						editor.clear();
						((MyApplication)getApplication()).saveArray(data);
						adapter.notifyDataSetChanged();
						multiListView.invalidate();
						
						finish();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				}).create().show();
				return false;
			}
		});
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
//		addMultiList();
//		Message msg = new Message();
//		msg.what = 10;
//		handler.sendMessage(msg);
		super.onResume();
		
	}
	
	
	public Boolean checkExist(String roomName){
		for(int i=0;i<data.size();i++){
			Map<String,Object> item = data.get(i);
			if(item.get("roomName").equals(roomName)){
				existFlag = true;
				existPosition = i;
				return true;
			}
			else continue;
		}
		return false;
	}

	
	
	public void addMultiList(){
		((MyApplication)getApplication()).loadArray(data);
		
	}

}
