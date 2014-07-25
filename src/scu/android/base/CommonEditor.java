package scu.android.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import scu.android.application.MyApplication;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

/**
 * 回复问题输入控件 ，弃用
 */
public class CommonEditor extends LinearLayout implements OnClickListener {

	private Activity activity;// 所属Activiy
	private View contentView;//

	private TextView addRecord;
	private EditText inputField;
	private TextView addExtras;
	private TextView chatSend;

	private View extrasView;// 其他
	private View addDoodle;
	private View addHWrite;
	private View addPhotos;
	private View addCamera;

	private View thumbnailsParentView;
	// private GridView thumbnailsView;
	// private PhotosAdapter thumbnailsAdapter;
	private ArrayList<String> thumbnails;
	private String audioPath;

	private ImageView thumbnail;

	private String cameraName;
	private String action;
	private int maxPhotosNum;
	private int curPhotosNum;

	public CommonEditor(Context context) {
		super(context);
		init(context);
	}

	public CommonEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CommonEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		contentView = inflater.inflate(R.layout.find_input_field, this);
		addRecord = (TextView) contentView.findViewById(R.id.add_record);
		addExtras = (TextView) contentView.findViewById(R.id.add_extras);
		inputField = (EditText) contentView.findViewById(R.id.input_field);
		chatSend = (TextView) contentView.findViewById(R.id.chat_send);
		extrasView = contentView.findViewById(R.id.question_popup_layout);
		addDoodle = extrasView.findViewById(R.id.popup_doodle_lay);
		addCamera = extrasView.findViewById(R.id.popup_camera_lay);
		addPhotos = extrasView.findViewById(R.id.popup_imgpicker_lay);
		addHWrite = extrasView.findViewById(R.id.popup_handwrite_lay);
		addExtras.setOnClickListener(this);
		inputField.setOnClickListener(this);
		chatSend.setOnClickListener(this);
		addDoodle.setOnClickListener(this);
		addCamera.setOnClickListener(this);
		addPhotos.setOnClickListener(this);
		addHWrite.setOnClickListener(this);
		maxPhotosNum = Constants.MAX_PHOTOS_NUM;
		// intialRecord();
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		thumbnailsParentView = contentView.findViewById(R.id.thumbnails_parent_view);
		// thumbnailsView = (GridView)
		// contentView.findViewById(R.id.thumbnails_view);
		thumbnails = new ArrayList<String>();
		// thumbnailsAdapter = new PhotosAdapter(activity, thumbnails);
		// thumbnailsAdapter.setColumnNum(4);
		// thumbnailsAdapter.setAction(action);
		// thumbnailsView.setAdapter(thumbnailsAdapter);
		thumbnail = (ImageView) contentView.findViewById(R.id.thumbnail);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.input_field:
			if (extrasView.getVisibility() == View.VISIBLE)
				extrasView.setVisibility(View.GONE);
			break;
		case R.id.add_extras:
			setExtrasViewVisibility();
			break;
		case R.id.chat_send:
			Intent intent = new Intent();
			intent.setAction(action);
			activity.sendBroadcast(intent);
			break;
		case R.id.popup_doodle_lay:
			if (curPhotosNum < (maxPhotosNum)) {
				AppUtils.doodleBoard(activity);
			} else {
				alertFull();
			}
			break;
		case R.id.popup_camera_lay:
			if (curPhotosNum < (maxPhotosNum)) {
				cameraName = AppUtils.sysCamera(activity);
			} else {
				alertFull();
			}
			break;
		case R.id.popup_imgpicker_lay:
			if (curPhotosNum < (maxPhotosNum)) {
				int availNumber = maxPhotosNum - curPhotosNum;
				AppUtils.phonePictures(activity, availNumber);
			} else {
				alertFull();
			}
			break;
		case R.id.popup_handwrite_lay:
			if (curPhotosNum < (maxPhotosNum)) {
				AppUtils.hwBoard(activity);
			} else {
				alertFull();
			}
			break;
		}
	}

	public void disableAddExtras() {
		addExtras.setVisibility(View.GONE);
	}

	public void setExtrasViewVisibility() {
		if (extrasView.getVisibility() == View.VISIBLE) {
			extrasView.setVisibility(View.GONE);
		} else {
			extrasView.setVisibility(View.VISIBLE);
		}
		// hideSoft();
	}

	public void hideSoft() {
		// 隐藏软键盘
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputField.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void setMaxPhotosNum(int maxPhotosNum) {
		this.maxPhotosNum = maxPhotosNum;
	}

	public int getCurPhotosNum() {
		return curPhotosNum;
	}

	public void setCurPhotosNum(int curPhotosNum) {
		this.curPhotosNum = curPhotosNum;
	}

	/**
	 * 获取选择的图片
	 */
	public ArrayList<String> getThumbnails() {
		return thumbnails;
	}

	// public PhotosAdapter getThumbnailsAdapter() {
	// return thumbnailsAdapter;
	// }
	//
	// public GridView getThumbnailsView() {
	// return thumbnailsView;
	// }

	public View getThumbnailsParentView() {
		return thumbnailsParentView;
	}

	public View getExtrasView() {
		return extrasView;
	}

	public void setExtrasView(View extrasView) {
		this.extrasView = extrasView;
	}

	public String getContent() {
		return inputField.getText().toString();
	}

	public void clearContent() {
		inputField.setText(null);
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return this.action;
	}

	public void focus() {
		inputField.findFocus();
	}

	public void alertFull() {
		Toast.makeText(activity, "最多只能选择" + maxPhotosNum + "张图片", Toast.LENGTH_SHORT).show();
	}

	TextView second = null;
	TextView testInclude = null;
	MediaRecorder mr = null;
	MediaPlayer voiceMp = null;
	PopupWindow pop;
	View view, view2;
	AnimationDrawable animaition;
	private long startVoiceT, endVoiceT;
	LinearLayout rcChat_popup = null, yourVocie_lay = null;
	RelativeLayout play_lay = null;
	private Handler mHandler;
	boolean recordFlag = false;

	ImageView volume, question_add_showrecord_imgview;

	public void intialRecord() {

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				System.out.println("进入handler");
				switch (msg.what) {
				case 0:
				case 1:
					testInclude.setText("1");
					volume.setImageResource(R.drawable.amp1);
					break;
				case 2:
				case 3:
					testInclude.setText("2");
					volume.setImageResource(R.drawable.amp2);

					break;
				case 4:
				case 5:
					testInclude.setText("3");
					volume.setImageResource(R.drawable.amp3);
					break;
				case 6:
				case 7:
					testInclude.setText("4");
					volume.setImageResource(R.drawable.amp4);
					break;
				case 8:
				case 9:
					testInclude.setText("5");
					volume.setImageResource(R.drawable.amp5);
					break;
				case 10:
				case 11:
					testInclude.setText("6");
					volume.setImageResource(R.drawable.amp6);
					break;
				default:
					testInclude.setText("7");
					volume.setImageResource(R.drawable.amp7);
					break;
				}
				super.handleMessage(msg);
			}

		};

		rcChat_popup = (LinearLayout) findViewById(R.id.rcChat_popup);
		volume = (ImageView) findViewById(R.id.volume2);
		testInclude = (TextView) findViewById(R.id.testInclude);
		play_lay = (RelativeLayout) findViewById(R.id.play_lay);
		yourVocie_lay = (LinearLayout) findViewById(R.id.yourVocie_lay);
		second = (TextView) findViewById(R.id.volume);
		question_add_showrecord_imgview = (ImageView) findViewById(R.id.question_add_showrecord_imgview);
		// 创建PopupWindow对象

		pop = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);

		// // 需要设置一下此参数，点击外边可消失
		//
		pop.setBackgroundDrawable(new ColorDrawable());

		// 设置点击窗口外边窗口消失

		pop.setOutsideTouchable(true);

		// 设置此参数获得焦点，否则无法点击

		pop.setFocusable(true);
		addRecord.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// TODO Auto-generated method stub
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					thumbnailsParentView.setVisibility(View.VISIBLE);
					System.out.println("按下录音。。。");
					rcChat_popup.setVisibility(View.VISIBLE);
					startVoiceT = System.currentTimeMillis();
					// 按住事件发生后执行代码的区域
					mr = new MediaRecorder();
					File file2 = new File(MyApplication.getSDCardPath() + "/ConquerQuestion" + "/" + MyApplication.getCurrentUser(getContext()).getUser_name() + "/" + "Audio");
					if (!file2.exists())
						file2.mkdirs();
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
					String time = df.format(new Date());

					File file = new File(file2.getAbsolutePath() + "/" + time + ".amr");
					audioPath = file.getAbsolutePath();
					mr.setAudioSource(MediaRecorder.AudioSource.MIC);
					mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					// 设置输出文件
					mr.setOutputFile(file.getAbsolutePath());
					try {
						// 创建文件
						file.createNewFile();
						// 准备录制
						mr.prepare();
						mr.start();
						start();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;

				case MotionEvent.ACTION_MOVE:
					break;

				case MotionEvent.ACTION_UP:
					System.out.println("s松开录音。。。");
					endVoiceT = System.currentTimeMillis();
					if (mr != null) {
						mr.stop();
						mr.release();
						mr = null;
					}
					stop();
					int seconds = (int) (endVoiceT - startVoiceT) / 1000;
					if (seconds < 1) {
						seconds = 1;
					}
					yourVocie_lay.setVisibility(View.VISIBLE);
					second.setText("" + seconds);

					rcChat_popup.setVisibility(View.GONE);
					Toast.makeText(getContext().getApplicationContext(), "录音完毕", Toast.LENGTH_LONG).show();
					// play.setVisibility(View.VISIBLE);
					recordFlag = true;

					// 松开事件发生后执行代码的区域
					break;

				default:

					break;
				}
				return true;
			}
		});

		play_lay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					// voiceMp = new MediaPlayer();
					if (voiceMp != null) {
						voiceMp.reset();
						voiceMp.release();
						voiceMp = null;
					}
					voiceMp = new MediaPlayer();
					voiceMp.setDataSource(audioPath);
					voiceMp.prepare();
					voiceMp.setLooping(false);
					voiceMp.start();
					voiceMp.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer arg0) {
							// TODO Auto-generated method stub
							voiceMp.release();
							voiceMp = null;
							animaition.stop();
						}
					});
					question_add_showrecord_imgview.setImageResource(R.anim.playvoice);
					animaition = (AnimationDrawable) question_add_showrecord_imgview.getDrawable();
					// 最后，就可以启动动画了，代码如下：

					// 是否仅仅启动一次？

					animaition.setOneShot(false);

					if (animaition.isRunning())// 是否正在运行？

					{
						animaition.stop();// 停止

					}
					animaition.start();// 启动
				} catch (IllegalStateException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};

	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mr.getMaxAmplitude() / 2700.0;
			System.out.println("amp: " + amp);
			// updateDisplay(amp);
			Message msg = mHandler.obtainMessage();
			msg.what = (int) amp;
			mHandler.sendMessage(msg);

			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start() {
		// mr.start();
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		// mr.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	public String getAudioPath() {
		return audioPath;
	}

}
