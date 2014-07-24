package scu.android.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;

import scu.android.application.MyApplication;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;

import com.demo.note.R;

public class GetAllContactVcardThread extends AsyncTask<Void, Integer, Void>{
	
	private Context mContext;
	private Activity myActivity;
	
	private List<RosterEntry> entries;
		
	private Handler handler;

	

	
	public GetAllContactVcardThread(Context context,Activity activity,List<RosterEntry> entries,Handler handler){
		this.mContext = context;
		this.myActivity = activity;	
		this.entries = entries;
		this.handler = handler;
	
		
		
	
		
	}
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
 
    }
    @Override
    protected void onPostExecute(Void result) {//通知handler下载完成
        // TODO Auto-generated method stub
        super.onPostExecute(result);

    }
    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        /*
         * Perform download and Bitmap conversion here
         *
         */
    	ByteArrayInputStream bais = null;
		ArrayList<Map<String,Object>> allContactsVcard = new ArrayList<Map<String,Object>>();
		System.out.println("输出所有好友vcard：\n");
		for(RosterEntry item : entries){
			Map<String,Object> map = new HashMap<String, Object>();
//			RosterEntry item = entries.get(0);
			if(item.getName()!=null&&(!item.getName().equals(""))&&(!item.getName().equals("null"))){
				try {
					VCard vcard = new VCard();
					// 加入这句代码，解决No VCard for
					ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",
							new org.jivesoftware.smackx.provider.VCardProvider());

					vcard.load(XmppTool.getConnection(), item.getName() + "@" + ((MyApplication)mContext.getApplicationContext()).hostName);
//					vcard = ((MyApplication)mContext.getApplicationContext()).getUserVcard(XmppTool.getConnection(),item.getName());

//					if (vcard == null || vcard.getAvatar() == null)
//						map.put("friend_avatar", mContext.getResources().getDrawable(R.drawable.default_avatar));
//					else{
//					bais = new ByteArrayInputStream(vcard.getAvatar());
//						map.put("friend_avatar",((MyApplication)mContext.getApplicationContext()).bitmap2Drawable(BitmapFactory.decodeStream(bais)) ); 
//					}
//					map.put("friend_avatar", mContext.getResources().getDrawable(R.drawable.default_avatar));
					
					 bais = ((MyApplication)mContext.getApplicationContext()).getUserImage(XmppTool.getConnection(), item.getName());
					 if(bais!=null){
						 map.put("friend_avatar",((MyApplication)mContext.getApplicationContext()).bitmap2Drawable(BitmapFactory.decodeStream(bais)) );
					 }
					 else map.put("friend_avatar", mContext.getResources().getDrawable(R.drawable.default_avatar));
					map.put("friend_name", item.getName());
//					System.out.println("item.getName():  "+item.getName());
					if(vcard.getNickName()==null||vcard.getNickName().equals("")){
						map.put("friend_nickName", "");
					}
					else map.put("friend_nickName", vcard.getNickName());
//					System.out.println("vcard.getNickName():  "+vcard.getNickName());
					if(vcard.getMiddleName()==null||vcard.getMiddleName().equals("")){
						map.put("friend_carrer", "");
					}
					else map.put("friend_carrer", vcard.getMiddleName());
//					System.out.println(" vcard.getMiddleName()"+ vcard.getMiddleName());
					if(vcard.getFirstName()==null||vcard.getFirstName().equals("")){
						map.put("friend_gender", "");
					}
					else map.put("friend_gender", vcard.getFirstName());
//					System.out.println("vcard.getFirstName():"+vcard.getFirstName());
					if(vcard.getAddressFieldHome("zone")==null||vcard.getAddressFieldHome("zone").equals("")){
						map.put("friend_zone", "");
					}
					else map.put("friend_zone", vcard.getAddressFieldHome("zone"));
//					System.out.println("vcard.getAddressFieldHome():"+vcard.getAddressFieldHome("zone"));
					if(vcard.getLastName()==null||vcard.getLastName().equals("")){
						map.put("friend_sign", "");
					}
					else map.put("friend_sign",vcard.getLastName());
//					System.out.println("vcard.getLastName():"+vcard.getLastName());
//					map.put("friend_nickName", vcard.getNickName());
//					map.put("friend_carrer", vcard.getMiddleName());
//					map.put("friend_gender", vcard.getFirstName());
//					map.put("friend_zone", vcard.getAddressFieldHome("zone"));
//					map.put("friend_sign", vcard.getLastName());
					
					allContactsVcard.add(map);
	

				} catch (Exception e) {
					System.out.println("进入超时错误。。。");
					allContactsVcard = new ArrayList<Map<String,Object>>();
					for(RosterEntry entry:entries){
						Map<String,Object> map2 = new HashMap<String, Object>();
						map2.put("friend_avatar", mContext.getResources().getDrawable(R.drawable.default_avatar));
						map2.put("friend_name", entry.getName());
						map2.put("friend_nickName", entry.getName());
						map2.put("friend_carrer", "");
						map2.put("friend_gender", "");
						map2.put("friend_zone", "");
						map2.put("friend_sign", "");
						allContactsVcard.add(map2);
					}
					e.printStackTrace();
				}
			}
		}
    	
	
		((MyApplication)mContext.getApplicationContext()).setAllContactsVcard(allContactsVcard);
    	
    	
        return null;
    }
}

