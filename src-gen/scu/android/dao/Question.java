package scu.android.dao;

import java.util.List;
import scu.android.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table QUESTION.
 */
public class Question implements java.io.Serializable {

    private Long id;
    private Long q_id;
    private String q_title;
    private String q_text_content;
    private Long created_time;
    private String q_grade;
    private String q_subject;
    private Short q_state;
    private Long q_user;
    private Long q_resource;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient QuestionDao myDao;

    private User user;
    private Long user__resolvedKey;

    private List<Reply> replyList;
    private List<Resource> resourceList;

    public Question() {
    }

    public Question(Long id) {
        this.id = id;
    }

    public Question(Long id, Long q_id, String q_title, String q_text_content, Long created_time, String q_grade, String q_subject, Short q_state, Long q_user, Long q_resource) {
        this.id = id;
        this.q_id = q_id;
        this.q_title = q_title;
        this.q_text_content = q_text_content;
        this.created_time = created_time;
        this.q_grade = q_grade;
        this.q_subject = q_subject;
        this.q_state = q_state;
        this.q_user = q_user;
        this.q_resource = q_resource;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getQuestionDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQ_id() {
        return q_id;
    }

    public void setQ_id(Long q_id) {
        this.q_id = q_id;
    }

    public String getQ_title() {
        return q_title;
    }

    public void setQ_title(String q_title) {
        this.q_title = q_title;
    }

    public String getQ_text_content() {
        return q_text_content;
    }

    public void setQ_text_content(String q_text_content) {
        this.q_text_content = q_text_content;
    }

    public Long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Long created_time) {
        this.created_time = created_time;
    }

    public String getQ_grade() {
        return q_grade;
    }

    public void setQ_grade(String q_grade) {
        this.q_grade = q_grade;
    }

    public String getQ_subject() {
        return q_subject;
    }

    public void setQ_subject(String q_subject) {
        this.q_subject = q_subject;
    }

    public Short getQ_state() {
        return q_state;
    }

    public void setQ_state(Short q_state) {
        this.q_state = q_state;
    }

    public Long getQ_user() {
        return q_user;
    }

    public void setQ_user(Long q_user) {
        this.q_user = q_user;
    }

    public Long getQ_resource() {
        return q_resource;
    }

    public void setQ_resource(Long q_resource) {
        this.q_resource = q_resource;
    }

    /** To-one relationship, resolved on first access. */
    public User getUser() {
        Long __key = this.q_user;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
            	user__resolvedKey = __key;
            }
        }
        return user;
    }

    public void setUser(User user) {
        synchronized (this) {
            this.user = user;
            q_user = user == null ? null : user.getId();
            user__resolvedKey = q_user;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Reply> getReplyList() {
        if (replyList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReplyDao targetDao = daoSession.getReplyDao();
            List<Reply> replyListNew = targetDao._queryQuestion_ReplyList(q_id);
            synchronized (this) {
                if(replyList == null) {
                    replyList = replyListNew;
                }
            }
        }
        return replyList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetReplyList() {
        replyList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Resource> getResourceList() {
        if (resourceList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ResourceDao targetDao = daoSession.getResourceDao();
            List<Resource> resourceListNew = targetDao._queryQuestion_ResourceList(q_resource);
            synchronized (this) {
                if(resourceList == null) {
                    resourceList = resourceListNew;
                }
            }
        }
        return resourceList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetResourceList() {
        resourceList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
