package scu.android.ui;

//import scu.android.base.HandWriteCanvas;
import scu.android.util.BitmapUtils;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.demo.note.R;

/*
 * 手写板
 */
@SuppressLint("SetJavaScriptEnabled")
public class HandWriteCanvasActivity extends Activity implements
		View.OnClickListener {
	private WebView note;
	// private HandWriteCanvas canvas;
	private ImageButton undo;
	private final String handwritePath = "ConquerQuestion/CurrentUser/image/handwrite";// 存储路径
	private Intent intent;
	private BroadcastReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handwrite_canvas);
		intent = getIntent();
		init();
	}

	public void init() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		note = (WebView) findViewById(R.id.handwriteNote);
		note.getSettings().setJavaScriptEnabled(true);
		note.setDrawingCacheEnabled(true);
		note.loadUrl("file:///android_asset/note.html");

		// canvas = (HandWriteCanvas) findViewById(R.id.handwriteCanvas);
		undo = (ImageButton) findViewById(R.id.undo);
		undo.setOnClickListener(this);
		receiver = new ImageRecevier();
		registerReceiver(receiver, new IntentFilter(
				"scu.android.base.handwriteCanvas"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.doodleboard_actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.doodleComplete:
			intent.putExtra("handwritePath", getHandWritePath());
			setResult(Activity.RESULT_OK, intent);
			HandWriteCanvasActivity.this.finish();
			Log.i("Complete", "doodleComplete");
			break;
		}
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.undo:
			note.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(
					KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));

			break;
		}
	}

	// 保存
	public String getHandWritePath() {
		Bitmap bitmap = note.getDrawingCache();
		return BitmapUtils.saveBitmap(this, bitmap, handwritePath, null,
				note.getWidth(), note.getHeight());
	}

	private class ImageRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("scu.android.base.handwriteCanvas")) {
				String src = intent.getStringExtra("imgPath");
				note.loadUrl("javascript:insertHandWriteImage('" + src + "')");
				Log.i("insertHandWriteImage:", src);
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}
