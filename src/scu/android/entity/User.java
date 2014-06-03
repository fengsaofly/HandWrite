package scu.android.entity;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6627513002705036525L;

	private long userId;// 用户ID
	private String userName;// 用户名
	private String password;
	private String email;// 验证邮箱
	private int phone;
	private int type;// 类型
	private String nickname;// 昵称
	private String avatar;// 头像
	private String school;
	private String grade;// 年级
	private char sex;//
	private int age;
	private double curLon;
	private double curLat;//

	public User(long userId, String userName, String password, String email,
			int phone, int type, String nickname, String avatar, String school,
			String grade, char sex, int age, double curLon, double curLat) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.phone = phone;
		this.type = type;
		this.nickname = nickname;
		this.avatar = avatar;
		this.school = school;
		this.grade = grade;
		this.sex = sex;
		this.age = age;
		this.curLon = curLon;
		this.curLat = curLat;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getCurLon() {
		return curLon;
	}

	public void setCurLon(double curLon) {
		this.curLon = curLon;
	}

	public double getCurLat() {
		return curLat;
	}

	public void setCurLat(double curLat) {
		this.curLat = curLat;
	}

}
