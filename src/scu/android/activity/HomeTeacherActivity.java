package scu.android.activity;

import scu.android.util.ActivitySupport;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.demo.note.R;

public class HomeTeacherActivity extends ActivitySupport {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hometeacher_add_lay);
	}

	public void myOnclick(View view) {
		switch (view.getId()) {
		case R.id.top_left_back_btn:
			cancel();
			break;
		case R.id.submit:
			addHomeTeacher();
			break;
		}
	}

	// 请家教
	public void addHomeTeacher() {

	}

	public void cancel() {
		Dialog alert = new AlertDialog.Builder(this).setTitle("破题")
				.setMessage("放弃请家教?")
				.setPositiveButton("确定", new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).setNegativeButton("取消", null).create();
		alert.show();
	}

	@Override
	public void onBackPressed() {
		cancel();
	}

}
