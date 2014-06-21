package scu.android.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.demo.note.R;

public class ModifyAvatarActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.modify_nomal);
	}

}
