package scu.android.ui;

import com.demo.note.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ModifyAvatarActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.modify_nomal);
	}

}
