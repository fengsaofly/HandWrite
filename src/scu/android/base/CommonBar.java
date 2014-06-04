package scu.android.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.demo.note.R;

public class CommonBar extends LinearLayout implements View.OnClickListener,
		OnMenuItemClickListener {

	private final String DOODLECLASS = "scu.android.activity.DoodleBoardActivity";

	private ImageButton record, camera, handwrite, doodle;
	private ImageButton keyboard, handwriteBlank, handwriteReturn,
			handwritePaint, handwriteDelete;
	private PopupMenu cameraTypeMenu;
	private HandWriteCanvas handWriteCanvas;

	private View defaultBar, handwriteBar;

	public CommonBar(Context context) {
		super(context);
		init(context);
	}

	public CommonBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CommonBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.common_bar, this);
		initDefaultBar(view);
		initHandwriteBar(view);
		initMenu();
	}

	public void initHandwriteBar(View view) {
		handwriteBar = (View) view.findViewById(R.id.handwriteBar);
		handWriteCanvas = (HandWriteCanvas) view
				.findViewById(R.id.handwriteCanvas);
		keyboard = (ImageButton) view.findViewById(R.id.keyboard);
		handwriteBlank = (ImageButton) view.findViewById(R.id.handwriteBlank);
		handwriteReturn = (ImageButton) view.findViewById(R.id.handwriteReturn);
		handwritePaint = (ImageButton) view.findViewById(R.id.handwritePaint);
		handwriteDelete = (ImageButton) view.findViewById(R.id.handwriteDelete);
		keyboard.setOnClickListener(this);
		handwriteBlank.setOnClickListener(this);
		handwriteReturn.setOnClickListener(this);
		handwritePaint.setOnClickListener(this);
		handwriteDelete.setOnClickListener(this);
	}

	public void initDefaultBar(View view) {
		defaultBar = (View) view.findViewById(R.id.defaultBar);
		record = (ImageButton) view.findViewById(R.id.record);
		camera = (ImageButton) view.findViewById(R.id.camera);
		handwrite = (ImageButton) view.findViewById(R.id.handwrite);
		doodle = (ImageButton) view.findViewById(R.id.doodle);
		record.setOnClickListener(this);
		camera.setOnClickListener(this);
		handwrite.setOnClickListener(this);
		doodle.setOnClickListener(this);
	}

	public void initMenu() {
		cameraTypeMenu = new PopupMenu(getContext(), camera);
		cameraTypeMenu.getMenuInflater().inflate(R.menu.camera_type_menu,
				cameraTypeMenu.getMenu());
		cameraTypeMenu.setOnMenuItemClickListener(this);
	}

	public Bitmap getHandWriteBitmap() {
//		return handWriteCanvas.getHandWriteBitmap();
		return null;
	}

	public void setTargetAction(String targetAction) {
//		handWriteCanvas.setTargetpAction(targetAction);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.record:
			break;
		case R.id.camera:
			cameraTypeMenu.show();
			break;
		case R.id.handwrite:
			defaultBar.setVisibility(View.GONE);
			handwriteBar.setVisibility(View.VISIBLE);
			handWriteCanvas.setVisibility(View.VISIBLE);
			break;
		case R.id.doodle:
			Intent intent = new Intent();
			intent.setClassName(getContext(), DOODLECLASS);
			getContext().startActivity(intent);
			break;
		case R.id.keyboard:
			defaultBar.setVisibility(View.VISIBLE);
			handwriteBar.setVisibility(View.GONE);
			handWriteCanvas.setVisibility(View.GONE);
			break;
		case R.id.handwriteBlank:
//			handWriteCanvas.handwriteBlank();
			break;
		case R.id.handwriteReturn:
//			handWriteCanvas.handwriteReturn();
			break;
		case R.id.handwriteDelete:
//			handWriteCanvas.handwriteDelete();
			break;

		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.photos:	
//			handWriteCanvas.sendBroadcast(handWriteCanvas.targetAction, "image", "test.jpg");
			break;
		}
		return false;
	}
	
	public String record(){
		String recordPath=null;
		
		return recordPath;
	}

}
