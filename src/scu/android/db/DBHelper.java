package scu.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public final static String TABLE_USER = "aUser";
	public final static String TABLE_QUESTION = "aQuestion";
	public final static String TABLE_REPLY = "aReply";
	public final static String TABLE_RESOURCE = "aResource";

	private final String table_user = "CREATE TABLE aUser(userId INTEGER PRIMARY KEY AUTOINCREMENT,userName VARCHAR,password VARCHAR,email VARCHAR,phone INTEGER,type INTEGER,nickname VARCHAR,avatar VARCHAR,school VARCHAR,grade VARCHAR,sex CHAR,age INTEGER,curLon REAL,curLat REAL,createTime DATETIME DEFAULT CURRENT_TIMESTAMP)";
	private final String crtTblReply = "CREATE TABLE aReply(r_id INTEGER PRIMARY KEY AUTOINCREMENT,r_text_content VARCHAR,r_resource INTEGER,created_time TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime')),r_user INTEGER,q_id INTEGER,r_type INTEGER)";
	private final String crtTblQuestion = "CREATE TABLE aQuestion(q_id INTEGER PRIMARY KEY AUTOINCREMENT,q_title VARCHAR NOT NULL,q_user INTEGER NOT NULL,q_text_content TEXT,q_resource INTEGER,created_time TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime')),q_state INTEGER NOT NULL,q_grade VARCHAR,q_subject VARCHAR)";
	private final String crtTblResource = "CREATE TABLE aResource(id INTEGER PRIMARY KEY AUTOINCREMENT,resource_id INTEGER NOT NULL,resource_path VARCHAR NOT NULL)";

	public static DBHelper getInstance(Context context) {
		DBHelper dbHelper = new DBHelper(context);
		return dbHelper;
	}

	public DBHelper(Context context) {
		super(context, "demo.db", null, 19);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(table_user);
		db.execSQL(crtTblQuestion);
		db.execSQL(crtTblReply);
		db.execSQL(crtTblResource);
		Log.i("SQL-CREATE", "CREATE_TABLE");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS aUser");
		db.execSQL("DROP TABLE IF EXISTS aQuestion");
		db.execSQL("DROP TABLE IF EXISTS aReply");
		db.execSQL("DROP TABLE IF EXISTS aResource");
		Log.i("SQL-UPGRADE", "DROP_TABLE");
		onCreate(db);
	}

}
