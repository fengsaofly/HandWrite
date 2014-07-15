package scu.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.OfflineMessageManager;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class GetOffLineMessageThread extends AsyncTask<Void, Integer, Void>{
	
	private Context mContext;
	private Activity myActivity;
		 

	

	
	public GetOffLineMessageThread(Context context,Activity activity){
		this.mContext = context;
		this.myActivity = activity;	
	
		
		
	
		
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
//    	System.out.println("切换为离线登陆状态。。。");
    	Presence presence = new Presence(Presence.Type.unavailable);
		XmppTool.getConnection().sendPacket(presence);
    	OfflineMessageManager offlineManager = new OfflineMessageManager(XmppTool.getConnection());  
	    try {  
	        Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager  
	                .getMessages();  

//	        System.out.println(offlineManager.supportsFlexibleRetrieval());  
//	        System.out.println("离线消息数量: " + offlineManager.getMessageCount());  

	          
	        Map<String,ArrayList<org.jivesoftware.smack.packet.Message>> offlineMsgs = new HashMap<String,ArrayList<org.jivesoftware.smack.packet.Message>>();  
	          
	        while (it.hasNext()) {  
	        	
	          org.jivesoftware.smack.packet.Message message = it.next();  
	          System.out.println("所有离线消息： "+message.toXML());
	            System.out  
	                    .println("收到离线消息, Received from 【" + message.getFrom()  
	                            + "】 message: " + message.getBody());  
	            String fromUser = message.getFrom().split("/")[0];  

	            if(offlineMsgs.containsKey(fromUser))  
	            {  
	                offlineMsgs.get(fromUser).add(message);  
	            }else{  
	                ArrayList<org.jivesoftware.smack.packet.Message> temp = new ArrayList<org.jivesoftware.smack.packet.Message>();  
	                temp.add(message);  
	                offlineMsgs.put(fromUser, temp);  
	            }  
	        }  

	        //在这里进行处理离线消息集合......  
	        Set<String> keys = offlineMsgs.keySet();  
	        Iterator<String> offIt = keys.iterator();  
	        while(offIt.hasNext())  
	        {  
	            String key = offIt.next();  
	            ArrayList<org.jivesoftware.smack.packet.Message> ms = offlineMsgs.get(key);  
//	            TelFrame tel = new TelFrame(key);  
//	            ChatFrameThread cft = new ChatFrameThread(key, null);  
//	            cft.setTel(tel);  
//	            cft.start();  
//	            for (int i = 0; i < ms.size(); i++) {  
//	                tel.messageReceiveHandler(ms.get(i));  
//	            }  
	            
	            for(org.jivesoftware.smack.packet.Message msg : ms){
	            	System.out.println("消息：   "+msg.toString());
	            }
	        } 
	        
	  
	          
	          
	        offlineManager.deleteMessages();  
	        
	        System.out.println("获取到了离线消息。。。");
			
	  					Presence presence2 = new Presence(Presence.Type.available);
	  					XmppTool.getConnection().sendPacket(presence2);
	  					
	  					System.out.println("切换为正常登陆状态。。。");
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    } 
	
    	
    	
    	
        return null;
    }
}
