/**
 * Description:
 * Accident.java Create on 2012-10-8 ����11:37:52 
 * @author QJK
 * @version 1.0
 * Copyright (c) 2012 Company,Inc. All Rights Reserved.
 */
package scu.android.db; 

import java.io.Serializable;



/**
 * @author QUE
 *
 */
public class ChatRecord implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String account;
	private String time;
	private String content;
	private String flag; //判断是自己发的消息还是获取的消息
	private String date;
	private String type;   //数据类型   0为消息数据，1为通知数据，2为破题数据，3为动态数据
	private String isGroupChat;  //是否是分组聊天（群聊），如果是则取jid判断是哪个房间的
	private String jid;
	private String content_type; //文本类型，nomal:普通文本，unnomal富文本
	
	
	
	

	
	
	public String getContent_type() {
		return content_type;
	}


	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}


	public String getJid() {
		return jid;
	}


	public void setJid(String jid) {
		this.jid = jid;
	}


	public String getIsGroupChat() {
		return isGroupChat;
	}


	public void setIsGroupChat(String isGroupChat) {
		this.isGroupChat = isGroupChat;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public ChatRecord(){
		
	}


	public ChatRecord(int id,String account, String time,String content,String flag,String date,String type){
		this.id = id;
		this.account = account;
		this.time = time;
		this.content=content;
		this.flag = flag;
		this.date = date;
		this.type = type;
		
	
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getAccount() {
		return account;
	}



	public void setAccount(String account) {
		this.account = account;
	}



	public String getTime() {
		return time;
	}



	public void setTime(String time) {
		this.time = time;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}


	public String getFlag() {
		return flag;
	}


	public void setFlag(String flag) {
		this.flag = flag;
	}



	



	
	
	
}

