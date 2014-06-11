package scu.android.base;

import scu.android.util.AppUtils;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.demo.note.R;

/**
 * 通用输入控件
 */
public class CommonEditText extends LinearLayout implements OnClickListener {

	private Activity activity;// 所属Activiy

	private View contentView;
	private TextView addExtras;
	private EditText inputField;

	private View extrasView;// 其他
	private View addDoodle;
	private View addHWrite;
	private View addPhotos;
	private View addCamera;

	private View thumbnailsParentView;
	private GridView thumbnailsView;
	private ThumbnailsAdapter thumbnailsAdapter;
	private ArrayList<String> thumbnails;

	private String cameraName;
	private String action;
	private int maxPhotosNum;
	private int curPhotosNum;

	public CommonEditText(Context context) {
		super(context);
		init(context);
	}

	public CommonEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CommonEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(R.layout.common_edit_text, this);
		extrasView = contentView.findViewById(R.id.question_popup_layout);
	}

	public void OnClick(View view) {
		Log.i("add", "Add");
		switch (view.getId()) {
		case R.id.chat_record_btn:
			break;
		case R.id.chat_add_btn:
			if (extrasView.getVisibility() == View.VISIBLE) {
				extrasView.setVisibility(View.INVISIBLE);
			} else {
				extrasView.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.popup_imgpicker_lay:
			// if (selectNativePhotosNumber < (MAX_NUMBER - 2)) {
			// int availNumber = MAX_NUMBER - 2 - selectNativePhotosNumber;
			// AppUtils.phonePictures(this, availNumber);
			// } else {
			// Toast.makeText(this, "最多只能选择" + (MAX_NUMBER - 2) + "张图片",
			// Toast.LENGTH_SHORT).show();
			// }
			break;
		case R.id.popup_handwrite_lay:
			AppUtils.hwBoard(activity);
			break;
		case R.id.popup_camera_lay:
			// imgName = AppUtils.sysCamera(this);
			break;
		case R.id.popup_recode_lay:
			AppUtils.doodleBoard(activity);

		LayoutInflater inflater = LayoutInflater.from(context);
		contentView = inflater.inflate(R.layout.common_edit_text, this);
		addExtras = (TextView) contentView.findViewById(R.id.add_extras);
		inputField = (EditText) contentView.findViewById(R.id.input_field);
		extrasView = contentView.findViewById(R.id.question_popup_layout);
		addDoodle = extrasView.findViewById(R.id.popup_recode_lay);
		addCamera = extrasView.findViewById(R.id.popup_camera_lay);
		addPhotos = extrasView.findViewById(R.id.popup_imgpicker_lay);
		addHWrite = extrasView.findViewById(R.id.popup_handwrite_lay);
		addExtras.setOnClickListener(this);
		inputField.setOnClickListener(this);
		addDoodle.setOnClickListener(this);
		addCamera.setOnClickListener(this);
		addPhotos.setOnClickListener(this);
		addHWrite.setOnClickListener(this);
		maxPhotosNum = AppUtils.MAX_PHOTOS_NUM;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
		thumbnailsParentView = contentView
				.findViewById(R.id.thumbnails_parent_view);
		thumbnailsView = (GridView) contentView
				.findViewById(R.id.thumbnails_view);
		thumbnails = new ArrayList<String>();
		thumbnailsAdapter = new ThumbnailsAdapter(activity, thumbnails);
		thumbnailsView.setAdapter(thumbnailsAdapter);
		thumbnailsView.setOnItemClickListener(new ThumbnailListener());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.input_field:
			if (extrasView.getVisibility() == View.VISIBLE)
				extrasView.setVisibility(View.GONE);
			break;
		case R.id.add_extras:
			setExtrasViewVisibility();
			break;
		case R.id.popup_recode_lay:
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


	public void setActivity(Activity activity) {
		this.activity = activity;

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

	public void setExtrasViewVisibility() {
		if (extrasView.getVisibility() == View.VISIBLE) {
			extrasView.setVisibility(View.GONE);
		} else {
			extrasView.setVisibility(View.VISIBLE);
		}
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

	public ThumbnailsAdapter getThumbnailsAdapter() {
		return thumbnailsAdapter;
	}

	public GridView getThumbnailsView() {
		return thumbnailsView;
	}

	public View getThumbnailsParentView() {
		return thumbnailsParentView;
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

	public void alertFull() {
		Toast.makeText(activity, "最多只能选择" + maxPhotosNum + "张图片",
				Toast.LENGTH_SHORT).show();
	}

}
