package scu.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public final static String TABLE_USER = "aUser";
	public final static String TABLE_QUESTION = "aQuestion";
	public final static String TABLE_REPLY = "aReply";
	public final static String TABLE_IMAGES = "aImages";

	private final String table_user = "CREATE TABLE aUser(userId INTEGER PRIMARY KEY AUTOINCREMENT,userName VARCHAR,password VARCHAR,email VARCHAR,phone INTEGER,type INTEGER,nickname VARCHAR,avatar VARCHAR,school VARCHAR,grade VARCHAR,sex CHAR,age INTEGER,curLon REAL,curLat REAL)";
	private final String table_question = "CREATE TABLE aQuestion(quesId INTEGER PRIMARY KEY AUTOINCREMENT,title VARCHAR,content VARCHAR,audio VARCHAR,publishTime DATETIME DEFAULT CURRENT_TIMESTAMP,status INTEGER,grade VARCHAR,subject VARCHAR,userId INTEGER,FOREIGN KEY(userId) REFERENCES aUser(userId))";
	private final String table_reply = "CREATE TABLE aReply(repId INTEGER PRIMARY KEY AUTOINCREMENT,content VARCHAR,audio VARCHAR,replyTime DATETIME DEFAULT CURRENT_TIMESTAMP,userId INTEGER,quesId INTEGER,FOREIGN KEY(userId) REFERENCES aUser(userId),FOREIGN KEY(quesId) REFERENCES aQuestion(quesId))";
	private final String table_images = "CREATE TABLE aImages(imgId INTEGER PRIMARY KEY AUTOINCREMENT,imgPath VARCHAR,imgFrom VARCHAR,imgSize INTEGER,type INTEGER)";

	public static DBHelper getInstance(Context context) {
		DBHelper dbHelper = new DBHelper(context);
		return dbHelper;
	}

	public DBHelper(Context context) {
		super(context, "test.db", null, 7);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(table_user);
		db.execSQL(table_question);
		db.execSQL(table_reply);
		db.execSQL(table_images);
		Log.i("SQL-CREATE", "CREATE_TABLE");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS aUser");
		db.execSQL("DROP TABLE IF EXISTS aQuestion");
		db.execSQL("DROP TABLE IF EXISTS aReply");
		db.execSQL("DROP TABLE IF EXISTS aImages");
		Log.i("SQL-UPGRADE", "DROP_TABLE");
		onCreate(db);
	}

}
