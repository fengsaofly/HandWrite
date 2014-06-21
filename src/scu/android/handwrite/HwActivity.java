package scu.android.handwrite;

import scu.android.util.AppUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.demo.note.R;

public class HwActivity extends Activity {
	private BroadcastReceiver receiver;
	private Intent intent;

	private FingerShowView showView;
	private FingerView fingerView;
	private View paintSet;
	private ImageView paintSize;
	private ImageView paintDemo;
	private SeekBar range;
	private boolean isSetColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hw);

		showView = (FingerShowView) findViewById(R.id.gameview);
		fingerView = (FingerView) findViewById(R.id.fingerView);
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("com.demo.new")) {
					showView.setDataBitmaps(fingerView.getDataBitmaps());
				}
			}
		};
		paintSet = findViewById(R.id.paint_set);
		paintSize = (ImageView) paintSet.findViewById(R.id.paint_size);
		paintDemo = (ImageView) paintSet.findViewById(R.id.paint_demo);
		isSetColor = true;
		range = (SeekBar) paintSet.findViewById(R.id.range);
		range.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (isSetColor) {
					String color = null;
					switch (progress / 16) {
					case 0:
						color = getResources().getString(R.color.black);
						break;
					case 1:
						color = getResources().getString(R.color.red);
						break;
					case 2:
						color = getResources().getString(R.color.blue);
						break;
					case 3:
						color = getResources().getString(R.color.green);
						break;
					case 4:
						color = getResources().getString(R.color.purbe);
						break;
					case 5:
						color = getResources().getString(R.color.orange);
						break;
					default:
						color = getResources().getString(R.color.black);
						break;
					}
					AppUtils.setViewSize(paintDemo, LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT);
					paintDemo.setBackgroundColor(Color.parseColor(color));
					fingerView.setPaintColor(color);
				} else {
					int baseSize = AppUtils.getWindowMetrics(HwActivity.this).widthPixels / 200;
					int paintSize = 0;
					switch (progress / 16) {
					case 0:
						paintSize = baseSize;
						break;
					case 1:
						paintSize = 2 * baseSize;
						break;
					case 2:
						paintSize = 4 * baseSize;
						break;
					case 3:
						paintSize = 6 * baseSize;
						break;
					case 4:
						paintSize = 8 * baseSize;
						break;
					case 5:
						paintSize = 10 * baseSize;
						break;
					default:
						baseSize = fingerView.getPaintSize();
						break;
					}
					AppUtils.setViewSize(paintDemo, paintSize, paintSize);
					fingerView.setPaintSize(paintSize);
				}
			}
		});

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
			HwActivity.this.finish();
			Log.i("DemoActivity", "imgPath=" + handwritePath);
			break;
		}
		return true;
	}

	public void OnClick(View view) {
		switch (view.getId()) {
		case R.id.paint_size:
			isSetColor = !isSetColor;
			togglePaintBg();
			break;
		case R.id.doodle_color:
			setPaintSetVisibility();
			break;
		case R.id.withdraw:
			showView.withdraw();
			break;
		case R.id.del:
			showView.clean();
			break;
		case R.id.space:
			showView.space();
			break;
		case R.id.newline:
			showView.newline();
			break;
		}
	}

	public void togglePaintBg() {
		if (isSetColor) {
			paintSize.setBackgroundResource(R.drawable.paint_color);
		} else {
			paintSize.setBackgroundResource(R.drawable.paint_size);
		}
	}

	public void setPaintSetVisibility() {
		if (paintSet.getVisibility() == View.INVISIBLE)
			paintSet.setVisibility(View.VISIBLE);
		else
			paintSet.setVisibility(View.INVISIBLE);
	}

}
