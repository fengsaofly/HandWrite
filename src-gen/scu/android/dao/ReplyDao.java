package scu.android.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import scu.android.dao.Reply;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table REPLY.
*/
public class ReplyDao extends AbstractDao<Reply, Long> {

    public static final String TABLENAME = "REPLY";

    /**
     * Properties of entity Reply.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Qr_id = new Property(1, Long.class, "qr_id", false, "QR_ID");
        public final static Property Qr_text = new Property(2, String.class, "qr_text", false, "QR_TEXT");
        public final static Property Created_time = new Property(3, Long.class, "created_time", false, "CREATED_TIME");
        public final static Property Qr_type = new Property(4, Short.class, "qr_type", false, "QR_TYPE");
        public final static Property Qr_user = new Property(5, Long.class, "qr_user", false, "QR_USER");
        public final static Property Qr_q = new Property(6, Long.class, "qr_q", false, "QR_Q");
        public final static Property Qr_resource = new Property(7, Long.class, "qr_resource", false, "QR_RESOURCE");
    };

    private DaoSession daoSession;

    private Query<Reply> user_ReplyListQuery;
    private Query<Reply> question_ReplyListQuery;
    private Query<Reply> reply_ReplyListQuery;

    public ReplyDao(DaoConfig config) {
        super(config);
    }
    
    public ReplyDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'REPLY' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'QR_ID' INTEGER," + // 1: qr_id
                "'QR_TEXT' TEXT," + // 2: qr_text
                "'CREATED_TIME' INTEGER," + // 3: created_time
                "'QR_TYPE' INTEGER," + // 4: qr_type
                "'QR_USER' INTEGER," + // 5: qr_user
                "'QR_Q' INTEGER," + // 6: qr_q
                "'QR_RESOURCE' INTEGER);"); // 7: qr_resource
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'REPLY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Reply entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long qr_id = entity.getQr_id();
        if (qr_id != null) {
            stmt.bindLong(2, qr_id);
        }
 
        String qr_text = entity.getQr_text();
        if (qr_text != null) {
            stmt.bindString(3, qr_text);
        }
 
        Long created_time = entity.getCreated_time();
        if (created_time != null) {
            stmt.bindLong(4, created_time);
        }
 
        Short qr_type = entity.getQr_type();
        if (qr_type != null) {
            stmt.bindLong(5, qr_type);
        }
 
        Long qr_user = entity.getQr_user();
        if (qr_user != null) {
            stmt.bindLong(6, qr_user);
        }
 
        Long qr_q = entity.getQr_q();
        if (qr_q != null) {
            stmt.bindLong(7, qr_q);
        }
 
        Long qr_resource = entity.getQr_resource();
        if (qr_resource != null) {
            stmt.bindLong(8, qr_resource);
        }
    }

    @Override
    protected void attachEntity(Reply entity) {
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
    public Reply readEntity(Cursor cursor, int offset) {
        Reply entity = new Reply( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // qr_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // qr_text
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // created_time
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4), // qr_type
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // qr_user
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // qr_q
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // qr_resource
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Reply entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setQr_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setQr_text(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCreated_time(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setQr_type(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4));
        entity.setQr_user(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setQr_q(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setQr_resource(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Reply entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Reply entity) {
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
    
    /** Internal query to resolve the "replyList" to-many relationship of User. */
    public List<Reply> _queryUser_ReplyList(Long qr_user) {
        synchronized (this) {
            if (user_ReplyListQuery == null) {
                QueryBuilder<Reply> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Qr_user.eq(null));
                user_ReplyListQuery = queryBuilder.build();
            }
        }
        Query<Reply> query = user_ReplyListQuery.forCurrentThread();
        query.setParameter(0, qr_user);
        return query.list();
    }

    /** Internal query to resolve the "replyList" to-many relationship of Question. */
    public List<Reply> _queryQuestion_ReplyList(Long qr_q) {
        synchronized (this) {
            if (question_ReplyListQuery == null) {
                QueryBuilder<Reply> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Qr_q.eq(null));
                question_ReplyListQuery = queryBuilder.build();
            }
        }
        Query<Reply> query = question_ReplyListQuery.forCurrentThread();
        query.setParameter(0, qr_q);
        return query.list();
    }

    /** Internal query to resolve the "replyList" to-many relationship of Reply. */
    public List<Reply> _queryReply_ReplyList(Long qr_q) {
        synchronized (this) {
            if (reply_ReplyListQuery == null) {
                QueryBuilder<Reply> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Qr_q.eq(null));
                reply_ReplyListQuery = queryBuilder.build();
            }
        }
        Query<Reply> query = reply_ReplyListQuery.forCurrentThread();
        query.setParameter(0, qr_q);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getQuestionDao().getAllColumns());
            builder.append(" FROM REPLY T");
            builder.append(" LEFT JOIN USER T0 ON T.'QR_USER'=T0.'_id'");
            builder.append(" LEFT JOIN QUESTION T1 ON T.'QR_Q'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Reply loadCurrentDeep(Cursor cursor, boolean lock) {
        Reply entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User user = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setUser(user);
        offset += daoSession.getUserDao().getAllColumns().length;

        Question question = loadCurrentOther(daoSession.getQuestionDao(), cursor, offset);
        entity.setQuestion(question);

        return entity;    
    }

    public Reply loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Reply> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Reply> list = new ArrayList<Reply>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Reply> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Reply> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}