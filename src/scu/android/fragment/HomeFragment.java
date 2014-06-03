package scu.android.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scu.android.db.ChatRecord;
import scu.android.db.DbManager2;
import scu.android.util.HomeAdapter;
import scu.android.util.TimeRender;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.demo.note.R;





public class HomeFragment extends Fragment{
	TextView home_info_nums = null;
	ListView home_List = null;
	List<Map<String,Object>> data;
	SimpleAdapter adapter;
	SharedPreferences sp;

	DbManager2 db;
	Cursor cursor;
	public static HomeFragment newInstance(){
		HomeFragment detail= new HomeFragment();
		
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

		View view = inflater.inflate(R.layout.fragment_home, container,false);
		data = new ArrayList<Map<String,Object>>();
		db = new DbManager2(getActivity());
		sp = getActivity().getSharedPreferences("poti",  0);
		int firstIn = sp.getInt("firstIn", 0);
		if(firstIn==0){
			addTestDataToDb();  //如果是第一次进入应用，添加测试数据
		}
		
		cursor = db.queryRecent();
		cursor.moveToPosition(-1);
		while(cursor.moveToNext()){
//			cursor.getString(1)//account
//			cursor.getString(3)//content
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("name", cursor.getString(1));
			map.put("content", cursor.getString(3));
			map.put("type", cursor.getString(6));
			data.add(map);
			
		}
		
		HomeAdapter adapter = new HomeAdapter(getActivity(), getActivity(), data);
		home_List = (ListView)view.findViewById(R.id.home_List);
		
		home_List.setAdapter(adapter);
	
	      return view;
	  } 
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 1:

				break;
			case 2:
				
				int p2 = (Integer)msg.obj;

				break;
			
			}
			super.handleMessage(msg);
		}
		
	};
	
	public void addTestDataToDb(){
		ChatRecord chatRecord = new ChatRecord();
		
		chatRecord.setAccount("破题回复");
		chatRecord.setContent("how are you 翻译");
		chatRecord.setFlag("in");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("2");//破题消息
		
		db.insertRecord(chatRecord);
		
		chatRecord.setAccount("校园故事");
		chatRecord.setContent("川大望江基础教学楼B座420。。。");
		chatRecord.setFlag("in");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("1");//通知消息
		
		db.insertRecord(chatRecord);
		
		
		chatRecord.setAccount("破题高手");
		chatRecord.setContent("x+3y+z=139.。。");
		chatRecord.setFlag("in");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("2");//破题消息
		
		db.insertRecord(chatRecord);
		
		
		chatRecord.setAccount("好友动态：jalsary");
		chatRecord.setContent("jalsary：今天打麻将输惨了。。");
		chatRecord.setFlag("in");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("3");//动态消息
		
		db.insertRecord(chatRecord);
		
		chatRecord.setAccount("好友动态：flyln");
		chatRecord.setContent("上传了20张图片至我的生活");
		chatRecord.setFlag("in");
		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
		chatRecord.setType("3");//动态消息
		
		db.insertRecord(chatRecord);
		
		
		
		
	}
	public void onDestroy() {
		super.onDestroy();
		cursor.close();
		db.close();
	};

}
