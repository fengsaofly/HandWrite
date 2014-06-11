package scu.android.demo;

import com.demo.note.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Demo extends Activity {
	private BroadcastReceiver receiver;
	private Intent intent;

	private FingerShowView showView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);

		showView = (FingerShowView) findViewById(R.id.gameview);
		final FingerView view = (FingerView) findViewById(R.id.fingerView);
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("com.demo.new")) {
					showView.setDataBitmaps(view.getDataBitmaps());
				}
			}
		};
		registerReceiver(receiver, new IntentFilter("com.demo.new"));

		intent = getIntent();
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
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
			String handwritePath = showView.getHandWritePath();
			intent.putExtra("handwritePath", handwritePath);
			setResult(Activity.RESULT_OK, intent);
			Demo.this.finish();
			Log.i("DemoActivity", "imgPath=" + handwritePath);
			break;
		}
		return true;
	}

}
