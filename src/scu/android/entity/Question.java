package scu.android.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Question implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2752572348702656784L;

	private long qId;// 问题ID
	private String qTitle;// 标题
	private long qUser;// 发布问题的用户ID
	private String qTextContent;// 问题内容
	private long qResource;
	private Date createdTime;// 发布问题时间
	private int qState;// 问题状态（是否解决）
	private String qGrade;// 所属年级
	private String qSubject;// 所属科目

	private ArrayList<Resource> resources;

	
	public Question(long qId, String qTitle, long qUser, String qTextContent,
			long qResource, Date createdTime, int qState, String qGrade,
			String qSubject) {
		super();
		this.qId = qId;
		this.qTitle = qTitle;
		this.qUser = qUser;
		this.qTextContent = qTextContent;
		this.qResource = qResource;
		this.createdTime = createdTime;
		this.qState = qState;
		this.qGrade = qGrade;
		this.qSubject = qSubject;
	}

	public long getqId() {
		return qId;
	}

	public void setqId(long qId) {
		this.qId = qId;
	}

	public String getqTitle() {
		return qTitle;
	}

	public void setqTitle(String qTitle) {
		this.qTitle = qTitle;
	}

	public long getqUser() {
		return qUser;
	}

	public void setqUser(long qUser) {
		this.qUser = qUser;
	}

	public String getqTextContent() {
		return qTextContent;
	}

	public void setqTextContent(String qTextContent) {
		this.qTextContent = qTextContent;
	}

	public long getqResource() {
		return qResource;
	}

	public void setqResource(long qResource) {
		this.qResource = qResource;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public int getqState() {
		return qState;
	}

	public void setqState(int qState) {
		this.qState = qState;
	}

	public String getqGrade() {
		return qGrade;
	}

	public void setqGrade(String qGrade) {
		this.qGrade = qGrade;
	}

	public String getqSubject() {
		return qSubject;
	}

	public void setqSubject(String qSubject) {
		this.qSubject = qSubject;
	}

	public ArrayList<Resource> getResouces() {
		return resources;
	}

	public void setResouces(ArrayList<Resource> resources) {
		this.resources = resources;
	}

	public String toString() {
		return qId + "," + qTitle + "," + qUser + "," + qTextContent + ","
				+ qResource + "," + createdTime.toString() + "," + qState + ","
				+ qSubject;
	}

}
