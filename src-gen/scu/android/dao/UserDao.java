package scu.android.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import scu.android.dao.User;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table USER.
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property User_id = new Property(1, Long.class, "user_id", false, "USER_ID");
        public final static Property User_name = new Property(2, String.class, "user_name", false, "USER_NAME");
        public final static Property Created_time = new Property(3, Long.class, "created_time", false, "CREATED_TIME");
        public final static Property Grade = new Property(4, String.class, "grade", false, "GRADE");
        public final static Property User_avatar = new Property(5, String.class, "user_avatar", false, "USER_AVATAR");
        public final static Property User_nickname = new Property(6, String.class, "user_nickname", false, "USER_NICKNAME");
        public final static Property User_sex = new Property(7, Short.class, "user_sex", false, "USER_SEX");
        public final static Property User_pwd = new Property(8, String.class, "user_pwd", false, "USER_PWD");
        public final static Property Cur_lon = new Property(9, String.class, "cur_lon", false, "CUR_LON");
        public final static Property Cur_lat = new Property(10, String.class, "cur_lat", false, "CUR_LAT");
        public final static Property Cur_age = new Property(11, Short.class, "cur_age", false, "CUR_AGE");
        public final static Property User_email = new Property(12, String.class, "user_email", false, "USER_EMAIL");
        public final static Property User_type = new Property(13, Short.class, "user_type", false, "USER_TYPE");
        public final static Property User_school = new Property(14, String.class, "user_school", false, "USER_SCHOOL");
        public final static Property User_phone = new Property(15, String.class, "user_phone", false, "USER_PHONE");
    };

    private DaoSession daoSession;


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'USER' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'USER_ID' INTEGER," + // 1: user_id
                "'USER_NAME' TEXT," + // 2: user_name
                "'CREATED_TIME' INTEGER," + // 3: created_time
                "'GRADE' TEXT," + // 4: grade
                "'USER_AVATAR' TEXT," + // 5: user_avatar
                "'USER_NICKNAME' TEXT," + // 6: user_nickname
                "'USER_SEX' INTEGER," + // 7: user_sex
                "'USER_PWD' TEXT," + // 8: user_pwd
                "'CUR_LON' TEXT," + // 9: cur_lon
                "'CUR_LAT' TEXT," + // 10: cur_lat
                "'CUR_AGE' INTEGER," + // 11: cur_age
                "'USER_EMAIL' TEXT," + // 12: user_email
                "'USER_TYPE' INTEGER," + // 13: user_type
                "'USER_SCHOOL' TEXT," + // 14: user_school
                "'USER_PHONE' TEXT);"); // 15: user_phone
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'USER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindLong(2, user_id);
        }
 
        String user_name = entity.getUser_name();
        if (user_name != null) {
            stmt.bindString(3, user_name);
        }
 
        Long created_time = entity.getCreated_time();
        if (created_time != null) {
            stmt.bindLong(4, created_time);
        }
 
        String grade = entity.getGrade();
        if (grade != null) {
            stmt.bindString(5, grade);
        }
 
        String user_avatar = entity.getUser_avatar();
        if (user_avatar != null) {
            stmt.bindString(6, user_avatar);
        }
 
        String user_nickname = entity.getUser_nickname();
        if (user_nickname != null) {
            stmt.bindString(7, user_nickname);
        }
 
        Short user_sex = entity.getUser_sex();
        if (user_sex != null) {
            stmt.bindLong(8, user_sex);
        }
 
        String user_pwd = entity.getUser_pwd();
        if (user_pwd != null) {
            stmt.bindString(9, user_pwd);
        }
 
        String cur_lon = entity.getCur_lon();
        if (cur_lon != null) {
            stmt.bindString(10, cur_lon);
        }
 
        String cur_lat = entity.getCur_lat();
        if (cur_lat != null) {
            stmt.bindString(11, cur_lat);
        }
 
        Short cur_age = entity.getCur_age();
        if (cur_age != null) {
            stmt.bindLong(12, cur_age);
        }
 
        String user_email = entity.getUser_email();
        if (user_email != null) {
            stmt.bindString(13, user_email);
        }
 
        Short user_type = entity.getUser_type();
        if (user_type != null) {
            stmt.bindLong(14, user_type);
        }
 
        String user_school = entity.getUser_school();
        if (user_school != null) {
            stmt.bindString(15, user_school);
        }
 
        String user_phone = entity.getUser_phone();
        if (user_phone != null) {
            stmt.bindString(16, user_phone);
        }
    }

    @Override
    protected void attachEntity(User entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // user_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // user_name
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // created_time
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // grade
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // user_avatar
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // user_nickname
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7), // user_sex
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // user_pwd
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // cur_lon
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // cur_lat
            cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11), // cur_age
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // user_email
            cursor.isNull(offset + 13) ? null : cursor.getShort(offset + 13), // user_type
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // user_school
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15) // user_phone
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUser_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setUser_name(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCreated_time(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setGrade(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUser_avatar(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setUser_nickname(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setUser_sex(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7));
        entity.setUser_pwd(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setCur_lon(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCur_lat(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setCur_age(cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11));
        entity.setUser_email(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setUser_type(cursor.isNull(offset + 13) ? null : cursor.getShort(offset + 13));
        entity.setUser_school(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setUser_phone(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
