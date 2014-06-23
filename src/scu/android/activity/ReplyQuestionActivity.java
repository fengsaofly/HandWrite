package scu.android.activity;

import java.util.ArrayList;
import scu.android.base.CommonEditText;
import scu.android.db.ReplyDao;
import scu.android.entity.Question;
import scu.android.entity.Reply;
import scu.android.ui.PhotosAdapter;
import scu.android.util.AppUtils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.demo.note.R;

/*
 * 回复问题
 */
public class ReplyQuestionActivity extends Activity {
	private Question question;// 问题
	private ArrayList<Reply> replys;// 回复
	private CommonEditText commonEditText;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_reply);
		init();
	}

	public void init() {
		question = (Question) getIntent().getSerializableExtra("question");
		new GetReplysTask().execute();

		((TextView) findViewById(R.id.nickname)).setText("测试用户名");
		((TextView) findViewById(R.id.publish_time)).setText(AppUtils
				.timeToNow(question.getPublishTime()));
		((TextView) findViewById(R.id.title)).setText(question.getTitle());
		((TextView) findViewById(R.id.grade)).setText(question.getGrade());
		((TextView) findViewById(R.id.subject)).setText(question.getSubject());
		((TextView) findViewById(R.id.content)).setText(question.getContent());
		((TextView) findViewById(R.id.reply_number)).setText("获取中...");
		((GridView) findViewById(R.id.photos_view))
				.setAdapter(new PhotosAdapter(this, question.getImages()));
		ImageView avatar = (ImageView) findViewById(R.id.avatar);
		int width = AppUtils.getDefaultPhotoWidth(this) / 3;
		AppUtils.setViewSize(avatar, width, width);
		avatar.setBackgroundResource(R.drawable.avatar);

//		commonEditText = (CommonEditText) findViewById(R.id.common_edit_text);
		commonEditText.setActivity(this);
		commonEditText.setAction("scu.android.activiy.ReplyQuestionActivity");
		receiver = new PhotoReceiver();
		registerReceiver(receiver,
				new IntentFilter(AppUtils.SCAN_PHOTOS_ACTION));
	}

	/*
	 * 首次获取回复信息
	 */
	private class GetReplysTask extends AsyncTask<Void, Void, ArrayList<Reply>> {

		@Override
		protected ArrayList<Reply> doInBackground(Void... params) {
			ArrayList<Reply> replys = ReplyDao.getReply(
					ReplyQuestionActivity.this, question.getQuesId());
			return replys;
		}

		@Override
		protected void onPostExecute(ArrayList<Reply> result) {
			ReplyQuestionActivity.this.replys = result;
			((TextView) findViewById(R.id.reply_number)).setText("回答("
					+ replys.size() + ")");
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

	}

	private class PhotoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(AppUtils.SCAN_PHOTOS_ACTION)) {
				int index = intent.getIntExtra("photoIndex", 0);
				commonEditText.getThumbnails().remove(index);
				commonEditText.getThumbnailsAdapter().notifyDataSetChanged();
				commonEditText
						.setCurPhotosNum(commonEditText.getCurPhotosNum() - 1);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.reply_question_actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reply:
			String content = commonEditText.getContent();
			if (content != null && content.length() > 0) {
				String audio = null;
				Reply reply = new Reply(0, content, audio,
						commonEditText.getThumbnails(), null,
						question.getQuesId(), 0);
				long repId = ReplyDao.insertReply(this, reply);
				replys.add(0, ReplyDao.getReplyById(this, repId));
				commonEditText.getThumbnailsParentView().setVisibility(
						View.INVISIBLE);
				commonEditText.getThumbnails().clear();
				commonEditText.clearContent();
				commonEditText.setCurPhotosNum(0);
				reply();
			} else {
				Toast.makeText(this, "忘记输入内容啦。。。", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AppUtils.SYS_CAMEAR:// 相机
			if (resultCode == Activity.RESULT_OK) {
				commonEditText.getThumbnails().add(
						commonEditText.getCameraName());
				commonEditText
						.setCurPhotosNum(commonEditText.getCurPhotosNum() + 1);
			}
			break;
		case AppUtils.PHONE_PICTURES:// 图库
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> photos = data
						.getStringArrayListExtra("photos");
				commonEditText.getThumbnails().addAll(photos);
				commonEditText.setCurPhotosNum(commonEditText.getCurPhotosNum()
						+ photos.size());
			}
			break;
		case AppUtils.DOODLE_BOARD:// 涂鸦
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("doodlePath");
				commonEditText.getThumbnails().add(imgPath);
				commonEditText
						.setCurPhotosNum(commonEditText.getCurPhotosNum() + 1);
			}
			break;
		case AppUtils.HANDWRITE_BOARD:// 手写
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("handwritePath");
				commonEditText.getThumbnails().add(imgPath);
				commonEditText
						.setCurPhotosNum(commonEditText.getCurPhotosNum() + 1);
			}
			break;
		default:
			break;
		}
		commonEditText.getThumbnailsAdapter().notifyDataSetChanged();// 需要和数据更新在一个线程
		if (commonEditText.getThumbnailsParentView().getVisibility() == View.INVISIBLE)
			commonEditText.getThumbnailsParentView()
					.setVisibility(View.VISIBLE);
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 显示回复
	public void reply() {
		Intent intent = new Intent(ReplyQuestionActivity.this,
				ReplysActiviy.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("replys", replys);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void myOnclick(View view) {
		switch (view.getId()) {
		case R.id.reply_number:
			if (replys.size() > 0)
				reply();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

}
