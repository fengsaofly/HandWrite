package scu.android.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import scu.android.entity.Question;
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

	public static ArrayList<User> getUsers(Context context) {

		ArrayList<User> users = new ArrayList<User>();
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_USER, null, null, null,
				null, null, null);
		while (cursor.moveToNext()) {
			users.add(getUser(cursor));
		}
		cursor.close();
		aDatabase.close();
		Log.i("UserDao", "GET_USERS|size=" + users.size());
		return users;
	}

	public static ArrayList<User> getRecords(Context context, String tblName,
			long start, long lens) {
		ArrayList<User> records = new ArrayList<User>();
		final DBHelper dbHelper = DBHelper.getInstance(context);
		final SQLiteDatabase aDatabase = dbHelper.getReadableDatabase();
		final String orderBy = "createTime desc limit  " + start + "," + lens;
		final Cursor cursor = aDatabase.query(tblName, null, null, null, null,
				null, orderBy);
		while (cursor.moveToNext()) {
			records.add(getUser(cursor));
		}
		cursor.close();
		aDatabase.close();
		return records;
	}

	public static User getUserById(Context context, long userId) {
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
		// String password =
		// cursor.getString(cursor.getColumnIndex("password"));
		String email = cursor.getString(cursor.getColumnIndex("email"));
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
//		String mCreateTime = cursor.getString(cursor
//				.getColumnIndex("createTime"));
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date createTime = null;
//		try {
//			createTime = format.parse(mCreateTime);
//			createTime.setHours(createTime.getHours() + 8);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		return new User(userId, userName, email, phone, type, nickname, avatar,
				school, grade, sex, age, curLon, curLat, new Date());
	}

	public static int getRecordsNum(Context context, String tblName) {
		final DBHelper dbHelper = DBHelper.getInstance(context);
		final SQLiteDatabase aDatabase = dbHelper.getReadableDatabase();
		final String queryString = "select count(userId) from " + tblName;
		final Cursor cursor = aDatabase.rawQuery(queryString, null);
		cursor.moveToFirst();
		final int recordsNum = cursor.getInt(0);
		cursor.close();
		aDatabase.close();
		return recordsNum;
	}
}
