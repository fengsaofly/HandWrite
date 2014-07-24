package scu.android.db;

import java.util.ArrayList;

import scu.android.entity.Resource;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ResourceDao {

	public final static String TAG = "ResourceDao";

	/**
	 * insert download resources
	 * 
	 * @param context
	 *            context
	 * @param resources
	 *            download resources
	 */
	public static void insertDownloadResource(final Context context,
			final ArrayList<Resource> resources) {
		for (Resource resource : resources) {
			insertResouce(context, resource);
		}
	}

	/**
	 * 插入资源
	 * 
	 * @param dbHelper
	 * @param resource
	 * @return
	 */
	public static long insertResouce(final Context context,
			final ArrayList<Resource> resources) {
		if (resources.size() > 0) {
			long resource_id = insertResouce(context, resources.get(0));
			for (int i = 1; i < resources.size(); i++) {
				resources.get(i).setResourceId(resource_id);
				insertResouce(context, resources.get(i));
			}
			update(context, resource_id, resource_id);
			return resource_id;
		} else {
			return 0l;
		}
	}

	/**
	 * 插入资源
	 * 
	 * @param dbHelper
	 * @param resource
	 * @return
	 */
	public static long insertResouce(final Context context,
			final Resource resource) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		try {
			aDatabase.beginTransaction();
			ContentValues values = new ContentValues();
			values.put("resource_id", resource.getResourceId());
			values.put("resource_spath", resource.getResourceSPath());
			values.put("resource_lpath", resource.getResourceLPath());
			final long id = aDatabase.insert(DBHelper.TABLE_RESOURCE, null,
					values);
			aDatabase.setTransactionSuccessful();
			values.clear();
			if (id != 0) {
				Log.d(TAG, "[insertResouce] id=" + id + resource.toString());
				return id;
			} else {
				Log.e(TAG, "[insertResouce] failed");
				return 0;
			}
		} finally {
			aDatabase.endTransaction();
			aDatabase.close();
		}
	}

	/**
	 * 获取资源
	 * 
	 * @param dbHelper
	 * @param resourceId
	 * @return
	 */
	public static ArrayList<Resource> getResourceById(final Context context,
			final long resourceId) {
		ArrayList<Resource> resources = new ArrayList<Resource>();
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		try {
			Cursor cursor = aDatabase.query(DBHelper.TABLE_RESOURCE, null,
					"resource_id=?",
					new String[] { String.valueOf(resourceId) }, null, null,
					null);
			while (cursor.moveToNext()) {
				String resourceSPath = cursor.getString(cursor
						.getColumnIndex("resource_spath"));
				String resourceLPath = cursor.getString(cursor
						.getColumnIndex("resource_lpath"));
				resources.add(new Resource(resourceId, resourceSPath,
						resourceLPath));
			}
			cursor.close();
		} finally {
			aDatabase.close();
			Log.d(TAG, "[getResourceById] size=" + resources.size());
		}
		return resources;
	}

	public static void update(Context context, long id, long resourceId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		aDatabase.execSQL("update aResource set resource_id=" + resourceId
				+ " where id=" + id);
		aDatabase.close();
	}

	// public static void update(Context context, long id, String
	// newResourcePath) {
	// SQLiteDatabase aDatabase = DBHelper.getInstance(context)
	// .getReadableDatabase();
	// aDatabase.execSQL("update aResource set resource_path='"
	// + newResourcePath + "' where id=" + id);
	// aDatabase.close();
	// Log.d(TAG, "[update] id=" + id + "newResourcePath=" + newResourcePath);
	// }

	public static void updateUploadResource(Context context, long reourceId,
			long oldResourceId) {
		SQLiteDatabase aDatabase = DBHelper.getInstance(context)
				.getReadableDatabase();
		aDatabase.execSQL("update aResource set resource_id=" + reourceId
				+ " where resource_id=" + oldResourceId);
		aDatabase.close();
	}
}
