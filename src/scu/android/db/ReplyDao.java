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
			values.put("type", reply.getType());
			repId = aDatabase.insert(DBHelper.TABLE_REPLY, null, values);
			aDatabase.setTransactionSuccessful();
			values.clear();
		} finally {
			aDatabase.endTransaction();
		}
		aDatabase.close();
		if (repId != 0 && reply.getImages() != null)
			ImagesDao.insertImages(context, reply.getImages(), repId, 3);
		Log.i("ReplyDao", "INSERT_REPLY|repId=" + String.valueOf(repId));
		return repId;
	}

	public static ArrayList<Reply> getReply(Context context, long quesId,
			long start, long end, int type) {
		ArrayList<Reply> replys = new ArrayList<Reply>();
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_REPLY, null,
				"quesId=? and type=?", new String[] { String.valueOf(quesId),
						String.valueOf(type) }, null, null,
				"replyTime desc limit " + start + "," + end);
		while (cursor.moveToNext()) {
			replys.add(getReply(context, cursor));
		}
		cursor.close();
		aDatabase.close();
		Log.i("ReplyDao", "GET_REPLYS|size=" + replys.size());
		return replys;
	}

	public static int getReplyNum(Context context, long quesId) {
		int replyNum = 0;
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.rawQuery(
				"select count(repId) from aReply where quesId=? and type=?",
				new String[] { String.valueOf(quesId), String.valueOf(0) });
		cursor.moveToFirst();
		replyNum = cursor.getInt(0);
		cursor.close();
		aDatabase.close();
		return replyNum;
	}

	public static int getTotalReplyNum(Context context) {
		int replyNum = 0;
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.rawQuery("select count(repId) from aReply",
				null);
		cursor.moveToFirst();
		replyNum = cursor.getInt(0);
		cursor.close();
		aDatabase.close();
		return replyNum;
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
		long userId = cursor.getLong(cursor.getColumnIndex("userId"));
		int type = cursor.getInt(cursor.getColumnIndex("type"));
		return new Reply(repId, content, audio, images, replyTime, 0l, userId,
				type);
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

	public static boolean deleteReply(Context context, long repId, long userId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();

		boolean result = aDatabase.delete(DBHelper.TABLE_REPLY, "repId=?",
				new String[] { String.valueOf(repId) }) == 1;
		aDatabase.close();
		return result;
	}
}