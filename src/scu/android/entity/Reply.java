package scu.android.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Reply implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2468416367228166111L;

	private long rId;// 回复Id
	private String rTextContent;
	private long rResource;
	private Date createdTime;
	private long qId;
	private long rUser;
	private int type;

	private ArrayList<Reply> replys;
	private ArrayList<Resource> resources;


	public Reply(long rId, String rTextContent, long rResource,
			Date createdTime, long qId, long rUser, int type) {
		super();
		this.rId = rId;
		this.rTextContent = rTextContent;
		this.rResource = rResource;
		this.createdTime = createdTime;
		this.qId = qId;
		this.rUser = rUser;
		this.type = type;
	}

	public long getrId() {
		return rId;
	}

	public void setrId(long rId) {
		this.rId = rId;
	}

	public String getrTextContent() {
		return rTextContent;
	}

	public void setrTextContent(String rTextContent) {
		this.rTextContent = rTextContent;
	}

	public long getrResource() {
		return rResource;
	}

	public void setrResource(long rResource) {
		this.rResource = rResource;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public long getqId() {
		return qId;
	}

	public void setqId(long qId) {
		this.qId = qId;
	}

	public long getrUser() {
		return rUser;
	}

	public void setrUser(long rUser) {
		this.rUser = rUser;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<Reply> getReplys() {
		return replys;
	}

	public void setReplys(ArrayList<Reply> replys) {
		this.replys = replys;
	}

	public ArrayList<Resource> getResources() {
		return resources;
	}

	public void setResources(ArrayList<Resource> resources) {
		this.resources = resources;
	}

}
