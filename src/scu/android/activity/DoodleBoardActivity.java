package scu.android.activity;

import scu.android.base.DoodleCanvas;
import scu.android.util.AppUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
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

/*
 * 涂鸦板
 */
public class DoodleBoardActivity extends Activity {

	private DoodleCanvas doodleCanvas;
	private View paintSet;
	private ImageView paintDemo;
	private SeekBar range;
	private Intent intent;
	private boolean isSetColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doodle_board);
		intent = getIntent();
		init();
	}

	public void init() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		doodleCanvas = (DoodleCanvas) findViewById(R.id.doodle_canvas);
		paintSet = findViewById(R.id.paint_set);
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
					AppUtils.setViewSize(paintDemo, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					paintDemo.setBackgroundColor(Color.parseColor(color));
					doodleCanvas.setPaintColor(color);
				} else {
					int baseSize = AppUtils
							.getWindowMetrics(DoodleBoardActivity.this).widthPixels / 200;
					int paintSize = 0;
					switch (progress / 16) {
					case 0:
						paintSize = baseSize;
						break;
					case 1:
						paintSize = 2 * baseSize;
						break;
					case 2:
						paintSize = 3 * baseSize;
						break;
					case 3:
						paintSize = 4 * baseSize;
						break;
					case 4:
						paintSize = 5 * baseSize;
						break;
					case 5:
						paintSize = 6 * baseSize;
						break;
					default:
						baseSize = doodleCanvas.getPaintSize();
						break;
					}
					AppUtils.setViewSize(paintDemo, paintSize, paintSize);
					doodleCanvas.setPaintSize(paintSize);
				}
			}
		});
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
			String doodlePath = doodleCanvas.getDoodlePath();
			intent.putExtra("doodlePath", doodlePath);
			setResult(Activity.RESULT_OK, intent);
			DoodleBoardActivity.this.finish();
			Log.i("DoodleBoradActivity", "imgPath=" + doodlePath);
			break;
		}
		return true;
	}

	public void OnClick(View view) {
		switch (view.getId()) {
		case R.id.paint_size:
			isSetColor = !isSetColor;
			break;
		case R.id.withdraw:
			doodleCanvas.withdraw(false);
			break;
		case R.id.doodlePaint:
			setPaintSetVisibility();
			break;
		case R.id.doodleColor:
			setPaintSetVisibility();
			break;
		case R.id.del:
			doodleCanvas.withdraw(true);
			break;
		}
	}

	public void setPaintSetVisibility() {
		if (paintSet.getVisibility() == View.INVISIBLE)
			paintSet.setVisibility(View.VISIBLE);
		else
			paintSet.setVisibility(View.INVISIBLE);
	}

}
