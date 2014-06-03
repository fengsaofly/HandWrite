package scu.android.util;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import scu.android.application.MyApplication;


public class XmppTool {

	private static XMPPConnection con = null;
	
	private static void openConnection() {
		try {
//			MyApplication.hostIp;
			System.out.println("tool.ip: "+MyApplication.hostIp);
			
			ConnectionConfiguration connConfig = new ConnectionConfiguration(MyApplication.hostIp, 5222);
			con = new XMPPConnection(connConfig);
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
