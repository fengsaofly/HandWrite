package scu.android.ui;

import java.io.Serializable;

public class RoomInformation implements Serializable  {
	
//	TextView group_name_val = null,tag_group_maxmember_val = null,group_type_val,tag_group_notice_val,exit = null;
	
	int existPosition = -1;
	String roomType = "";
	String roomCount = "";
	String group_name;
	String group_maxmember;
	String owenr;
	public String getOwenr() {
		return owenr;
	}



	public void setOwenr(String owenr) {
		this.owenr = owenr;
	}

	String tag_group_notice;
	

	public RoomInformation(){
	
	}



	public int getExistPosition() {
		return existPosition;
	}

	public void setExistPosition(int existPosition) {
		this.existPosition = existPosition;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getRoomCount() {
		return roomCount;
	}

	public void setRoomCount(String roomCount) {
		this.roomCount = roomCount;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String getGroup_maxmember() {
		return group_maxmember;
	}

	public void setGroup_maxmember(String group_maxmember) {
		this.group_maxmember = group_maxmember;
	}



	public String getTag_group_notice() {
		return tag_group_notice;
	}

	public void setTag_group_notice(String tag_group_notice) {
		this.tag_group_notice = tag_group_notice;
	}


	
	
	
	
}
