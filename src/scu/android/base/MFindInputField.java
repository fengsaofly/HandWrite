package scu.android.base;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import scu.android.application.MyApplication;
import scu.android.util.AppUtils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MFindInputField extends LinearLayout {

	private Activity activity;
	private String action;
	private View view;
	private ViewPager extras;
	private View thumbnailPopupLayout;
	private View voicePopupLayout;

	private LayoutInflater inflater;
	private ImageLoader loader;
	private DisplayImageOptions options;

	private String imgUri = null;

	public MFindInputField(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.find_input_field, this);
		extras = (ViewPager) view.findViewById(R.id.extras);
		extras.setAdapter(new MPagerAdapter());
		initControls();
	}

	public void init(Activity activity, String action) {
		this.activity = activity;
		this.action = action;
		initImageLoader();
		initThumbnailPopupLayout();
	}

	public void initControls() {
		findViewById(R.id.add_record).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeInput();
				thumbnailPopupLayout.setVisibility(View.GONE);
				if (extras.getVisibility() == View.VISIBLE && extras.getCurrentItem() == 1) {
					extras.setVisibility(View.GONE);
				} else {
					extras.setVisibility(View.VISIBLE);
					extras.setCurrentItem(1);
				}
			}
		});
		findViewById(R.id.add_extras).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closeInput();
				thumbnailPopupLayout.setVisibility(View.GONE);
				if (extras.getVisibility() == View.VISIBLE && extras.getCurrentItem() == 0) {
					extras.setVisibility(View.GONE);
				} else {
					extras.setVisibility(View.VISIBLE);
					extras.setCurrentItem(0);
				}
			}
		});
		findViewById(R.id.input_field).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				extras.setVisibility(View.GONE);
				thumbnailPopupLayout.setVisibility(View.GONE);
				return false;
			}
		});
		findViewById(R.id.chat_send).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.sendBroadcast(new Intent(action));
			}
		});
	}

	public void initImageLoader() {
		loader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public void closeInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null && activity.getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public View getQuestionPopupLayout() {
		View layout = inflater.inflate(R.layout.question_popup_layout_withoutrecord, null);
		layout.findViewById(R.id.popup_doodle_lay).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imgUri == null) {
					AppUtils.doodleBoard(activity);
				} else {
					extras.setVisibility(View.GONE);
					thumbnailPopupLayout.setVisibility(View.VISIBLE);
				}
			}
		});
		layout.findViewById(R.id.popup_camera_lay).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imgUri == null) {
					imgUri = AppUtils.sysCamera(activity);
				} else {
					extras.setVisibility(View.GONE);
					thumbnailPopupLayout.setVisibility(View.VISIBLE);
				}
			}
		});
		layout.findViewById(R.id.popup_imgpicker_lay).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imgUri == null) {
					AppUtils.phonePictures(activity, 1);
				} else {
					extras.setVisibility(View.GONE);
					thumbnailPopupLayout.setVisibility(View.VISIBLE);
				}
			}
		});
		layout.findViewById(R.id.popup_handwrite_lay).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imgUri == null) {
					AppUtils.hwBoard(activity);
				} else {
					extras.setVisibility(View.GONE);
					thumbnailPopupLayout.setVisibility(View.VISIBLE);
				}
			}
		});
		return layout;
	}

	class MRecorder {
		private MediaRecorder recorder;
		private MediaPlayer player;
		private Handler handler;
		private String audio;
		private boolean isPlaying;
		private boolean isPause;
		private long start;
		private long end;

		public MRecorder(Handler handler) {
			this.handler = handler;
		}

		public File generateFile() {
			File file = new File(MyApplication.getSDCardPath() + "/ConquerQuestion" + "/" + MyApplication.getCurrentUser(getContext()).getUser_name() + "/" + "Audio");
			if (!file.exists()) {
				file.mkdirs();
			}
			String audioName = System.currentTimeMillis() + ".amr";
			File audioFile = new File(file, audioName);
			return audioFile;
		}

		public void initRecorder() {
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			File audioFile = generateFile();
			audio = audioFile.getAbsolutePath();
			recorder.setOutputFile(audio);
			try {
				audioFile.createNewFile();
				recorder.prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
			recorder.start();
			start = System.currentTimeMillis();
		}

		public int getVolumn() {
			if (recorder != null) {
				return recorder.getMaxAmplitude() / 2700;
			}
			return new Random().nextInt(7);
		}

		public void initPlayer() {
			if (player != null) {
				player.release();
				player = null;
			}
			player = new MediaPlayer();
			player.reset();
			player.setLooping(false);
			try {
				player.setDataSource(audio);
				player.prepare();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			player.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					isPlaying = false;
					isPause = false;
					mp.release();
					player = null;
					handler.sendEmptyMessage(0x1234);
				}
			});
		}

		public void play() {
			isPlaying = true;
			isPause = false;
			if (player == null) {
				initPlayer();
			}
			player.start();
		}

		public void pause() {
			isPlaying = false;
			isPause = true;
			player.pause();
		}

		public void stopRecord() {
			end = System.currentTimeMillis();
			// try{
			// recorder.stop();
			// }catch(Exception e){
			//
			// }
		}

		public void resetRecorder() {
			if (recorder != null) {
				recorder.stop();
				recorder.release();
				recorder = null;
			}
			initPlayer();
		}

		public void reset() {
			audio = null;
		}

		public int getLength() {
			return (int) ((end - start) / 1000);
		}

		public String getAudio() {
			return audio;
		}

		public boolean shouldReset() {
			return recorder != null;
		}

		public boolean isPlaying() {
			return isPlaying;
		}

		public boolean isPause() {
			return isPause;
		}

	}

	private MRecorder mRecorder = null;
	private Runnable update = null;

	public View getVoicePopupLayout() {
		voicePopupLayout = inflater.inflate(R.layout.voice_popup_layout, null);
		final ImageView record = (ImageView) voicePopupLayout.findViewById(R.id.record);
		final TextView alterInfo = (TextView) voicePopupLayout.findViewById(R.id.alter_info);
		final TextView recordAgain = (TextView) voicePopupLayout.findViewById(R.id.record_again);
		final ImageView left = (ImageView) voicePopupLayout.findViewById(R.id.find_audioplay_left);
		final ImageView right = (ImageView) voicePopupLayout.findViewById(R.id.find_audioplay_right);
		final int[] leftDrawbles = new int[] { R.drawable.btn_posts_record_left_zero, R.drawable.btn_posts_record_left_one, R.drawable.btn_posts_record_left_two,
				R.drawable.btn_posts_record_left_three, R.drawable.btn_posts_record_left_four, R.drawable.btn_posts_record_left_five, R.drawable.btn_posts_record_left_six };
		final int[] rightDrawbles = new int[] { R.drawable.btn_posts_record_right_zero, R.drawable.btn_posts_record_right_one, R.drawable.btn_posts_record_right_two,
				R.drawable.btn_posts_record_right_three, R.drawable.btn_posts_record_right_four, R.drawable.btn_posts_record_right_five, R.drawable.btn_posts_record_right_six };

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0x1234:
					record.setImageResource(R.drawable.btn_posts_record_play_s);
					left.setBackgroundResource(leftDrawbles[0]);
					right.setBackgroundResource(rightDrawbles[0]);
					recordAgain.setEnabled(true);
					alterInfo.setText("点击播放");
					if (update != null)
						removeCallbacks(update);
					break;
				default:
					int index = msg.what >= 6 ? 6 : msg.what;
					left.setBackgroundResource(leftDrawbles[index]);
					right.setBackgroundResource(rightDrawbles[index]);
					break;
				}
			}

		};

		update = new Runnable() {
			public void run() {
				int volumn = mRecorder.getVolumn();
				handler.sendEmptyMessage(volumn);
				handler.postDelayed(this, 300);
			}
		};

		mRecorder = new MRecorder(handler);

		record.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mRecorder.getAudio() == null) {
						extras.requestDisallowInterceptTouchEvent(true);//禁止viewpager滑动
						record.setImageResource(R.drawable.btn_posts_record_zero_s);
						alterInfo.setText("松开保存录音");
						mRecorder.initRecorder();
						handler.postDelayed(update, 300);
					} else {
						if (mRecorder.isPlaying()) {
							record.setImageResource(R.drawable.btn_posts_record_play_s);
							alterInfo.setText("点击播放");
							mRecorder.pause();
							handler.removeCallbacks(update);
							recordAgain.setEnabled(true);
						} else {
							record.setImageResource(R.drawable.btn_posts_record_stop_s);
							alterInfo.setText("点击暂停");
							recordAgain.setEnabled(false);
							mRecorder.play();
							handler.postDelayed(update, 300);
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					if (!mRecorder.isPlaying() && !mRecorder.isPause()) {
						record.setImageResource(R.drawable.btn_posts_record_play_s);
						alterInfo.setText("点击播放");
						if (mRecorder.shouldReset()) {
							mRecorder.stopRecord();
							extras.requestDisallowInterceptTouchEvent(false);//允许滑动
							handler.removeCallbacks(update);//停止动画
							left.setBackgroundResource(leftDrawbles[0]);
							right.setBackgroundResource(rightDrawbles[0]);
							if (mRecorder.getLength() <= 0) {
								Toast.makeText(activity, "时间过短，请重录", Toast.LENGTH_SHORT).show();
								record.setImageResource(R.drawable.btn_posts_record_zero_n);
								alterInfo.setText("长按开始录音");
								mRecorder.reset();
							} else {
								view.findViewById(R.id.has_record).setVisibility(View.VISIBLE);
								recordAgain.setVisibility(View.VISIBLE);
								mRecorder.resetRecorder();
								recordAgain.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										record.setImageResource(R.drawable.btn_posts_record_zero_n);
										alterInfo.setText("长按开始录音");
										recordAgain.setVisibility(View.GONE);
										mRecorder.reset();
										findViewById(R.id.has_record).setVisibility(View.GONE);
									}
								});
							}
						}
					}
					break;
				}
				return true;
			}
		});
		return voicePopupLayout;
	}

	public void initThumbnailPopupLayout() {
		thumbnailPopupLayout = view.findViewById(R.id.thumbnail_popup_layout);
		final ImageView thumbnail = (ImageView) thumbnailPopupLayout.findViewById(R.id.thumbnail);
		thumbnailPopupLayout.findViewById(R.id.del).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				thumbnailPopupLayout.setVisibility(View.GONE);
				thumbnail.setImageBitmap(null);
				MFindInputField.this.imgUri = null;
				view.findViewById(R.id.has_img).setVisibility(View.GONE);
				extras.setVisibility(View.VISIBLE);
				extras.setCurrentItem(0);
			}
		});

	}
	
	class MPagerAdapter extends PagerAdapter {

		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Object instantiateItem(View view, int position) {
			View layout = null;
			switch (position) {
			case 0:
				layout = getQuestionPopupLayout();
				break;
			case 1:
				layout = getVoicePopupLayout();
				break;
			}
			((ViewPager) view).addView(layout, 0);
			return layout;
		}

		
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
	}

	/**
	 * 显示已选择的图片
	 * 
	 * @param imgUri
	 */
	public void setImg(final String imgUri) {
		extras.setVisibility(View.GONE);
		thumbnailPopupLayout.setVisibility(View.VISIBLE);
		final ImageView thumbnail = (ImageView) thumbnailPopupLayout.findViewById(R.id.thumbnail);
		this.imgUri = imgUri;
		AppUtils.disImg(activity.getApplicationContext(), loader, options, thumbnail, imgUri, imgUri);
		view.findViewById(R.id.has_img).setVisibility(View.VISIBLE);
	}

	/**
	 * 重置至默认显示
	 */
	public void reset() {
		closeInput();
		((TextView) view.findViewById(R.id.input_field)).setText(null);
		((ImageView) thumbnailPopupLayout.findViewById(R.id.thumbnail)).setImageBitmap(null);
		thumbnailPopupLayout.setVisibility(View.GONE);
		view.findViewById(R.id.has_img).setVisibility(View.GONE);
		view.findViewById(R.id.has_record).setVisibility(View.GONE);
		imgUri = null;
		((ImageView) voicePopupLayout.findViewById(R.id.record)).setImageResource(R.drawable.btn_posts_record_zero_n);
		((TextView) voicePopupLayout.findViewById(R.id.alter_info)).setText("长按开始录音");
		voicePopupLayout.findViewById(R.id.record_again).setVisibility(View.GONE);
		mRecorder.reset();
		findViewById(R.id.has_record).setVisibility(View.GONE);
		mRecorder.reset();
		extras.setVisibility(View.GONE);
	}

	
	
	public void setImgUri(String imgUri) {
		this.imgUri = imgUri;
	}

	public String getImgUri() {
		return this.imgUri;
	}

	public String getText() {
		return ((TextView) findViewById(R.id.input_field)).getText().toString().trim();
	}

	public String getAudio() {
		return mRecorder.getAudio();
	}

	public void enableExtras(boolean enable) {
		if (!enable) {
			view.findViewById(R.id.add_extras).setVisibility(View.GONE);
			view.findViewById(R.id.add_record).setVisibility(View.GONE);
			extras.setVisibility(View.GONE);
			View v=view.findViewById(R.id.input_field);
//			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//			p.weight = 1;
//			v.setLayoutParams(p);
			thumbnailPopupLayout.setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.add_extras).setVisibility(View.VISIBLE);
			view.findViewById(R.id.add_record).setVisibility(View.VISIBLE);
		}
	}

}
