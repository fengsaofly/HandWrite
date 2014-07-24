package scu.android.ui;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demo.note.R;

/**
 * 播放录音组件
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class AudioView extends LinearLayout implements
		OnBufferingUpdateListener, OnPreparedListener, OnCompletionListener,
		OnClickListener, OnErrorListener {

	private final String TAG = "AudioView";
	private TextView duration;// 总时间
	private ProgressBar loading;// 加载中
	private ImageView status;// 播放状态
	private static MediaPlayer mediaPlayer;
	private String url;
	private Handler handler;
	private AnimationDrawable animationDrawable;

	public MediaPlayer getMediaPlayer() {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		} else {
			if (mediaPlayer.isPlaying()) {
				pause();
			}
		}
		return mediaPlayer;
	}

	public AudioView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		final View view = inflater.inflate(R.layout.audio_view, this);
		duration = (TextView) view.findViewById(R.id.duration);
		loading = (ProgressBar) view.findViewById(R.id.loading);
		status = (ImageView) view.findViewById(R.id.status);

		view.setOnClickListener(this);
		try {
			mediaPlayer = getMediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setOnErrorListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				final int total = mediaPlayer.getDuration();
				final int now = mediaPlayer.getCurrentPosition();
				duration.setText((total - now) / 1000 + "\"");
			}

		};
		final TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						handler.sendEmptyMessage(0);
					}
				}
			}
		};
		new Timer().schedule(task, 0, 1000);
	}

	/**
	 * 显示录音文件总时间
	 * 
	 * @param url
	 *            录音文件地址
	 */
	public void showDuration(final String url) {
		try {
			this.url = url;
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
//			Log.e(TAG, e.getMessage());
		}
		Log.d(TAG, "[showDuration]");
	}

	/**
	 * 播放网络音乐
	 */
	public void playNetAudio() {
		if (!mediaPlayer.isPlaying()) {
			try {
				status.setBackgroundResource(R.anim.audioplay);
				animationDrawable = (AnimationDrawable) status.getBackground();
				animationDrawable.setOneShot(false);
				animationDrawable.start();
				mediaPlayer.reset();
				mediaPlayer.setDataSource(url);
				mediaPlayer.start();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		} else {
			pause();
		}
		Log.d(TAG, "[playNetAudio]");
	}

	public void pause() {
		mediaPlayer.pause();
		animationDrawable.stop();
		Log.d(TAG, "[pause]");
	}

	public void stop() {
		if (mediaPlayer != null) {
			duration.setText(mediaPlayer.getDuration() / 1000 + "\"");
			animationDrawable.stop();
			status.setBackgroundResource(R.drawable.question_add_record_icon_default);
			mediaPlayer.stop();
			mediaPlayer.release();
		}
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		duration.setText(player.getDuration() / 1000 + "\"");
		loading.setVisibility(View.GONE);
		Log.d(TAG, "[onPrepared]");
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		stop();
		Log.d(TAG, "[onCompletion]");
	}

	@Override
	public void onBufferingUpdate(MediaPlayer player, int bufferingProgress) {
		duration.setText(bufferingProgress + "%");
	}

	@Override
	public void onClick(View v) {
		playNetAudio();
	}

	@Override
	public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
		return true;
	}

}
