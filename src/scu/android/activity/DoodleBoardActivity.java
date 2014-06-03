package scu.android.activity;

import scu.android.base.DoodleCanvas;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.demo.note.R;

/*
 * 涂鸦板
 */
public class DoodleBoardActivity extends Activity implements
		View.OnClickListener {

	private ImageButton withdraw, doodlePaint, doodelColor, del;
	private DoodleCanvas doodleCanvas;
	private Intent intent;

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

		doodleCanvas = (DoodleCanvas) findViewById(R.id.doodleCanvas);
		withdraw = (ImageButton) findViewById(R.id.withdraw);
		doodlePaint = (ImageButton) findViewById(R.id.doodlePaint);
		doodelColor = (ImageButton) findViewById(R.id.doodleColor);
		del = (ImageButton) findViewById(R.id.del);

		withdraw.setOnClickListener(this);
		doodelColor.setOnClickListener(this);
		doodlePaint.setOnClickListener(this);
		del.setOnClickListener(this);
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
			String doodlePath=doodleCanvas.getDoodlePath();
			intent.putExtra("doodlePath", doodlePath);
			setResult(Activity.RESULT_OK, intent);
			DoodleBoardActivity.this.finish();
			Log.i("DoodleBoradActivity", "imgPath="+doodlePath);
			break;
		}
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.withdraw:
			doodleCanvas.withdraw(false);
			break;
		case R.id.doodlePaint:
			break;
		case R.id.doodleColor:
			break;
		case R.id.del:
			doodleCanvas.withdraw(true);
			break;
		}
	}

}
