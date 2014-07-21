package scu.android.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.RosterEntry;

import scu.android.application.MyApplication;
import scu.android.ui.ChatMainActivity;
import scu.android.ui.MultiRoomListActivity;
import scu.android.ui.NewFriendsActivity;
import scu.android.util.PinyinComparator;
import scu.android.util.SideBar;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

@SuppressLint("NewApi")
public class FriendFragment extends Fragment

{

	ListView friendListView = null;
	// ListView multiListView = null;
//	public List<RosterEntry> entries;
	private  ArrayList<Map<String, Object>> friendData = null;
	// List<Map<String,Object>> multiData = null;
	ContactAdapter friendAdapter = null;
	Chat newchat;
	// SimpleAdapter multiAdapter = null;
//	private ProgressDialog pd;
//	LinearLayout multiChat = null;
	
	private SideBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
	private boolean createFlag = false;
	// List<DiscoverItems.Item> items = new ArrayList<DiscoverItems.Item>();
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			
			case 10:
//				pd.dismiss();
//				if (friendAdapter == null) {
					// multiAdapter = new SimpleAdapter(getActivity(),
					// multiData, R.layout.friend_item, new
					// String[]{"friend_icon","friend_name"},new
					// int[]{R.id.friend_icon,R.id.friend_name} );
					// multiListView.setAdapter(multiAdapter);

					friendAdapter = new ContactAdapter(getActivity(),friendData);
//							new String[] {
//									"friend_icon", "friend_name" }, new int[] {
//									R.id.friend_icon, R.id.friend_name });
					friendListView.setAdapter(friendAdapter);
//				} else {
////					// multiAdapter.notifyDataSetChanged();
////					// multiListView.invalidate();
//					friendAdapter.notifyDataSetChanged();
//					friendListView.invalidate();
//				}

				break;
			}
		}
	};

	public static FriendFragment newInstance() {
		FriendFragment detail = new FriendFragment();

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
		System.out.println("进入Friend----oncreateView");
//		System.out.println("oncreateView:  ");
		 
//		pd = new ProgressDialog(getActivity());
//		pd.setTitle("提示");
//		pd.setMessage("正在更新列表");
//		pd.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(((MyApplication)getActivity().getApplication()).loginFlag){
					
				while(((MyApplication)getActivity().getApplication()).getAllContactsVcard()==null||((MyApplication)getActivity().getApplication()).getAllContactsVcard().size()==0){
					try {
						System.out.println("正在等待好友数据。。。");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}
			
			}
		}).start();
		View view = inflater.inflate(R.layout.fragment_friends, container,
				false);
		friendListView = (ListView) view.findViewById(R.id.friendListView);
		
		mWindowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
		 
		 indexBar = (SideBar)view.findViewById(R.id.sideBar);  
	       
		
		// multiListView = (ListView)view.findViewById(R.id.multiListView);

//		entries = ((MyApplication) getActivity().getApplication()).entries;
//		multiChat = (LinearLayout)view.findViewById(R.id.multiChat);
		if(((MyApplication)getActivity().getApplication()).loginFlag==false){
//			multiChat.setVisibility(View.INVISIBLE);
			indexBar.setVisibility(View.INVISIBLE);
		}
		else {
//			multiChat.setVisibility(View.VISIBLE);
			indexBar.setVisibility(View.VISIBLE);
			 indexBar.setListView(friendListView); 
		        mDialogText = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_position, null);
		        mDialogText.setVisibility(View.INVISIBLE);
		        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
		                WindowManager.LayoutParams.TYPE_APPLICATION,
		                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
		                PixelFormat.TRANSLUCENT);
		        mWindowManager.addView(mDialogText, lp);
		        indexBar.setTextView(mDialogText);
		}
//		multiChat.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(getActivity(),
//						MultiRoomListActivity.class);
//
//				startActivity(intent);
//			}
//		});


		friendData = new ArrayList<Map<String, Object>>();
		// multiData = new ArrayList<Map<String,Object>>();

		if(((MyApplication)getActivity().getApplication()).loginFlag){
			new Thread(new Runnable() {
				@Override
				public void run() {
					/**
					 * 添加聊天室列表
					 */

					// addMultiRoom();

					/**
					 * 添加好友列表
					 */
					addChatList();
					createFlag = true;
					Message msg = new Message();
					msg.what = 10;
					handler.sendMessage(msg);
				}
			}).start();
		}

		friendListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if(position==0){
					Intent intent = new Intent(getActivity(),
							MultiRoomListActivity.class);
	
					startActivity(intent);
				}
				else if(position==1) {
					Intent intent = new Intent(getActivity(),
							NewFriendsActivity.class);

					startActivity(intent);
				}
				else  {
					Intent intent = new Intent();
					intent.setClass(getActivity(), ChatMainActivity.class);
					intent.putExtra("currentContact", friendData.get(position-2)
							.get("friend_name").toString());
					startActivity(intent);
				}
			
			}
		});

		friendListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int position, long arg3) {
						// TODO Auto-generated method stub
						final int p = position;
					
						
						if (position != 0&&position!=1) {
							new AlertDialog.Builder(getActivity())
									.setMessage(
											"确认删除好友"
													+ friendData.get(position-2)
															.get("friend_name")
															.toString() + "吗")
									.setTitle("系统提示")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													// TODO Auto-generated
													// method stub
													if (((MyApplication) getActivity()
															.getApplication())
															.removeUser(friendData
																	.get(p-2)
																	.get("friend_name")
																	.toString())) {
														Toast.makeText(
																getActivity(),
																"删除成功",
																Toast.LENGTH_SHORT)
																.show();
														friendData.remove(p-2);
//														friendAdapter
//																.notifyDataSetChanged();
//														friendListView
//																.invalidate();
														friendData = (ArrayList<Map<String, Object>>) ((MyApplication)getActivity().getApplication()).getAllContactsVcard().clone();
														friendAdapter = new ContactAdapter(getActivity(),friendData
																);
//		
														friendListView.setAdapter(friendAdapter);
													}
												}
											})
									.setNegativeButton(
											"取消",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													// TODO Auto-generated
													// method stub

												}
											}).create().show();
						}
						return false;
					}
				});

	
		return view;

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// getActivity().unregisterReceiver( myBroadcastReciver);
		// db.close();
	}

	@Override
	public void onResume() {
		if(((MyApplication)getActivity().getApplication()).loginFlag){
			System.out.println("进入Friend----onresume");
			System.out.println("before: friendData。size()"+friendData.size());
			new Thread(new Runnable() {
				@Override
				public void run() {
					/**
					 * 添加聊天室列表
					 */

					// addMultiRoom();

					/**
					 * 添加好友列表
					 */
					System.out.println("进入Friend----onresume");
//					while(((MyApplication)getActivity().getApplication()).allContactsVcard==null||((MyApplication)getActivity().getApplication()).allContactsVcard.size()==0){
//						try {
//							System.out.println("正在等待好友数据。。。");
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					handler.sendEmptyMessage(9);
//					friendData =null;
					
					addChatList();
//					friendData = new ArrayList<Map<String,Object>>();
//					friendData = ((MyApplication)getActivity().getApplication()).getAllContactsVcard();
					
					Message msg = new Message();
					msg.what = 10;
					handler.sendMessage(msg);
				}
			}).start();
			
			System.out.println("afater: friendData。size()"+friendData.size());
		}
//		
		
		super.onResume();
	}
	




	public  void addChatList() {
//		Map<String, Object> map2 = new HashMap<String, Object>();
//		map2.put("friend_icon", R.drawable.group_icon);
//		map2.put("friend_name", "群聊");
//		friendData.add(map2);
//		for (int i = 0; i < entries.size(); i++) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("friend_icon", R.drawable.actionbar_icon);
//			map.put("friend_name", entries.get(i).getName());
//
//			System.out.println("entries.get(i).getName(): "
//					+ entries.get(i).getName());
//			if (!(entries.get(i).getName() == null || entries.get(i).getName()
//					.equals("")))
//				friendData.add(map);
//		}
//		if(((MyApplication)getActivity().getApplication()).allContactsVcard!=null&&((MyApplication)getActivity().getApplication()).allContactsVcard.size()!=0)
		
//		if(((MyApplication)getActivity().getApplication()).getAllContactsVcard()==null||((MyApplication)getActivity().getApplication()).getAllContactsVcard().size()==0){
//			friendData = new ArrayList<Map<String,Object>>();
//			for(RosterEntry entry:((MyApplication)getActivity().getApplication()).entries){
//				Map<String,Object> map2 = new HashMap<String, Object>();
//				map2.put("friend_avatar", getActivity().getResources().getDrawable(R.drawable.default_avatar));
//				map2.put("friend_name", entry.getName());
//				map2.put("friend_nickName", entry.getName());
//				map2.put("friend_carrer", "");
//				map2.put("friend_gender", "");
//				map2.put("friend_zone", "");
//				map2.put("friend_sign", "");
//				friendData.add(map2);
//			}
//		}
//		else{
			System.out.println("getAllContactsVcard不为空");
//			friendData = null;
			friendData = new ArrayList<Map<String,Object>>();
			friendData = (ArrayList<Map<String, Object>>) ((MyApplication)getActivity().getApplication()).getAllContactsVcard().clone();
//		}
	}
	
	
	  static class ContactAdapter extends BaseAdapter implements SectionIndexer {  
	    	private Context mContext;
	    	private String[] mNicks;
	    	List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
//	    	private List<Map<String, Object>> data;
	    	private int size = 0;
	    	@SuppressWarnings("unchecked")
			public ContactAdapter(Context mContext,ArrayList<Map<String,Object>> datasrc){
	    		
	    		this.mContext = mContext;
//	    		for(Map<String,Object> map :datasrc){
//	    			this.data.add(map);
//	    		}
	    		this.data = (List<Map<String, Object>>) datasrc.clone();
//	    		this.data = data;
	    		size = data.size();
	    		System.out.println("现在的大小是： "+size);
	    		this.mNicks = new String[size];
	    		for(int i=0;i<size;i++){
	    			this.mNicks[i]
	    					= data
	    					.get(i).
	    					get("friend_name").toString();
	    		}
	    		//排序(实现了中英文混排)
	    		Arrays.sort(mNicks, new PinyinComparator());
	    		System.out.println("排序前：  "+"---------");
	    		for(Map<String, Object> item:data){
	    			System.out.println("item: "+item.get("friend_name"));
	    		}
	    		
	    		
	    		System.out.println("排序后： ");
	    		
	    		for(String nick:mNicks){
	    			System.out.println("item: "+nick);
	    		}
	    		
	    		for(int i=0;i<mNicks.length;i++){
	    			for(int j=0;j<data.size();j++){
	    				if(mNicks[i].equals(data.get(j).get("friend_name"))){
	    					Map<String,Object> temp = data.get(j);
	    					data.set(j,data.get(i));
	    					data.set(i,temp);
	    					
	    				}
	    			}
	    		}
	    		
	    		System.out.println("交换后： ");
	    		
	    		for(Map<String, Object> item:data){
	    			System.out.println("item: "+item.get("friend_name"));
	    		}
	    		
	    		//添加群聊和新的朋友
	    		Map<String,Object> temp = new HashMap<String, Object>();
	    		temp.put("friend_avatar", mContext.getResources().getDrawable((R.drawable.default_avatar)));
	    		temp.put("friend_name", "新的朋友");
	    		temp.put("friend_sign", "");
	    		
	    		Map<String,Object> temp1 = new HashMap<String, Object>();
	    		temp1.put("friend_avatar", mContext.getResources().getDrawable((R.drawable.group_icon)));
	    		temp1.put("friend_name", "群聊");
	    		temp1.put("friend_sign", "");
	    		
	    		data.add(0, temp);
	    		data.add(0, temp1);
	    		
	    		System.out.println("交换后data的size： "+data.size());
	    		
	    		String[] tempMNicks = new String[(mNicks.length)];
	    		for(int i=0;i<tempMNicks.length;i++){
	    			tempMNicks[i] = mNicks[i];
	    		}
	    		mNicks = new String[tempMNicks.length+2];
	    		mNicks[0] = "群聊";
    			mNicks[1] = "新的朋友";
	    		for(int i=0;i<tempMNicks.length;i++){
	    			
	    			mNicks[i+2] = tempMNicks[i];
	    		}
	    		
	    		System.out.println("交换后mNick的size： "+mNicks.length);
	    		
	    	}
			@Override
			public int getCount() {
				return mNicks.length;
			}

			@Override
			public Object getItem(int position) {
				return mNicks[position];
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final String nickName = mNicks[position];
				ViewHolder viewHolder = new ViewHolder();
//				if(convertView == null){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.friend_item, null);
					viewHolder = new ViewHolder();
					viewHolder.tvCatalog = (TextView)convertView.findViewById(R.id.contactitem_catalog);
					viewHolder.ivAvatar = (ImageView)convertView.findViewById(R.id.friend_icon);
					viewHolder.tvNick = (TextView)convertView.findViewById(R.id.friend_name);
					viewHolder.sign = (TextView)convertView.findViewById(R.id.distance_textview);
					
					convertView.setTag(viewHolder);
//				}else{
//					viewHolder = (ViewHolder)convertView.getTag();
//				}
				String catalog = converterToFirstSpell(nickName).substring(0, 1);
				if(position == 0||position == 1){
					viewHolder.tvCatalog.setVisibility(View.INVISIBLE);
					viewHolder.tvCatalog.setText(catalog);
				}
				else if(position==2){
					viewHolder.tvCatalog.setVisibility(View.VISIBLE);
					viewHolder.tvCatalog.setText(catalog);
				}
				else{
					String lastCatalog = converterToFirstSpell(mNicks[position-1]).substring(0, 1);
					if(catalog.equals(lastCatalog)){
						viewHolder.tvCatalog.setVisibility(View.GONE);
					}else{
						viewHolder.tvCatalog.setVisibility(View.VISIBLE);
						viewHolder.tvCatalog.setText(catalog);
					}
				}
				
				
				viewHolder.tvNick.setText(nickName);
				System.out.println("position: "+position);
				viewHolder.ivAvatar.setImageDrawable((Drawable)data.get(position).get("friend_avatar"));
				viewHolder.sign.setText(data.get(position).get("friend_sign").toString());
				return convertView;
			}
	    	
			static class ViewHolder{
				TextView tvCatalog;//目录
				ImageView ivAvatar;//头像
				TextView tvNick;//昵称
				TextView sign;
			}
	 
			@Override
			public int getPositionForSection(int section) {
				for (int i = 0; i < mNicks.length; i++) {  
		            String l = converterToFirstSpell(mNicks[i]).substring(0, 1);  
		            char firstChar = l.toUpperCase().charAt(0);  
		            if (firstChar == section) {  
		                return i;  
		            }  
		        } 
				return -1;
			}
			@Override
			public int getSectionForPosition(int position) {
				return 0;
			}
			@Override
			public Object[] getSections() {
				return null;
			}
	    }
	    
	    /**
	     * 昵称
	     */
//	    private static String[] nicks = {"阿雅","北风","张山","李四","欧阳锋","郭靖","黄蓉","杨过","凤姐","芙蓉姐姐","移联网","樱木花道","风清扬","张三丰","梅超风"};
	    /**  
	     * 汉字转换位汉语拼音首字母，英文字符不变  
	     * @param chines 汉字  
	     * @return 拼音  
	     */     
	    public static String converterToFirstSpell(String chines){             
	         String pinyinName = "";      
	        char[] nameChar = chines.toCharArray();      
	         HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();      
	         defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);      
	         defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);      
	        for (int i = 0; i < nameChar.length; i++) {      
	            if (nameChar[i] > 128) {      
	                try {      
	                     pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);      
	                 } catch (BadHanyuPinyinOutputFormatCombination e) {      
	                     e.printStackTrace();      
	                 }      
	             }else{      
	                 pinyinName += nameChar[i];      
	             }      
	         }      
	        return pinyinName;      
	     }  

}
