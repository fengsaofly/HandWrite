package scu.android.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Reply implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2468416367228166111L;

	private long repId;// 回复Id

	private String content;
	private String audio;
	private ArrayList<String> images;
	private Date replyTime;

	private ArrayList<Reply> replys;
	private long quesId;
	private long userId;

	private int type;

	public Reply(long repId, String content, String audio,
			ArrayList<String> images, Date replyTime, long quesId, long userId,
			int type) {
		super();
		this.repId = repId;
		this.content = content;
		this.audio = audio;
		this.images = images;
		this.replyTime = replyTime;
		this.quesId = quesId;
		this.userId = userId;
		this.type = type;
	}

	public long getRepId() {
		return repId;
	}

	public void setRepId(long repId) {
		this.repId = repId;
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

	public Date getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(Date replyTime) {
		this.replyTime = replyTime;
	}

	public long getQuesId() {
		return quesId;
	}

	public void setQuesId(long quesId) {
		this.quesId = quesId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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



}
