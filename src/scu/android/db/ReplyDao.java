package scu.android.db;

import java.util.ArrayList;
import java.util.Date;
import scu.android.entity.Reply;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ReplyDao {

	
	public static long insertReply(Context context, Reply reply) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getWritableDatabase();
		aDatabase.beginTransaction();
		long rId = 0;
		try {
			ContentValues values = new ContentValues();
			values.put("r_text_content", reply.getrTextContent());
			values.put("r_resource", reply.getrResource());
			values.put("created_time", System.currentTimeMillis());
			values.put("q_id", reply.getqId());
			values.put("r_user", reply.getrUser());
			values.put("r_type", reply.getType());
			rId = aDatabase.insert(DBHelper.TABLE_REPLY, null, values);
			aDatabase.setTransactionSuccessful();
			values.clear();
		} finally {
			aDatabase.endTransaction();
		}
		aDatabase.close();
		Log.i("ReplyDao", "INSERT_REPLY|r_id=" + String.valueOf(rId));
		return rId;
	}

	public static ArrayList<Reply> getReply(Context context, long qId,
			long start, long end, int type) {
		ArrayList<Reply> replys = new ArrayList<Reply>();
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_REPLY, null,
				"q_id=? and r_type=?", new String[] { String.valueOf(qId),
						String.valueOf(type) }, null, null,
				"created_time desc limit " + start + "," + end);
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
				"select count(r_id) from aReply where q_id=? and r_type=?",
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
		Cursor cursor = aDatabase.rawQuery("select count(r_id) from aReply",
				null);
		cursor.moveToFirst();
		replyNum = cursor.getInt(0);
		cursor.close();
		aDatabase.close();
		return replyNum;
	}

	public static Reply getReply(Context context, Cursor cursor) {
		long rId = cursor.getLong(cursor.getColumnIndex("r_id"));
		String rTextContent = cursor.getString(cursor
				.getColumnIndex("r_text_content"));
		long rResource = cursor.getLong(cursor.getColumnIndex("r_resource"));
		// String sReplyTime = cursor.getString(cursor
		// .getColumnIndex("created_time"));
		// SimpleDateFormat format = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// Date createdTime = null;
		// try {
		// createdTime = format.parse(sReplyTime);
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		long createdTime = cursor
				.getLong(cursor.getColumnIndex("created_time"));
		long qId = cursor.getLong(cursor.getColumnIndex("q_id"));
		long rUser = cursor.getLong(cursor.getColumnIndex("r_user"));
		int type = cursor.getInt(cursor.getColumnIndex("r_type"));
		Reply reply = new Reply(rId, rTextContent, rResource, new Date(
				createdTime), qId, rUser, type);
		reply.setResources(ResourceDao.getResourceById(context, rResource));
		return reply;
	}

	public static Reply getReplyById(Context context, long repId) {
		Reply reply = null;
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_REPLY, null, "r_id=?",
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

		boolean result = aDatabase.delete(DBHelper.TABLE_REPLY, "r_id=?",
				new String[] { String.valueOf(repId) }) == 1;
		aDatabase.close();
		return result;
	}
}