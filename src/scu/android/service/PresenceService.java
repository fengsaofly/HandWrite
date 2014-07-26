//package scu.android.service;
//
//import org.jivesoftware.smack.PacketListener;
//import org.jivesoftware.smack.XMPPConnection;
//import org.jivesoftware.smack.filter.AndFilter;
//import org.jivesoftware.smack.filter.PacketFilter;
//import org.jivesoftware.smack.filter.PacketTypeFilter;
//import org.jivesoftware.smack.packet.Packet;
//import org.jivesoftware.smack.packet.Presence;
//
//import scu.android.application.MyApplication;
//import scu.android.db.ChatRecord;
//import scu.android.db.DbManager2;
//import scu.android.util.TimeRender;
//import scu.android.util.XmppTool;
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.util.Log;
//
//public class PresenceService extends Service {  
//	DbManager2 db;
//	  
//    private XMPPConnection cc = XmppTool.getConnection();// 保存了当前的链接 XMPPConnection
//  
//    @Override  
//    public IBinder onBind(Intent arg0) {  
//        return null;  
//    }  
//  
//    @Override  
//    public void onCreate() {  
//    	
//        super.onCreate();  
//    }  
//  
//    @Override  
//    public void onDestroy() {  
//    	if(db!=null)
//    		db.close();
//        super.onDestroy(); 
//    }  
//  
//    @Override  
//    public int onStartCommand(Intent intent, int flags, int startId) { 
//    	System.out.println("服务已启动。。。");
//        Log.i("Presence", "PresenceService-----" + (cc == null));  
//        new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				if (cc != null && cc.isConnected()  
//		                && cc.isAuthenticated()) {//已经认证的情况下，才能正确收到Presence包（也就是登陆）  
//		            final String loginuser = cc.getUser().substring(0, //这里要解释一下，这是要去除系统在登陆用户尾部添加的域名信息，例如  xxx<span id="kM0.021647081011906266">@域名.....</span>
//		                    cc.getUser().lastIndexOf("@"));  
//		            //理解为条件过滤器   过滤出Presence包  
//		            PacketFilter filter = new AndFilter(new PacketTypeFilter(  
//		                    Presence.class));  
//		            PacketListener listener = new PacketListener() {  
//		  
//		                @Override  
//		                public void processPacket(Packet packet) {  
//		                	db = new DbManager2(PresenceService.this);
//		                    Log.i("Presence", "PresenceService------" + packet.toXML());  
//		                    //看API可知道   Presence是Packet的子类  
//		                    if (packet instanceof Presence) {  
//		                        Log.i("Presence", packet.toXML());  
//		                        Presence presence = (Presence) packet;  
//		                        //Presence还有很多方法，可查看API   
//		                        String from = presence.getFrom();//发送方  
//		                        String to = presence.getTo();//接收方  
//		                        System.out.println("发送方： "+from);
//		                        System.out.println("接收方： "+to);
//		                        //Presence.Type有7中状态  
//		                        if (presence.getType().equals(Presence.Type.subscribe)) {//好友申请  
//		                        	if(to.contains(MyApplication.userName)||to.contains(MyApplication.nickName)){
//		                        		String[] args = new String[] { from,
//		    									"subscribe", TimeRender.getDate(),
//		    									
//		    									"IN" };
//		                            	addRecordToDb(args);
//		                        	}
//		                        	
//		                              System.out.println("好友申请");
//		                        } 
//		                        else if (presence.getType().equals(  
//		                                Presence.Type.subscribed)) {//同意添加好友  
//		                        	if(to.contains(MyApplication.userName)||to.contains(MyApplication.nickName)){
//		                        		String[] args = new String[] { from,
//		    									"subscribed", TimeRender.getDate(),
//		    									"IN" };
//		                            	addRecordToDb(args);
//		                        	}
//		                        	System.out.println("同意添加好友  ");
//		                        } 
//		                        else if (presence.getType().equals(  
//		                                Presence.Type.unsubscribe)) {//拒绝添加好友  和  删除好友  
//		                        	if(to.contains(MyApplication.userName)||to.contains(MyApplication.nickName)){
//		                        		String[] args = new String[] { from,
//		    									"unsubscribe", TimeRender.getDate(),
//		    									"IN" };
//		                            	addRecordToDb(args);
//		                        	}
//		                        	System.out.println("拒绝添加好友  和 删除好友 ");
//		                        }
//		                       
//		                        else if (presence.getType().equals(  
//		                                Presence.Type.unavailable)) {//好友下线   要更新好友列表，可以在这收到包后，发广播到指定页面   更新列表  
//		                        	System.out.println("好友申请");
//		                        } 
//		                        else {//好友上线  
//		                              
//		                        }  
//		                    } 
//		                    if(db!=null){
//		                    	db.close();
//		                    }
//		                }  
//		            };  
//		            cc.addPacketListener(listener, filter); //注册监听
//				}
//			}
//		}).start();
//        
//          
//        return super.onStartCommand(intent, flags, startId);  
//    }  
//    
//    
//    public void addRecordToDb(String[] args) {
//		// if(((MyApplication)getActivity().getApplication()).currentActivity.equals("main")){
//		ChatRecord chatRecord = new ChatRecord();
////		String s = args[0].toString().contains("@") ? args[0].toString().split(
////				"@")[0] : args[0].toString();
//				
//		chatRecord.setAccount(args[0]);
//		chatRecord.setContent(args[1]);
//		chatRecord.setFlag("in");
//		chatRecord.setTime(TimeRender.getDate().split(" ")[1]);
//		chatRecord.setDate(TimeRender.getDate().split(" ")[0]);
//		chatRecord.setType("0");
//		chatRecord.setIsGroupChat("false");
//		chatRecord.setJid("-1");
//		chatRecord.setContent_type("nomal");
//		System.out.println("写入数据库成功，内容为： " + args[1]);
//		db.insertRecord(chatRecord);
//		
//		
//
//		// }
//
//	}
//    
//    
//}  
