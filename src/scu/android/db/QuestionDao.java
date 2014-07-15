package scu.android.db;

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

	private static final String TAG = "QuestionDao";

	/**
	 * 插入问题
	 * 
	 * @param context
	 *            activity context
	 * @param question
	 *            插入问题
	 * @return
	 */
	public static long insertQuestion(Context context, Question question) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getWritableDatabase();
		aDatabase.beginTransaction();
		long qId = 0;
		try {
			ContentValues values = new ContentValues();
			values.put("q_title", question.getqTitle());
			values.put("q_user", question.getqUser());
			values.put("q_text_content", question.getqTextContent());
			values.put("q_resource", question.getqResource());
			values.put("created_time", question.getCreatedTime().getTime());
			values.put("q_state", question.getqState());
			values.put("q_grade", question.getqGrade());
			values.put("q_subject", question.getqSubject());
			qId = aDatabase.insert(DBHelper.TABLE_QUESTION, null, values);
			aDatabase.setTransactionSuccessful();
			values.clear();
		} finally {
			aDatabase.endTransaction();
		}
		aDatabase.close();
		question.setqId(qId);
		Log.d(TAG, "[insertQuestion] " + question.toString());
		return qId;
	}

	/**
	 * 批量插入问题
	 * 
	 * @param context
	 * @param questions
	 *            问题列表
	 * @return
	 */
	public static int insertQuestions(Context context,
			ArrayList<Question> questions) {
		int number = 0;
		for (Question question : questions) {
			if (insertQuestion(context, question) != 0) {
				++number;
			}
		}
		Log.d(TAG, "[insertQuestions] number=" + number);
		return number;
	}

	/**
	 * 获取问题总数
	 * 
	 * @param context
	 * @return
	 */
	public static int getQuesNum(Context context) {
		int quesNum = 0;
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.rawQuery("select count(q_id) from aQuestion",
				null);
		cursor.moveToFirst();
		quesNum = cursor.getInt(0);
		cursor.close();
		aDatabase.close();
		return quesNum;
	}

	/**
	 * 通过id获取问题
	 * 
	 * @param context
	 * @param quesId
	 *            问题id
	 * @return
	 */
	public static Question getQuestionById(Context context, int quesId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_QUESTION, null,
				"q_id=?", new String[] { String.valueOf(quesId) }, null, null,
				null);
		Question question = null;
		if (cursor.moveToNext()) {
			question = getQuestion(context, cursor);
			cursor.close();
			aDatabase.close();
		}
		return question;
	}

	public static Question getQuestion(Context context, Cursor cursor) {
		long qId = cursor.getLong(cursor.getColumnIndex("q_id"));
		String qTitle = cursor.getString(cursor.getColumnIndex("q_title"));
		String qTextContent = cursor.getString(cursor
				.getColumnIndex("q_text_content"));
		long qResource = cursor.getLong(cursor.getColumnIndex("q_resource"));
		long createdTime = cursor
				.getLong(cursor.getColumnIndex("created_time"));
		int qState = cursor.getInt(cursor.getColumnIndex("q_state"));
		String qGrade = cursor.getString(cursor.getColumnIndex("q_grade"));
		String qSubject = cursor.getString(cursor.getColumnIndex("q_subject"));
		long qUser = cursor.getLong(cursor.getColumnIndex("q_user"));
		Question question = new Question(qId, qTitle, qUser, qTextContent,
				qResource, new Date(createdTime), qState, qGrade, qSubject);
		question.setResources(ResourceDao.getResourceById(context, qResource));

		return question;
	}

	public static boolean deleteQuestion(Context context, long qId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		aDatabase.delete(DBHelper.TABLE_QUESTION, "q_id=?",
				new String[] { String.valueOf(qId) });
		aDatabase.close();
		return true;
	}

	public static void updateUploadQuestion(Context context, long qId,
			long qResource, long oldQId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		aDatabase.execSQL("update aQuestion set q_state=0,q_id=" + qId
				+ ",q_resource=" + qResource + " where q_id=" + oldQId);
		aDatabase.close();
	}

	public static void updateUploadQuestion(Context context, long oldQId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		aDatabase.execSQL("update aQuestion set q_state=3" + " where q_id="
				+ oldQId);
		aDatabase.close();
	}

	/**
	 * 
	 * @param context
	 * @param startTime
	 *            起始时间
	 * @return startTime之后发布的问题列表
	 */
	public static ArrayList<Question> getQuestions(Context context,
			final long startTime, final int lens) {
		ArrayList<Question> questions = new ArrayList<Question>();
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_QUESTION, null,
				"created_time < ?", new String[] { String.valueOf(startTime) },
				null, null, "created_time desc limit  " + lens);
		while (cursor.moveToNext()) {
			questions.add(getQuestion(context, cursor));
		}
		cursor.close();
		aDatabase.close();
		Log.d(TAG, "[getQuestions] size=" + questions.size());
		return questions;
	}
}
