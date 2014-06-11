package scu.android.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import scu.android.entity.Reply;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

// 插入问题
@SuppressLint("SimpleDateFormat")
public class ReplyDao {

	public static long insertReply(Context context, Reply reply) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getWritableDatabase();
		aDatabase.beginTransaction();
		long repId = 0;
		try {
			ContentValues values = new ContentValues();
			values.put("content", reply.getContent());
			values.put("audio", reply.getAudio());
			values.put("quesId", reply.getQuesId());
			values.put("userId", reply.getUserId());
			repId = aDatabase.insert(DBHelper.TABLE_REPLY, null, values);
			aDatabase.setTransactionSuccessful();
			values.clear();
		} finally {
			aDatabase.endTransaction();
		}
		aDatabase.close();
		if (repId != 0)
			ImagesDao.insertImages(context, reply.getImages(), repId, 3);
		Log.i("ReplyDao", "INSERT_REPLY|repId=" + String.valueOf(repId));
		return repId;
	}

	public static ArrayList<Reply> getReply(Context context, long quesId) {
		ArrayList<Reply> replys = new ArrayList<Reply>();
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_REPLY, null, "quesId=?",
				new String[] { String.valueOf(quesId) }, null, null,
				"replyTime desc limit 10");
		while (cursor.moveToNext()) {
			replys.add(getReply(context, cursor));
		}
		cursor.close();
		aDatabase.close();
		Log.i("ReplyDao", "GET_REPLYS|size=" + replys.size());
		return replys;
	}

	@SuppressWarnings("deprecation")
	public static Reply getReply(Context context, Cursor cursor) {
		long repId = cursor.getLong(cursor.getColumnIndex("repId"));
		String content = cursor.getString(cursor.getColumnIndex("content"));
		String audio = cursor.getString(cursor.getColumnIndex("audio"));
		ArrayList<String> images = ImagesDao.getImages(context, repId, 3);
		String sReplyTime = cursor
				.getString(cursor.getColumnIndex("replyTime"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date replyTime = null;
		try {
			replyTime = format.parse(sReplyTime);
			replyTime.setHours(replyTime.getHours() + 8);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Log.i("date", replyTime.toString());
		long userId = cursor.getLong(cursor.getColumnIndex("userId"));
		return new Reply(repId, content, audio, images, replyTime, 0l, userId);
	}
	
	public static Reply getReplyById(Context context, long repId) {
		Reply reply = null;
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_REPLY, null, "repId=?",
				new String[] { String.valueOf(repId) }, null, null, null);
		while (cursor.moveToNext()) {
			reply = getReply(context, cursor);
		}
		cursor.close();
		aDatabase.close();
		return reply;
	}

	public static boolean deleteReply(Context context, long repId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		return aDatabase.delete(DBHelper.TABLE_REPLY, "repId=?",
				new String[] { String.valueOf(repId) }) == 1;
	}
}