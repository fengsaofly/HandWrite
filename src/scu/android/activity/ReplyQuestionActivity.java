package scu.android.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import scu.android.db.ReplyDao;
import scu.android.entity.Question;
import scu.android.entity.Reply;
import scu.android.util.AppUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/*
 * 回复问题
 */
public class ReplyQuestionActivity extends Activity {

	private Question question;
	private ArrayList<Reply> replys;

	// 图片相关
	private ImageLoader loader;
	private DisplayImageOptions options;
	private int photoWidth;

	// 弹出
	private View questionPopup;
	
	private PhotosAdapter replyAdapter;
	private ArrayList<String> replyPhotos;

	private String imgName;
	private final int MAX_NUMBER = 6;
	private int selectNativePhotosNumber;// 选择图库图片数目

	private PopupWindow replyExtrasWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_reply);
		init();
	}

	public void init() {
		question = (Question) getIntent().getSerializableExtra("question");

		photoWidth = (AppUtils.getWindowMetrics(this).widthPixels - 4) / 3;
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.question)
				.showImageForEmptyUri(R.drawable.question).cacheInMemory()
				.cacheOnDisc().imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.build();

		ImageView avatar = (ImageView) findViewById(R.id.avatar);
		LayoutParams params = avatar.getLayoutParams();
		params.width = params.height = photoWidth / 3;
		avatar.setLayoutParams(params);
		avatar.setBackgroundResource(R.drawable.avatar);
		((TextView) findViewById(R.id.nickname)).setText("测试用户名");
		((TextView) findViewById(R.id.publish_time)).setText(AppUtils
				.timeToNow(question.getPublishTime()));
		((TextView) findViewById(R.id.title)).setText(question.getTitle());

		replys = ReplyDao.getReply(this, question.getQuesId());
		((TextView) findViewById(R.id.reply_number)).setText("回答("
				+ replys.size() + ")");

		((TextView) findViewById(R.id.grade)).setText(question.getGrade());
		((TextView) findViewById(R.id.subject)).setText(question.getSubject());
		((TextView) findViewById(R.id.content)).setText(question.getContent());
		((GridView) findViewById(R.id.photos_view))
				.setAdapter(new PhotosAdapter(this, question.getImages()));
		//
		questionPopup = (View) findViewById(R.id.question_popup_layout);
//		replyPhotosView = (GridView) findViewById(R.id.reply_photos_view);
		replyPhotos = new ArrayList<String>();
		replyAdapter = new PhotosAdapter(this, replyPhotos);
//		replyPhotosView.setAdapter(replyAdapter);
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
			String content = ((TextView) findViewById(R.id.chat_input_field))
					.getText().toString().trim();
			String audio = null;
			Reply reply = new Reply(0, content, audio, replyPhotos, null,
					question.getQuesId(), 0);
			ReplyDao.insertReply(this, reply);
			reply.setReplyTime(new Date());// this replyTime maybe different
											// from the web
			replys.add(0, reply);
			reply();
			break;
		}
		return true;
	}

	private class PhotosAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<String> bitmaps;

		public PhotosAdapter(Context context, ArrayList<String> bitmaps) {
			this.context = context;
			this.bitmaps = bitmaps;
		}

		public int getCount() {
			return bitmaps.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.thumbnail_item, null);
			}
			String photo = bitmaps.get(position);
			ImageView thumbnail = ((ImageView) convertView
					.findViewById(R.id.thumbnail));
			android.view.ViewGroup.LayoutParams params = thumbnail
					.getLayoutParams();
			params.width = params.height = photoWidth;
			thumbnail.setLayoutParams(params);// 设置图片大小
			loader.displayImage("file:///" + photo, thumbnail, options);
			final int index = position;
			thumbnail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ReplyQuestionActivity.this,
							ScanPhotosActivity.class);
					intent.putStringArrayListExtra("photos", bitmaps);
					intent.putExtra("index", index + 1);
					startActivity(intent);
				}
			});
			return convertView;
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AppUtils.SYS_CAMEAR:// 相机
			if (resultCode == Activity.RESULT_OK) {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					File file = new File(
							Environment.getExternalStorageDirectory() + "/"
									+ AppUtils.CAMERA_PHOTO_DIR + "/" + imgName);
					String imgPath = file.getAbsolutePath();
					replyPhotos.add(imgPath);
				}
			}
			break;
		case AppUtils.PHONE_PICTURES:// 图库
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> photos = data
						.getStringArrayListExtra("photos");
				for (String imgPath : photos) {
					replyPhotos.add(imgPath);
					++selectNativePhotosNumber;
				}
			}
			break;
		case AppUtils.DOODLE_BOARD:// 涂鸦
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("doodlePath");
				replyPhotos.add(imgPath);
			}
			break;
		case AppUtils.HANDWRITE_BOARD:// 手写
			if (resultCode == Activity.RESULT_OK) {
				String imgPath = data.getStringExtra("handwritePath");
				replyPhotos.add(imgPath);
			}
			break;
		default:
			break;
		}
		replyAdapter.notifyDataSetChanged();// 需要和数据更新在一个线程
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
		case R.id.chat_add_btn:
			int visible = questionPopup.getVisibility();
			if (visible == View.GONE) {
				questionPopup.setVisibility(View.VISIBLE);
				disReplyExtras();
			} else {
				questionPopup.setVisibility(View.GONE);
			}
			break;
		case R.id.popup_imgpicker_lay:
			if (selectNativePhotosNumber < (MAX_NUMBER - 2)) {
				int availNumber = MAX_NUMBER - 2 - selectNativePhotosNumber;
				AppUtils.phonePictures(this, availNumber);
			} else {
				Toast.makeText(this, "最多只能选择" + (MAX_NUMBER - 2) + "张图片",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.popup_handwrite_lay:
			AppUtils.hwBoard(this);
			break;
		case R.id.popup_camera_lay:
			imgName = AppUtils.sysCamera(this);
			break;
		case R.id.popup_recode_lay:
			AppUtils.doodleBoard(this);
			break;
		}
	}

	// 显示回复图片和语言
	public void disReplyExtras() {
		View contentView=getLayoutInflater().inflate(R.layout.question_reply_window, null);
		GridView replyPhotosView=(GridView)contentView.findViewById(R.id.reply_photos_view);
		replyPhotosView.setAdapter(replyAdapter);
		replyExtrasWindow=new PopupWindow(contentView, android.support.v4.view.ViewPager.LayoutParams.MATCH_PARENT, AppUtils.getWindowMetrics(this).heightPixels-questionPopup.getMeasuredHeight(),true);
		replyExtrasWindow.setBackgroundDrawable(new ColorDrawable());
		contentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (replyExtrasWindow.isShowing()) {
					replyExtrasWindow.dismiss();
				}
				return false;
			}
		});
		replyExtrasWindow.showAtLocation(questionPopup,Gravity.BOTTOM,0,0);
	}

}
