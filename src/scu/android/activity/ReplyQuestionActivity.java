//package scu.android.activity;
//
//import java.util.ArrayList;
//import java.util.List;
//import scu.android.application.MyApplication;
//import scu.android.base.CommonEditor;
//import scu.android.dao.Question;
//import scu.android.dao.Reply;
//import scu.android.dao.Resource;
//import scu.android.dao.User;
//import scu.android.db.DBTools;
//import scu.android.util.ActivitySupport;
//import scu.android.util.AppUtils;
//import scu.android.util.Constants;
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.demo.note.R;
//import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.nostra13.universalimageloader.core.ImageLoader;
//
///**
// * 回复问题
// * 
// * @author YouMingyang
// * @version 1.0
// */
//public class ReplyQuestionActivity extends ActivitySupport {
//	private Context context;
//	private Question question;// 问题
//	private User user;
//	private Long replyNum;
//	private CommonEditor editor;
//	private BroadcastReceiver receiver;
//	private final String action = "scu.android.activiy.RelpyQuestionActivity";
//	private ImageLoader loader;
//	private DisplayImageOptions options;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.question_reply);
//		init();
//	}
//
//	public void init() {
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//		this.context = getApplicationContext();
//		initImageLoader();
//		question = (Question) getIntent().getSerializableExtra("question");
//		user = (User) getIntent().getSerializableExtra("user");
//		getActionBar().setTitle("问题详情");
//		((TextView) findViewById(R.id.nickname)).setText(user.getUser_nickname());
//		((TextView) findViewById(R.id.publish_time)).setText(AppUtils.timeToNow(question.getCreated_time()));
////		((TextView) findViewById(R.id.title)).setText(question.getQ_title());
//		((TextView) findViewById(R.id.grade)).setText(question.getQ_grade());
//		((TextView) findViewById(R.id.subject)).setText(question.getQ_subject());
//		((TextView) findViewById(R.id.content)).setText(question.getQ_text_content());
//		((TextView) findViewById(R.id.reply_number)).setText("获取中...");
////		GridView view = ((GridView) findViewById(R.id.photos_view));
//		List<Resource> mResources=DBTools.getInstance(context).loadResources(question.getQ_resource());
////		List<String> thumbnails = DBTools.getSImages(mResources);
////		List<String> images = DBTools.getLImages(mResources);
////		view.setAdapter(new PhotosAdapter(this, thumbnails, images));
//		ImageView avatar = (ImageView) findViewById(R.id.avatar);
//		// final int width = AppUtils.getDefaultPhotoWidth(this, 9);
//		// AppUtils.setViewSize(avatar, width, width);
//		loader.displayImage(user.getUser_avatar(), avatar, options);
//		editor = (CommonEditor) findViewById(R.id.common_editor);
//		editor.setAction(action);
//		editor.setActivity(this);
//
//		receiver = new PhotoReceiver();
//		registerReceiver(receiver, new IntentFilter(action));
//	}
//
//	public void initImageLoader(){
//		loader = ImageLoader.getInstance();
//		options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.default_avatar)
//				.showImageForEmptyUri(R.drawable.default_avatar)
//				.showImageOnFail(R.drawable.default_avatar).cacheInMemory(true)
//				.cacheOnDisk(true).considerExifParams(true)
//				.bitmapConfig(Bitmap.Config.RGB_565).build();
//	}
//	
//	/*
//	 * 获取问题的回复数目
//	 */
//	private class GetReplysTask extends AsyncTask<Long, Void, Long> {
//
//		@Override
//		protected Long doInBackground(Long... params) {
//			long replyNum = DBTools.getInstance(context).getReplysNum(question.getQ_id());
//			return replyNum;
//		}
//
//		@Override
//		protected void onPostExecute(Long result) {
//			replyNum = result;
//			((TextView) findViewById(R.id.reply_number)).setText("回答("+ replyNum + ")");
//			super.onPostExecute(result);
//		}
//
//	}
//
//	/*
//	 * 获取图片删除信息
//	 */
//	private class PhotoReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(action)) {
//				int index = intent.getIntExtra("photoIndex", 10);
//				if (index != 10) {
//					editor.getThumbnails().remove(index);
//					editor.getThumbnailsAdapter().notifyDataSetChanged();
//					int curPhotosNum = editor.getCurPhotosNum() - 1;
//					editor.setCurPhotosNum(curPhotosNum);
//					if (curPhotosNum == 0) {
//						editor.getThumbnailsParentView().setVisibility(
//								View.INVISIBLE);
//						editor.getExtrasView().setVisibility(View.GONE);
//					}
//				} else {
//					doReply();
//				}
//			}
//		}
//	}
//
//	@Override
//	public boolean onMenuItemSelected(int featureId, MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			finish();
//			break;
//		case R.id.reply:
//			doReply();
//			break;
//		}
//		return true;
//	}
//
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		final String prefix = "file:///";
//		switch (requestCode) {
//		case Constants.SYS_CAMEAR:// 相机
//			if (resultCode == Activity.RESULT_OK) {
//				String imgPath = editor.getCameraName();
//				int curPhotosNum = editor.getCurPhotosNum() + 1;
//				editor.getThumbnails().add(prefix + imgPath);
//				editor.setCurPhotosNum(curPhotosNum);
//			}
//			break;
//		case Constants.PHONE_PICTURES:// 图库
//			if (resultCode == Activity.RESULT_OK) {
//				ArrayList<String> photos = data.getStringArrayListExtra("photos");
//				for (String photo : photos) {
//					editor.getThumbnails().add(prefix + photo);
//				}
//				editor.setCurPhotosNum(editor.getCurPhotosNum() + photos.size());
//			}
//			break;
//		case Constants.DOODLE_BOARD:// 涂鸦
//			if (resultCode == Activity.RESULT_OK) {
//				String imgPath = data.getStringExtra("doodlePath");
//				editor.getThumbnails().add(prefix + imgPath);
//				editor.setCurPhotosNum(editor.getCurPhotosNum() + 1);
//			}
//			break;
//		case Constants.HANDWRITE_BOARD:// 手写
//			if (resultCode == Activity.RESULT_OK) {
//				String imgPath = data.getStringExtra("handwritePath");
//				editor.getThumbnails().add(prefix + imgPath);
//				editor.setCurPhotosNum(editor.getCurPhotosNum() + 1);
//			}
//			break;
//		default:
//			break;
//		}
//		editor.getThumbnailsAdapter().notifyDataSetChanged();// 需要和数据更新在一个线程
//		if (editor.getThumbnails().size() > 0)
//			editor.getThumbnailsParentView().setVisibility(View.VISIBLE);
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	// 回复问题
//	public void doReply() {
//		editor.hideSoft();
//		final String qr_text = editor.getContent();
//		final long qr_user = MyApplication.getCurrentUser(context).getUser_id();
//		ArrayList<String> thumbnails = editor.getThumbnails();
//		if ((qr_text != null && qr_text.length() > 0) || thumbnails.size() > 0) {
//			List<Resource> resources = new ArrayList<Resource>();
//			for (String resourcePath : thumbnails) {
//				resources.add(new Resource(null, null, resourcePath,resourcePath));
//			}
//			final String path=editor.getAudioPath();
//			if (path != null)
//				resources.add(new Resource(null,null,null,path));
//			final long qr_q = question.getQ_id();
//			long qr_resource = -1;
//			if (resources.size() > 0) {
//				qr_resource = DBTools.getInstance(context).insertResources(resources);
//				MyApplication.oldResourceId = qr_resource;
//			}
//			Reply reply = new Reply(null, null, qr_text,System.currentTimeMillis() / 1000, (short) 0, qr_user,qr_q, qr_resource);
//			final long rId = DBTools.getInstance(context).insertReply(reply);
//			MyApplication.oldId = rId;
//			MyApplication.uploadReply(context, reply);// 上传回复
//
//			editor.getThumbnailsParentView().setVisibility(View.INVISIBLE);
//			editor.getExtrasView().setVisibility(View.GONE);
//			editor.getThumbnails().clear();
//			editor.clearContent();
//			editor.setCurPhotosNum(0);
//			goReply();
//		} else {
//			showToast( "没有发现回复内容。。。");
//		}
//	}
//
//	// 转到回复页面
//	public void goReply() {
//		Intent intent = new Intent(ReplyQuestionActivity.this,ReplysActiviy.class);
//		Bundle bundle = new Bundle();
//		bundle.putSerializable("question", question);
//		intent.putExtras(bundle);
//		startActivity(intent);
//	}
//
//	public void myOnclick(View view) {
//		switch (view.getId()) {
//		case R.id.reply_number:
////			if (replyNum > 0) {
//			editor.hideSoft();
//			goReply();
////			}
//			break;
//		}
//	}
//
//	@Override
//	public void onBackPressed() {
//		if (editor.getThumbnails().size() > 0) {
//			editor.getExtrasView().setVisibility(View.GONE);
//			editor.getThumbnailsParentView().setVisibility(View.INVISIBLE);
//			editor.getThumbnails().clear();
//			editor.clearContent();
//			editor.setCurPhotosNum(0);
//		} else {
//			super.onBackPressed();
//		}
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		new GetReplysTask().execute(question.getQ_id());// 重新设置问题回复数目
//	}
//
//	@Override
//	protected void onDestroy() {
//		unregisterReceiver(receiver);
//		super.onDestroy();
//	}
//
//}
