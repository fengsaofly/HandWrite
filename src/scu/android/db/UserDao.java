package scu.android.db;

import java.util.LinkedList;
import scu.android.entity.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDao {

	public static long insertUser(Context context, User user) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getWritableDatabase();
		aDatabase.beginTransaction();
		long userId = 0;
		try {
			ContentValues values = new ContentValues();
			values.put("userName", user.getUserName());
			values.put("password", user.getPassword());
			values.put("email", user.getEmail());
			values.put("phone", user.getPhone());
			values.put("type", user.getType());
			values.put("nickName", user.getNickname());
			values.put("avatar", user.getAvatar());
			values.put("school", user.getSchool());
			values.put("grade", user.getGrade());
			values.put("sex", String.valueOf(user.getSex()));
			values.put("age", user.getAge());
			values.put("curLon", user.getCurLon());
			values.put("curLat", user.getCurLat());
			userId = aDatabase.insert(DBHelper.TABLE_USER, null, values);
			aDatabase.setTransactionSuccessful();
			values.clear();
		} finally {
			aDatabase.endTransaction();
		}
		aDatabase.close();
		Log.i("UserDao", "INSERT_USER|userId=" + String.valueOf(userId));
		return userId;
	}

	public static LinkedList<User> getUsers(Context context) {

		LinkedList<User> users = new LinkedList<User>();
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_USER, null, null, null, null, null,
				null);
		while (cursor.moveToNext()) {
			users.add(getUser(cursor));
		}
		cursor.close();
		aDatabase.close();
		Log.i("UserDao", "GET_USERS|size=" + users.size());
		return users;
	}

	public static User getUserById(Context context, int userId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_USER, null, "userId=?",
				new String[] { String.valueOf(userId) }, null, null, null);
		User user = null;
		if (cursor.moveToNext()) {
			user = getUser(cursor);
			cursor.close();
			aDatabase.close();
		}
		return user;
	}

	public static User getUser(Cursor cursor) {
		long userId = cursor.getLong(cursor.getColumnIndex("userId"));
		String userName = cursor.getString(cursor.getColumnIndex("userName"));
		String password = cursor.getString(cursor.getColumnIndex("password"));
		String email = cursor.getString(cursor.getColumnIndex("password"));
		int phone = cursor.getInt(cursor.getColumnIndex("phone"));
		int type = cursor.getInt(cursor.getColumnIndex("type"));
		String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
		String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
		String school = cursor.getString(cursor.getColumnIndex("school"));
		String grade = cursor.getString(cursor.getColumnIndex("grade"));
		char sex = cursor.getString(cursor.getColumnIndex("sex")).toCharArray()[0];
		int age = cursor.getInt(cursor.getColumnIndex("age"));
		double curLon = cursor.getDouble(cursor.getColumnIndex("curLon"));
		double curLat = cursor.getDouble(cursor.getColumnIndex("curLat"));
		return new User(userId, userName, password, email, phone, type,
				nickname, avatar, school, grade, sex, age, curLon, curLat);
	}
}
