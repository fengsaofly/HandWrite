package scu.android.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class DbManager2 {
	
	private DbHelper2 helper;  
	private SQLiteDatabase db;  
	
	public DbManager2(Context context) { 
		helper = new DbHelper2(context);     
		db = helper.getWritableDatabase(); 
	}
	
	public void insertRecord(ChatRecord chatRecord){

		db.execSQL("insert into tb_chatrecord values(null,?,?,?,?,?,?)",
                new String[]{chatRecord.getAccount(),
				chatRecord.getTime(),
				chatRecord.getContent(),
				chatRecord.getFlag(),
				chatRecord.getDate(),
				chatRecord.getType()
		});

	}
 public void delete(String user, String account,String time){	    	
		   db.delete(DbHelper2.TABLE_NAME, "custom1 = ? and account=? and time=?", new String[]{user,account, time});
	    }
	
public Cursor readRecord(String account,String date){	
		Cursor cursor = db.query(DbHelper2.TABLE_NAME, null, "account=? and date = ?", new String[]{account,date}, null, null ,"_id ASC");
		return cursor;
	}
public Cursor readAllRecord(){	
	String selectSql = "select * from " + DbHelper2.TABLE_NAME +" order by time DESC";
	Cursor cursor = db.rawQuery(selectSql, null);
	return cursor;
}
	
public Cursor queryRecent(){
//	String selectSql = "select * from " + DbHelper2.TABLE_NAME +" group by account"+" order by time DESC limit 1";
	String selectSql = "select * from ( select * from "+DbHelper2.TABLE_NAME+" order by time ASC )"+" group by account order by time ASC";
	Cursor cursor = db.rawQuery(selectSql, null);
	return cursor;
	
//	SELECT * 
//	FROM (
//
//	SELECT * 
//	FROM  `Find` 
//	ORDER BY  `create_date` DESC
//	) AS B
//	GROUP BY  `find_username` 
//	ORDER BY  `create_date` DESC
}
public void deleteAllRecord(){
		String selectSql = "select * from " + DbHelper2.TABLE_NAME + " order by _id DESC";
		Cursor cursor = db.rawQuery(selectSql, null);
		if(cursor.getCount()!=0){
		String sql1="delete from "+ DbHelper2.TABLE_NAME;
		String sql2="update sqlite_sequence set seq ='0' where name ='tb_chatrecord'";
		db.execSQL(sql1);
		db.execSQL(sql2);
		}
		else System.out.println("��Ϊ��");
	}


	
	public void close(){
		db.close();
	}


	
}
