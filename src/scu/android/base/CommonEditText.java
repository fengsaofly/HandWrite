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
public class CommonEditText extends LinearLayout {

	private View contentView;
	private View extrasView;

	private Activity activity;

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
			break;
		}
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

}
