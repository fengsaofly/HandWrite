package scu.android.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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

import com.demo.note.R;

public class MAudioView extends LinearLayout implements OnClickListener {
	private final String TAG = getClass().getName();
	private static final int INTIAL_KB_BUFFER = 96 * 10 / 8;
	private ImageView play;
	private ImageView status;
	private ProgressBar loading;
	private AnimationDrawable animationDrawable;
	private MediaPlayer mediaPlayer;
	private String audioUrl;
	private int count;
	private File tempFile;
	private boolean isInterrupted;
	private Context context;

	public MAudioView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initplays();
	}

	public void initplays() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.maudio_view, this);
		play = (ImageView) view.findViewById(R.id.play);
		status = (ImageView) view.findViewById(R.id.status);
		loading = (ProgressBar) view.findViewById(R.id.loading);
		animationDrawable = (AnimationDrawable) status.getBackground();
		animationDrawable.setOneShot(false);
		view.setOnClickListener(this);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	};

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

	public void playAudio() {
		if (mediaPlayer.isPlaying()) {
			interrupt();
			animationDrawable.stop();
			play.setImageResource(R.drawable.play);
			mediaPlayer.pause();
		} else {
			status.setVisibility(View.VISIBLE);
			animationDrawable.start();
			play.setImageResource(R.drawable.pause);
			mediaPlayer.start();
		}
	}

	/**
	 * 音频缓冲
	 */
	public void bufferAudio() {
		play.setEnabled(false);
		status.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				downloadAudio();
			}
		}.start();
	}

	/**
	 * 下载
	 */
	public void downloadAudio() {
		try {
			URLConnection conn = new URL(audioUrl).openConnection();
			conn.connect();
			InputStream in = conn.getInputStream();
			if (null == in) {
				Log.e(getClass().getName(), "unable to get inputSteam from url" + audioUrl);
			} else {
				tempFile = new File(context.getCacheDir(), "tempAudio.dat");
				if (tempFile.exists()) {
					tempFile.delete();
				}
				FileOutputStream out = new FileOutputStream(tempFile);
				byte[] buffer = new byte[1024 * 10];
				int lens = 0, total = 0;
				do {
					lens = in.read(buffer);
					if (lens <= 0)
						break;
					out.write(buffer, 0, lens);
					total += lens;
					fireMediaBuffer(total / 1024);
				} while (validateNotInterrupted());
				out.close();
			}
			in.close();
			if (validateNotInterrupted()) {
				fireDataFullyLoaded();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fireMediaBuffer(final int total) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Log.d("audio:", tempFile.length() / 1024 + ":" + total);
				if (mediaPlayer == null) {
					if (total >= INTIAL_KB_BUFFER) {
						openMediaPlayer();
					}
				} else if (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() <= 1000) {
					transferBufferToMediaPlayer();
				}
			}
		});
	}

	/**
	 * 开始播放
	 */
	public void openMediaPlayer() {
		File bufferdFile = new File(getContext().getCacheDir(), "playMedia" + (count++) + ".dat");
		move(tempFile, bufferdFile);
		mediaPlayer = getMediaPlayer(bufferdFile);
	}

	public MediaPlayer getMediaPlayer(final File file) {
		MediaPlayer mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDataSource(new FileInputStream(file).getFD());
			mMediaPlayer.prepare();
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer player) {
					play.setEnabled(true);
					play.setImageResource(R.drawable.pause);
					loading.setVisibility(View.GONE);
					status.setVisibility(View.VISIBLE);
					animationDrawable.start();
					player.start();
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mMediaPlayer;
	}

	private void transferBufferToMediaPlayer() {
		try {
			if (mediaPlayer == null) {
				openMediaPlayer();
			}
			boolean wasPlaying = mediaPlayer.isPlaying();
			int curPosition = mediaPlayer.getCurrentPosition();
			File oldBufferedFile = new File(getContext().getCacheDir(), "playingMedia" + count + ".dat");
			File bufferedFile = new File(getContext().getCacheDir(), "playingMedia" + (count++) + ".dat");
			bufferedFile.deleteOnExit();
			move(tempFile, bufferedFile);
			mediaPlayer.pause();
			mediaPlayer = getMediaPlayer(bufferedFile);
			mediaPlayer.seekTo(curPosition);
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer player) {
					play.setImageResource(R.drawable.play);
					animationDrawable.stop();
					status.setVisibility(View.GONE);
					Log.d("Complete", tempFile.length() / 1024 + "");
				}
			});
			boolean atEndOfFile = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() <= 1000;
			if (wasPlaying || atEndOfFile) {
				mediaPlayer.start();
			}
			oldBufferedFile.delete();
		} catch (Exception e) {
			Log.e(getClass().getName(), "Error updating to newly loaded content.", e);
		}
	}

	public void move(File oldFile, File newFile) {
		if (oldFile.exists()) {
			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(oldFile));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(newFile, false));
				byte[] buffer = new byte[1024 * 8];
				int lens = 0;
				while ((lens = in.read(buffer)) > 0) {
					out.write(buffer, 0, lens);
				}
				out.close();
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void fireDataFullyLoaded() {
		Runnable updater = new Runnable() {
			public void run() {
				transferBufferToMediaPlayer();
			}
		};
		handler.post(updater);
	}

	private boolean validateNotInterrupted() {
		if (isInterrupted) {
			if (mediaPlayer != null) {
				mediaPlayer.pause();
			}
			return false;
		} else {
			return true;
		}
	}

	public void interrupt() {
		play.setEnabled(false);
		isInterrupted = true;
		validateNotInterrupted();
	}

	@Override
	public void onClick(View v) {
		if (mediaPlayer == null) {
			bufferAudio();
		} else {
			playAudio();
		}
	}
}
