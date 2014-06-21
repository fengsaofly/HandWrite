package scu.android.base;

import java.util.ArrayList;
import scu.android.activity.ScanPhotosActivity;
import scu.android.ui.PhotosAdapter;
import scu.android.util.AppUtils;
import scu.android.util.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.note.R;

/**
 * 回复问题输入控件
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class CommonEditor extends LinearLayout implements OnClickListener {

	private Activity activity;// 所属Activiy
	private View contentView;//

	private EditText inputField;
	private TextView addExtras;
	private TextView chatSend;

	private View extrasView;// 其他
	private View addDoodle;
	private View addHWrite;
	private View addPhotos;
	private View addCamera;

	private View thumbnailsParentView;
	private GridView thumbnailsView;
	private PhotosAdapter thumbnailsAdapter;
	private ArrayList<String> thumbnails;

	private String cameraName;
	private String action;
	private int maxPhotosNum;
	private int curPhotosNum;

	public CommonEditor(Context context) {
		super(context);
		init(context);
	}

	public CommonEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CommonEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		contentView = inflater.inflate(R.layout.common_editor, this);
		addExtras = (TextView) contentView.findViewById(R.id.add_extras);
		inputField = (EditText) contentView.findViewById(R.id.input_field);
		chatSend = (TextView) contentView.findViewById(R.id.chat_send);
		extrasView = contentView.findViewById(R.id.question_popup_layout);
		addDoodle = extrasView.findViewById(R.id.popup_doodle_lay);
		addCamera = extrasView.findViewById(R.id.popup_camera_lay);
		addPhotos = extrasView.findViewById(R.id.popup_imgpicker_lay);
		addHWrite = extrasView.findViewById(R.id.popup_handwrite_lay);
		addExtras.setOnClickListener(this);
		inputField.setOnClickListener(this);
		chatSend.setOnClickListener(this);
		addDoodle.setOnClickListener(this);
		addCamera.setOnClickListener(this);
		addPhotos.setOnClickListener(this);
		addHWrite.setOnClickListener(this);
		maxPhotosNum = Constants.MAX_PHOTOS_NUM;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		thumbnailsParentView = contentView
				.findViewById(R.id.thumbnails_parent_view);
		thumbnailsView = (GridView) contentView
				.findViewById(R.id.thumbnails_view);
		thumbnails = new ArrayList<String>();
		thumbnailsAdapter = new PhotosAdapter(activity, thumbnails);
		thumbnailsAdapter.setColumnNum(4);
		thumbnailsView.setAdapter(thumbnailsAdapter);
		thumbnailsView.setOnItemClickListener(new ThumbnailListener());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.add_record:
			//
			break;
		case R.id.input_field:
			if (extrasView.getVisibility() == View.VISIBLE)
				extrasView.setVisibility(View.GONE);
			break;
		case R.id.add_extras:
			setExtrasViewVisibility();
			break;
		case R.id.chat_send:
			Intent intent = new Intent();
			intent.setAction(action);
			activity.sendBroadcast(intent);
			break;
		case R.id.popup_doodle_lay:
			if (curPhotosNum < (maxPhotosNum)) {
				AppUtils.doodleBoard(activity);
			} else {
				alertFull();
			}
			break;
		case R.id.popup_camera_lay:
			if (curPhotosNum < (maxPhotosNum)) {
				cameraName = AppUtils.sysCamera(activity);
			} else {
				alertFull();
			}
			break;
		case R.id.popup_imgpicker_lay:
			if (curPhotosNum < (maxPhotosNum)) {
				int availNumber = maxPhotosNum - curPhotosNum;
				AppUtils.phonePictures(activity, availNumber);
			} else {
				alertFull();
			}
			break;
		case R.id.popup_handwrite_lay:
			if (curPhotosNum < (maxPhotosNum)) {
				AppUtils.hwBoard(activity);
			} else {
				alertFull();
			}
			break;
		}
	}

	// 缩略图监听器
	private class ThumbnailListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Intent intent = new Intent(activity, ScanPhotosActivity.class);
			intent.setAction(action);
			intent.putStringArrayListExtra("photos", thumbnails);
			intent.putExtra("index", position + 1);
			activity.startActivity(intent);
		}
	}

	public void disableAddExtras() {
		addExtras.setVisibility(View.GONE);
	}

	public void setExtrasViewVisibility() {
		if (extrasView.getVisibility() == View.VISIBLE) {
			extrasView.setVisibility(View.GONE);
		} else {
			extrasView.setVisibility(View.VISIBLE);
		}
		hideSoft();
	}

	public void hideSoft() {
		// 隐藏软键盘
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputField.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void setMaxPhotosNum(int maxPhotosNum) {
		this.maxPhotosNum = maxPhotosNum;
	}

	public int getCurPhotosNum() {
		return curPhotosNum;
	}

	public void setCurPhotosNum(int curPhotosNum) {
		this.curPhotosNum = curPhotosNum;
	}

	public ArrayList<String> getThumbnails() {
		return thumbnails;
	}

	public PhotosAdapter getThumbnailsAdapter() {
		return thumbnailsAdapter;
	}

	public GridView getThumbnailsView() {
		return thumbnailsView;
	}

	public View getThumbnailsParentView() {
		return thumbnailsParentView;
	}

	public View getExtrasView() {
		return extrasView;
	}

	public void setExtrasView(View extrasView) {
		this.extrasView = extrasView;
	}

	public String getContent() {
		return inputField.getText().toString();
	}

	public void clearContent() {
		inputField.setText(null);
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void focus() {
		inputField.findFocus();
	}

	public void alertFull() {
		Toast.makeText(activity, "最多只能选择" + maxPhotosNum + "张图片",
				Toast.LENGTH_SHORT).show();
	}

}
