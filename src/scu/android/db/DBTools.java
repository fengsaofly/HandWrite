package scu.android.db;

import java.util.List;

import scu.android.application.MyApplication;
import scu.android.dao.DaoSession;
import scu.android.dao.Question;
import scu.android.dao.QuestionDao;
import scu.android.dao.QuestionDao.Properties;
import scu.android.dao.Reply;
import scu.android.dao.ReplyDao;
import scu.android.dao.Resource;
import scu.android.dao.ResourceDao;
import scu.android.dao.Story;
import scu.android.dao.StoryDao;
import scu.android.dao.User;
import scu.android.dao.UserDao;
import android.content.Context;
import android.util.Log;
import de.greenrobot.dao.query.QueryBuilder;

public class DBTools {

	private final String TAG = getClass().getName();
	private static DBTools instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	private UserDao userDao;
	private QuestionDao questionDao;
	private ReplyDao replyDao;
	private ResourceDao resourceDao;
	private StoryDao storyDao;

	public static DBTools getInstance(Context context) {
		if (instance == null) {
			instance = new DBTools();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = MyApplication.getDaoSession(context);
			instance.userDao = instance.mDaoSession.getUserDao();
			instance.questionDao = instance.mDaoSession.getQuestionDao();
			instance.replyDao = instance.mDaoSession.getReplyDao();
			instance.resourceDao = instance.mDaoSession.getResourceDao();
			instance.storyDao=instance.mDaoSession.getStoryDao();
		}
		return instance;
	}

	public long insertQuestion(final Question entity) {
		long id = questionDao.insert(entity);
		entity.setQ_id(id);
		updateQuestion(entity);
		return id;
	}

	public void insertQuestions(final List<Question> entities) {
		questionDao.insertInTx(entities);
	}

	public void deleteQuestion(final Question entity) {
		questionDao.delete(entity);
		test();
	}

	public void updateQuestion(final Question entity) {
		questionDao.update(entity);
	}

	public List<Question> loadAllQuestion() {
		return questionDao.loadAll();
	}

	public List<Question> loadQuestions(final long floorTime) {
		QueryBuilder<Question> qb = questionDao.queryBuilder();
		qb.where(Properties.Created_time.gt(floorTime)).orderDesc(
				Properties.Created_time);
		return qb.list();
	}

	public List<Question> loadQuestions(final long topTime, final int lens) {
		QueryBuilder<Question> qb = questionDao.queryBuilder();
		qb.where(Properties.Created_time.lt(topTime))
				.orderDesc(Properties.Created_time).limit(lens);
		return qb.list();
	}

	public List<Question> loadQuestions(final int lens) {
		QueryBuilder<Question> qb = questionDao.queryBuilder()
				.orderDesc(Properties.Created_time).limit(lens);
		return qb.list();
	}

	public Question loadQuestionById(final long id) {
		return questionDao.loadByRowId(id);
	}

	public long insertReply(final Reply entity) {
		long id = replyDao.insert(entity);
		entity.setQr_id(id);
		updateReply(entity);
		return id;
	}

	public void insertReplys(final List<Reply> entities) {
		replyDao.insertInTx(entities);
	}

	public void deleteReply(final Reply entity) {
		replyDao.delete(entity);
	}

	public void deleteReplys(final List<Reply> entities) {
		replyDao.deleteInTx(entities);
	}

	public void updateReply(final Reply entity) {
		replyDao.update(entity);
	}

	public Reply loadReplyById(final long id) {
		return replyDao.loadByRowId(id);
	}

	public List<Reply> loadReplys(final long qr_q, final short qr_type,
			final int lens) {
		QueryBuilder<Reply> qb = replyDao.queryBuilder();
		qb.where(scu.android.dao.ReplyDao.Properties.Qr_q.eq(qr_q),
				scu.android.dao.ReplyDao.Properties.Qr_type.eq(qr_type))
				.orderDesc(scu.android.dao.ReplyDao.Properties.Created_time)
				.limit(lens);
		return qb.list();
	}

	public List<Reply> loadReplys(final long qr_q, final short qr_type,
			final long topTime, final int lens) {
		QueryBuilder<Reply> qb = replyDao.queryBuilder();
		qb.where(scu.android.dao.ReplyDao.Properties.Qr_q.eq(qr_q),
				scu.android.dao.ReplyDao.Properties.Qr_type.eq(qr_type),
				scu.android.dao.ReplyDao.Properties.Created_time.lt(topTime))
				.orderDesc(scu.android.dao.ReplyDao.Properties.Created_time)
				.limit(lens);
		return qb.list();
	}

	public List<Reply> loadReplys(final long qr_q, final short qr_type,
			final long floorTime) {
		QueryBuilder<Reply> qb = replyDao.queryBuilder();
		qb.where(scu.android.dao.ReplyDao.Properties.Qr_q.eq(qr_q),
				scu.android.dao.ReplyDao.Properties.Qr_type.eq(qr_type),
				scu.android.dao.ReplyDao.Properties.Created_time.gt(floorTime))
				.orderDesc(scu.android.dao.ReplyDao.Properties.Created_time);
		return qb.list();
	}

	public long getReplysNum(final long qr_q) {
		QueryBuilder<Reply> qb = replyDao.queryBuilder();
		qb.where(scu.android.dao.ReplyDao.Properties.Qr_q.eq(qr_q),
				scu.android.dao.ReplyDao.Properties.Qr_type.eq(0));
		return qb.count();
	}

	public long insertResource(final Resource entity) {
		return resourceDao.insert(entity);
	}

	public void insertDownloadResource(final List<Resource> entities) {
		resourceDao.insertInTx(entities);
	}

	public long insertResources(final List<Resource> entities) {
		Resource resource = entities.get(0);
		final long id = insertResource(resource);
		for (int i = 1; i < entities.size(); i++) {
			Resource entity = entities.get(i);
			entity.setResource_id(id);
			insertResource(entity);
		}
		resource.setResource_id(id);
		updateResource(resource);
		return id;
	}

	public void deleteResources(final List<Resource> entities) {
		resourceDao.deleteInTx(entities);
	}

	public void updateResource(final Resource entity) {
		resourceDao.update(entity);
	}

	public void updateResources(final List<Resource> entities) {
		resourceDao.updateInTx(entities);
	}

	public List<Resource> loadResources(final long id) {
		QueryBuilder<Resource> qb = resourceDao.queryBuilder();
		qb.where(scu.android.dao.ResourceDao.Properties.Resource_id.eq(id));
		return qb.list();
	}

	public List<User> loadAllUser() {
		return userDao.loadAll();
	}

	public User loadUser(long id) {
		return userDao.load(id);
	}
	
	
	public long insertStory(final Story entity) {
		long id = storyDao.insert(entity);
		entity.setS_id(id);
		updateStory(entity);
		return id;
	}

	public void insertStorys(final List<Story> entities) {
		storyDao.insertInTx(entities);
	}

	public void deleteStory(final Story entity) {
		storyDao.delete(entity);
		test();
	}

	public void updateStory(final Story entity) {
		storyDao.update(entity);
	}

	public List<Story> loadAllStory() {
		return storyDao.loadAll();
	}

	public List<Story> loadStorys(final long floorTime) {
		QueryBuilder<Story> qb = storyDao.queryBuilder();
		qb.where(scu.android.dao.StoryDao.Properties.Created_time.gt(floorTime)).orderDesc(
				scu.android.dao.StoryDao.Properties.Created_time);
		return qb.list();
	}

	public List<Story> loadStorys(final long topTime, final int lens) {
		QueryBuilder<Story> qb = storyDao.queryBuilder();
		qb.where(scu.android.dao.StoryDao.Properties.Created_time.lt(topTime))
				.orderDesc(scu.android.dao.StoryDao.Properties.Created_time).limit(lens);
		return qb.list();
	}

	public List<Story> loadStorys(final int lens) {
		QueryBuilder<Story> qb = storyDao.queryBuilder()
				.orderDesc(scu.android.dao.StoryDao.Properties.Created_time).limit(lens);
		return qb.list();
	}

	public Question loadStoryById(final long id) {
		return questionDao.loadByRowId(id);
	}

	public static int getType(final String path) {
		if (path.endsWith(".jpg") || path.endsWith(".JPG")
				|| path.endsWith(".png") || path.endsWith(".PNG")) {
			return 0;
		} else if (path.endsWith(".amr") || path.endsWith(".AMR")) {
			return 1;
		}
		return 2;
	}

	public static String getAudio(List<Resource> resources) {
		String audio = null;
		if (resources != null) {
			for (Resource resource : resources) {
				if (getType(resource.getResource_lpath()) == 1) {
					audio = resource.getResource_lpath();
					break;
				}
			}
		}
		return audio;
	}
	
//	public static ArrayList<String> getLImages(List<Resource> resources) {
//		ArrayList<String> images = new ArrayList<String>();
//		if (resources != null) {
//			for (Resource resource : resources) {
//				if (getType(resource.getResource_lpath()) == 0) {
//					images.add(resource.getResource_lpath());
//				}
//			}
//		}
//		return images;
//	}
//
//	public static ArrayList<String> getSImages(List<Resource> resources) {
//		ArrayList<String> images = new ArrayList<String>();
//		if (resources != null) {
//			for (Resource resource : resources) {
//				if (getType(resource.getResource_spath()) == 0) {
//					images.add(resource.getResource_spath());
//				}
//			}
//		}
//		return images;
//	}

	public static String getLImage(List<Resource> resources) {
		String img=null;
		if (resources != null) {
			for (Resource resource : resources) {
				if (getType(resource.getResource_lpath()) == 0) {
					img=resource.getResource_lpath();
					break;
				}
			}
		}
		return img;
	}

	public static String getSImage(List<Resource> resources) {
		String img=null;
		if (resources != null) {
			for (Resource resource : resources) {
				if (getType(resource.getResource_spath()) == 0) {
					img=resource.getResource_spath();
					break;
				}
			}
		}
		return img;
	}

	
	
	public static String getState(final int state) {
		String status = null;
		switch (state) {
		case 0:
			status = "未解决";
			break;
		case 1:
			status = "已解决";
			break;
		case 2:
			status = "上传中...";
			break;
		case 3:
			status = "上传失败";
			break;
		}
		return status;
	}

	private void test() {
		long qCount = questionDao.queryBuilder().count();
		long rCount = replyDao.queryBuilder().count();
		long rrCount = resourceDao.queryBuilder().count();
		Log.d(TAG, "qCount:" + qCount + "rCount:" + rCount + "rrCount:"
				+ rrCount);
	}
}
