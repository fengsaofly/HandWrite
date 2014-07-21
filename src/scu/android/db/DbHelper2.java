package scu.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper2 extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "chatrecord.db";
//	private final static int DATABASE_VERSION = 1;
	private final static int DATABASE_VERSION = 2;
	public final static String TABLE_NAME = "tb_chatrecord";
	
	public DbHelper2(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE_NAME + "(_id integer primary key autoincrement," 
				                                  + " account text,"
		                                          + " time text,"
				                                  + " content text,"
		                                          + " flag text,"
				                                  + " date text,"
		                                          + " type text,"
				                                  + " isGroupChat text,"
		                                          + " jid text,"
				                                  + " content_type text"
				                                  +")";     
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "drop table if exists "+TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
	}

}
