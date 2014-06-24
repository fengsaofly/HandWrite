package scu.android.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import scu.android.entity.Question;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class QuestionDao {

	// 插入问题
	public static long insertQuestion(Context context, Question question) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getWritableDatabase();
		aDatabase.beginTransaction();
		long quesId = 0;
		try {
			ContentValues values = new ContentValues();
			values.put("title", question.getTitle());
			values.put("content", question.getContent());
			values.put("audio", question.getAudio());
			values.put("status", 0);
			values.put("grade", question.getGrade());
			values.put("subject", question.getSubject());
			values.put("userId", question.getUserId());
			quesId = aDatabase.insert(DBHelper.TABLE_QUESTION, null, values);
			aDatabase.setTransactionSuccessful();
			values.clear();
		} finally {
			aDatabase.endTransaction();
		}
		aDatabase.close();
		if (quesId != 0 && question.getImages().size() > 0)
			ImagesDao.insertImages(context, question.getImages(), quesId, 2);
		Log.i("QuestionDao", "INSERT_Question|quesId=" + String.valueOf(quesId));
		return quesId;
	}

	// 获取问题列表
	public static ArrayList<Question> getQuestions(Context context, long start,
			long end) {
		ArrayList<Question> questions = new ArrayList<Question>();
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_QUESTION, null, null,
				null, null, null, "publishTime desc limit  " + start + ","
						+ end);
		while (cursor.moveToNext()) {
			questions.add(getQuestion(context, cursor));
		}
		cursor.close();
		aDatabase.close();
		Log.i("QuestionDao", "GET_QUESTIONS|size=" + questions.size());
		return questions;
	}

	public static int getQuesNum(Context context) {
		int quesNum = 0;
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.rawQuery(
				"select count(quesId) from aQuestion", null);
		cursor.moveToFirst();
		quesNum = cursor.getInt(0);
		cursor.close();
		aDatabase.close();
		return quesNum;
	}

	public static Question getQuestionById(Context context, int quesId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_QUESTION, null,
				"quesId=?", new String[] { String.valueOf(quesId) }, null,
				null, null);
		Question question = null;
		if (cursor.moveToNext()) {
			question = getQuestion(context, cursor);
			cursor.close();
			aDatabase.close();
		}
		return question;
	}

	@SuppressWarnings("deprecation")
	public static Question getQuestion(Context context, Cursor cursor) {
		long quesId = cursor.getLong(cursor.getColumnIndex("quesId"));
		String title = cursor.getString(cursor.getColumnIndex("title"));
		String content = cursor.getString(cursor.getColumnIndex("content"));
		String audio = cursor.getString(cursor.getColumnIndex("audio"));
		ArrayList<String> images = ImagesDao.getImages(context, quesId, 2);
		String sPublishTime = cursor.getString(cursor
				.getColumnIndex("publishTime"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date publishTime = null;
		try {
			publishTime = format.parse(sPublishTime);
			publishTime.setHours(publishTime.getHours() + 8);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		boolean status = cursor.getInt(cursor.getColumnIndex("status")) == 1 ? true
				: false;
		String grade = cursor.getString(cursor.getColumnIndex("grade"));
		String subject = cursor.getString(cursor.getColumnIndex("subject"));
		long userId = cursor.getLong(cursor.getColumnIndex("userId"));
		return new Question(quesId, title, content, audio, images, publishTime,
				status, grade, subject, userId);
	}

	public static boolean deleteQuestion(Context context, long quesId,
			long userId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		
		boolean result = (aDatabase.delete(DBHelper.TABLE_QUESTION, "quesId=?",
				new String[] { String.valueOf(quesId) }) == 1);
		aDatabase.close();
		return result;
	}
}
