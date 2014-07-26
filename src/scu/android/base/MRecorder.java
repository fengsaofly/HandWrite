//package scu.android.base;
//
//import java.io.File;
//import java.io.IOException;
//
//import scu.android.application.MyApplication;
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
//import android.media.MediaRecorder;
//import android.os.Handler;
//
//class MRecorder {
//
//	private static MRecorder mRecorder;
//	private static Context context;
//	private MediaRecorder recorder;
//	private MediaPlayer player;
//	private String audio;
//	private boolean isPlaying;
//	private long start;
//	private long end;
//	
//	public static MRecorder getInstance(Context context){
//		MRecorder.context=context;
//		if(mRecorder==null){
//			mRecorder=new MRecorder();
//		}
//		return mRecorder;
//	}
//
//	public File generateFile() {
//		File file = new File(MyApplication.getSDCardPath() + "/ConquerQuestion" + "/" + MyApplication.getCurrentUser(context).getUser_name()
//				+ "/" + "Audio");
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//		String audioName = System.currentTimeMillis() + ".amr";
//		File audioFile = new File(file, audioName);
//		return audioFile;
//	}
//
//	public void initRecorder() {
//		recorder = new MediaRecorder();
//		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//		File audioFile = generateFile();
//		audio = audioFile.getAbsolutePath();
//		recorder.setOutputFile(audio);
//		try {
//			audioFile.createNewFile();
//			recorder.prepare();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		recorder.start();
//		start = System.currentTimeMillis();
//	}
//
//	public void play() {
//		player = new MediaPlayer();
//		player.reset();
//		try {
//			player.setDataSource(audio);
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		player.start();
//		isPlaying=true;
//		player.setOnCompletionListener(new OnCompletionListener() {
//
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//				isPlaying=false;
//				mp.stop();
//				mp.release();
//				mp = null;
//			}
//		});
//	}
//
//	public void resetRecorder() {
//		end = System.currentTimeMillis();
//		if (recorder != null) {
//			recorder.stop();
//			recorder.release();
//			recorder = null;
//		}
//	}
//
//	public void reset() {
//		audio = null;
//	}
//
//	public int getLength() {
//		return (int) ((end - start) / 1000);
//	}
//
//	public String getAudio() {
//		return audio;
//	}
//
//	public boolean isPlaying() {
//		return isPlaying;
//	}
//
//	Handler hander = new Handler() {
//
//	};
//}
