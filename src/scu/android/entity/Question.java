package scu.android.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Question implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2752572348702656784L;

	private long quesId;// 问题ID
	private String title;// 标题
	private String content;// 问题内容
	private String audio;// 问题录音路径
	private ArrayList<String> images;// 问题描述图片
	private Date publishTime;// 发布问题时间
	private boolean status;// 问题状态（是否解决）
	private String grade;// 所属年级
	private String subject;// 所属科目

	private long userId;// 发布问题的用户ID

	public Question(long quesId, String title, String content, String audio,
			ArrayList<String> images, Date publishTime, boolean status,
			String grade, String subject, long userId) {
		super();
		this.quesId = quesId;
		this.title = title;
		this.content = content;
		this.audio = audio;
		this.images = images;
		this.publishTime = publishTime;
		this.status = status;
		this.grade = grade;
		this.subject = subject;
		this.userId = userId;
	}

	public long getQuesId() {
		return quesId;
	}

	public void setQuesId(long quesId) {
		this.quesId = quesId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public ArrayList<String> getImages() {
		return images;
	}

	public void setImages(ArrayList<String> images) {
		this.images = images;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}
