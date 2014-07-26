package scu.android.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import scu.android.dao.User;
import scu.android.dao.Question;
import scu.android.dao.Reply;
import scu.android.dao.Resource;
import scu.android.dao.Story;

import scu.android.dao.UserDao;
import scu.android.dao.QuestionDao;
import scu.android.dao.ReplyDao;
import scu.android.dao.ResourceDao;
import scu.android.dao.StoryDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig questionDaoConfig;
    private final DaoConfig replyDaoConfig;
    private final DaoConfig resourceDaoConfig;
    private final DaoConfig storyDaoConfig;

    private final UserDao userDao;
    private final QuestionDao questionDao;
    private final ReplyDao replyDao;
    private final ResourceDao resourceDao;
    private final StoryDao storyDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        questionDaoConfig = daoConfigMap.get(QuestionDao.class).clone();
        questionDaoConfig.initIdentityScope(type);

        replyDaoConfig = daoConfigMap.get(ReplyDao.class).clone();
        replyDaoConfig.initIdentityScope(type);

        resourceDaoConfig = daoConfigMap.get(ResourceDao.class).clone();
        resourceDaoConfig.initIdentityScope(type);

        storyDaoConfig = daoConfigMap.get(StoryDao.class).clone();
        storyDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        questionDao = new QuestionDao(questionDaoConfig, this);
        replyDao = new ReplyDao(replyDaoConfig, this);
        resourceDao = new ResourceDao(resourceDaoConfig, this);
        storyDao = new StoryDao(storyDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(Question.class, questionDao);
        registerDao(Reply.class, replyDao);
        registerDao(Resource.class, resourceDao);
        registerDao(Story.class, storyDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
        questionDaoConfig.getIdentityScope().clear();
        replyDaoConfig.getIdentityScope().clear();
        resourceDaoConfig.getIdentityScope().clear();
        storyDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public QuestionDao getQuestionDao() {
        return questionDao;
    }

    public ReplyDao getReplyDao() {
        return replyDao;
    }

    public ResourceDao getResourceDao() {
        return resourceDao;
    }

    public StoryDao getStoryDao() {
        return storyDao;
    }

}
