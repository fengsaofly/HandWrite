package scu.android.ui;


import com.demo.note.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectPicPopupWindow extends Activity implements OnClickListener{

	private Button btn_register, btn_pwd_feedback, btn_cancel;
	private LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);
		btn_register = (Button) this.findViewById(R.id.register_btn);
		btn_pwd_feedback = (Button) this.findViewById(R.id.login_popup_view_pwd_feedback);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
		
		layout=(LinearLayout)findViewById(R.id.pop_layout);
		
		//添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		layout.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
		//添加按钮监听
		btn_cancel.setOnClickListener(this);
		btn_register.setOnClickListener(this);
		btn_pwd_feedback.setOnClickListener(this);
	}
	
	//实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_btn:
			startActivity(new Intent(SelectPicPopupWindow.this,RegisterActivity.class));
			break;
		case R.id.login_popup_view_pwd_feedback:
			startActivity(new Intent(SelectPicPopupWindow.this,PwdFeedBackActivity.class));
			break;
		case R.id.btn_cancel:				
			break;
		default:
			break;
		}
		finish();
	}
	
}
