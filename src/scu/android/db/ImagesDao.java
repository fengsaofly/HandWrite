package scu.android.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ImagesDao {

	public static void insertImages(Context context, ArrayList<String> images,
			long id, int type) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getWritableDatabase();
		aDatabase.beginTransaction();
		int number = 0;
		try {
			for (String imgPath : images) {
				ContentValues values = new ContentValues();
				values.put("imgPath", imgPath);
				values.put("imgFrom", id);
				values.put("imgSize", 0);
				values.put("type", type);
				if (aDatabase.insert(DBHelper.TABLE_IMAGES, null, values) != 0)
					++number;
				values.clear();
			}
			aDatabase.setTransactionSuccessful();
		} finally {
			aDatabase.endTransaction();
		}
		aDatabase.close();
		Log.i("ImageDao", "INSERT_IMAGES|number=" + number);
	}

	public static ArrayList<String> getImages(Context context, long id, int type) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		Cursor cursor = aDatabase.query(DBHelper.TABLE_IMAGES, null,
				"imgFrom=? and type=?", new String[] { String.valueOf(id),
						String.valueOf(type) }, null, null, null, null);
		ArrayList<String> images = new ArrayList<String>();
		int number = 0;
		while (cursor.moveToNext()) {
			images.add(cursor.getString(cursor.getColumnIndex("imgPath")));
			++number;
		}
		Log.i("ImageDao", "GET_IMAGES|number=" + number);
		cursor.close();
		aDatabase.close();
		return images;
	}

	public static boolean deleteImages(Context context, long quesId, long userId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		boolean result= aDatabase
				.delete(DBHelper.TABLE_REPLY,
						"quesId=? and userId=?",
						new String[] { String.valueOf(quesId),
								String.valueOf(userId) }) == 1;
		aDatabase.close();
		return result;
	}
}
