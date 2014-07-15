package scu.android.activity;

import java.util.ArrayList;
import java.util.Date;

import scu.android.application.MyApplication;
import scu.android.base.CommonEditor;
import scu.android.db.ReplyDao;
import scu.android.db.ResourceDao;
import scu.android.entity.Question;
import scu.android.entity.Reply;
import scu.android.entity.Resource;
import scu.android.entity.User;
import scu.android.ui.PhotosAdapter;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 回复问题
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class ReplyQuestionActivity extends Activity {
	private Context context;
	private Question question;// 问题
	private User user;
	private int replyNum;
	private CommonEditor editor;
	private BroadcastReceiver receiver;
	private final String action = "scu.android.activiy.RelpyQuestionActivity";
	private ImageLoader loader;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_reply);
		init();
	}

	public void init() {
		this.context = getApplicationContext();
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_avatar)
				.showImageForEmptyUri(R.drawable.default_avatar)
				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		question = (Question) getIntent().getSerializableExtra("question");
		user = (User) getIntent().getSerializableExtra("user");
		getActionBar().setTitle(question.getqTitle());
		((TextView) findViewById(R.id.nickname)).setText(user.getNickname());
		((TextView) findViewById(R.id.publish_time)).setText(AppUtils
				.timeToNow(question.getCreatedTime()));
		((TextView) findViewById(R.id.title)).setText(question.getqTitle());
		((TextView) findViewById(R.id.grade)).setText(question.getqGrade());
		((TextView) findViewById(R.id.subject)).setText(question.getqSubject());
		((TextView) findViewById(R.id.content)).setText(question
				.getqTextContent());
		((TextView) findViewById(R.id.reply_number)).setText("获取中...");
		GridView view = ((GridView) findViewById(R.id.photos_view));
		ArrayList<String> thumbnails = Resource.getSImages(question
				.getResources());
		ArrayList<String> images = Resource.getLImages(question.getResources());
		view.setAdapter(new PhotosAdapter(this, thumbnails, images));
		ImageView avatar = (ImageView) findViewById(R.id.avatar);
		// final int width = AppUtils.getDefaultPhotoWidth(this, 9);
		// AppUtils.setViewSize(avatar, width, width);
		loader.displayImage(user.getAvatar(), avatar, options);
		editor = (CommonEditor) findViewById(R.id.common_editor);
		editor.setAction(action);
		editor.setActivity(this);

		receiver = new PhotoReceiver();
		registerReceiver(receiver, new IntentFilter(action));
	}

	/*
	 * 获取问题的回复数目
	 */
	private class GetReplysTask extends AsyncTask<Long, Void, Integer> {

		@Override
		protected Integer doInBackground(Long... params) {
			int replyNum = ReplyDao.getReplyNum(ReplyQuestionActivity.this,
					params[0]);
			return replyNum;
		}

		@Override
		protected void onPostExecute(Integer result) {
			replyNum = result;
			((TextView) findViewById(R.id.reply_number)).setText("回答(" + result
					+ ")");
			super.onPostExecute(result);
		}

	}

	/*
	 * 获取图片删除信息
	 */
	private class PhotoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(action)) {
				int index = intent.getIntExtra("photoIndex", 10);
				if (index != 10) {
					editor.getThumbnails().remove(index);
					editor.getThumbnailsAdapter().notifyDataSetChanged();
					int curPhotosNum = editor.getCurPhotosNum() - 1;
					editor.setCurPhotosNum(curPhotosNum);
					if (curPhotosNum == 0) {
						editor.getThumbnailsParentView().setVisibility(
								View.INVISIBLE);
						editor.getExtrasView().setVisibility(View.GONE);
					}
				} else {
					doReply();
				}
			}
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reply:
			doReply();
			break;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final String prefix = "file:///";
		switch (requestCode) {
		case Constants.SYS_CAMEAR:// 相机
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = editor.getCameraName();
				int curPhotosNum = editor.getCurPhotosNum() + 1;
				editor.getThumbnails().add(prefix + imgPath);
				editor.setCurPhotosNum(curPhotosNum);
			}
			break;
		case Constants.PHONE_PICTURES:// 图库
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> photos = data
						.getStringArrayListExtra("photos");
				for (String photo : photos) {
					editor.getThumbnails().add(prefix + photo);
				}
				editor.setCurPhotosNum(editor.getCurPhotosNum() + photos.size());
			}
			break;
		case Constants.DOODLE_BOARD:// 涂鸦
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("doodlePath");
				editor.getThumbnails().add(prefix + imgPath);
				editor.setCurPhotosNum(editor.getCurPhotosNum() + 1);
			}
			break;
		case Constants.HANDWRITE_BOARD:// 手写
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("handwritePath");
				editor.getThumbnails().add(prefix + imgPath);
				editor.setCurPhotosNum(editor.getCurPhotosNum() + 1);
			}
			break;
		default:
			break;
		}
		editor.getThumbnailsAdapter().notifyDataSetChanged();// 需要和数据更新在一个线程
		if (editor.getThumbnails().size() > 0)
			editor.getThumbnailsParentView().setVisibility(View.VISIBLE);
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 回复问题
	public void doReply() {
		editor.hideSoft();
		String rTextContent = editor.getContent();
		ArrayList<String> thumbnails = editor.getThumbnails();
		if ((rTextContent != null && rTextContent.length() > 0)
				|| thumbnails.size() > 0) {
			ArrayList<Resource> resources = new ArrayList<Resource>();
			for (String resourcePath : thumbnails) {
				resources.add(new Resource(0, resourcePath, resourcePath));
			}
			// if (path != null)
			// resources.add(new Resource(path));
			final long rUser = MyApplication.getLoginUser(this).getUserId();
			final long qId = question.getqId();
			final long rResource = ResourceDao.insertResouce(this, resources);
			Reply reply = new Reply(0, rTextContent, rResource, new Date(),
					qId, rUser, 0);
			reply.setResources(resources);
			// final long rId = ReplyDao.insertReply(this, reply);
			MyApplication.uploadReply(context, reply);
			editor.getThumbnailsParentView().setVisibility(View.INVISIBLE);
			editor.getExtrasView().setVisibility(View.GONE);
			editor.getThumbnails().clear();
			editor.clearContent();
			editor.setCurPhotosNum(0);
			goReply();
		} else {
			Toast.makeText(this, "没有发现回复内容。。。", Toast.LENGTH_SHORT).show();
		}
	}

	// 转到回复页面
	public void goReply() {
		Intent intent = new Intent(ReplyQuestionActivity.this,
				ReplysActiviy.class);
		intent.putExtra("quesId", question.getqId());
		startActivity(intent);
	}

	public void myOnclick(View view) {
		switch (view.getId()) {
		case R.id.reply_number:
			if (replyNum > 0) {
				editor.hideSoft();
				goReply();
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (editor.getThumbnails().size() > 0) {
			editor.getExtrasView().setVisibility(View.GONE);
			editor.getThumbnailsParentView().setVisibility(View.INVISIBLE);
			editor.getThumbnails().clear();
			editor.clearContent();
			editor.setCurPhotosNum(0);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		new GetReplysTask().execute(question.getqId());// 重新设置问题回复数目
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

}
