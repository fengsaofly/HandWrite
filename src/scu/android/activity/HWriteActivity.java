package scu.android.activity;

import scu.android.base.HwCanvas;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.demo.note.R;
/**
 * 手写，暂未使用
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class HWriteActivity extends Activity {
	
	private HwCanvas hwCanvas;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hwrite);
		intent = getIntent();
	}
	
	public void OnClick(View view){
		
	}
}
