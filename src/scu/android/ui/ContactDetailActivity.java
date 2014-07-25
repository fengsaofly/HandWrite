package scu.android.ui;

import java.util.Map;

import scu.android.application.MyApplication;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.note.R;

public class ContactDetailActivity extends Activity {
	ImageView tag_avatar_val = null;
	TextView tag_nickname_val = null;
	TextView tag_sex_val = null;
	TextView tag_career_val = null;
	TextView tag_region_val = null;
	TextView tag_sign_val = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_detail);
		initial();
	}

	public void initial() {
		Intent intent = getIntent();
		String contactName = intent.getStringExtra("contactName");
		System.out.println("contactName: "+contactName);
		Map<String,Object> map = ((MyApplication)getApplication()).getFriendVcard(contactName);
		Bitmap bm = ((MyApplication)getApplication()).drawable2Bitmap((Drawable)map.get("friend_avatar"));
		tag_avatar_val = (ImageView) findViewById(R.id.tag_avatar_val);
		tag_avatar_val.setImageBitmap(bm);
		tag_nickname_val = (TextView) findViewById(R.id.tag_nickname_val);
		tag_nickname_val.setText(map.get("friend_nickName").toString());
		tag_sex_val = (TextView) findViewById(R.id.tag_sex_val);
		tag_sex_val.setText(map.get("friend_gender").toString());
		tag_career_val = (TextView) findViewById(R.id.tag_career_val);
		tag_career_val.setText(map.get("friend_carrer").toString());
		tag_region_val = (TextView) findViewById(R.id.tag_region_val);
		tag_region_val.setText(map.get("friend_zone").toString());
		tag_sign_val = (TextView) findViewById(R.id.tag_sign_val);
		tag_sign_val.setText(map.get("friend_sign").toString());
		
	}

}