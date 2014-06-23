package scu.android.util;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import scu.android.application.MyApplication;


public class XmppTool {

	private static XMPPConnection con = null;
	
	private static void openConnection() {
		try {
//			MyApplication.hostIp;
			System.out.println("tool.ip: "+MyApplication.hostIp);
			
			ConnectionConfiguration connConfig = new ConnectionConfiguration(MyApplication.hostIp, 5222);
			
			
			
			con = new XMPPConnection(connConfig);
			if(MyApplication.loginFlag==false){
				System.out.println("xmpptool 设置离线状态");
				connConfig.setSendPresence(false);//获取离线消息，先设置为离线在登录
			}
//			else{
//				Presence presence = new Presence(Presence.Type.available);  //设置在线
//				con.sendPacket(presence);
//			}
			
	        
	        
			connConfig.setSASLAuthenticationEnabled(false);
			connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
			con.connect();
		}
		catch (XMPPException xe) 
		{
			xe.printStackTrace();
		}
	}

	public static XMPPConnection getConnection() {
		if (con == null) {
			openConnection();
		}
		return con;
	}

	public static void closeConnection() {
		con.disconnect();
		con = null;
	}
}
