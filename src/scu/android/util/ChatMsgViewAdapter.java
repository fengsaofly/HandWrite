package scu.android.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import scu.android.activity.ScanPhotosActivity;
import scu.android.application.MyApplication;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.note.R;

public class ChatMsgViewAdapter extends BaseAdapter {

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

	private List<ChatMsgEntity> coll;

	private   List<Map<String,Object>> allContactsVcard;
	private Context ctx;

	private LayoutInflater mInflater;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private String chatContact;
	private int type = -1;  //0：表示单聊 1表示群聊
	TextView tempTvContent = null;
	
	AnimationDrawable animaition;

	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll,String chatContact,int type) {
		ctx = context;
		this.coll = coll;
		this.chatContact = chatContact;
		mInflater = LayoutInflater.from(context);
		this.type = type;
		allContactsVcard = MyApplication.getAllContactsVcard();
	}

	public int getCount() {
		return coll.size();
	}

	public Object getItem(int position) {
		return coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		ChatMsgEntity entity = coll.get(position);

		if (entity.getMsgType()) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}

	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		final ChatMsgEntity entity = coll.get(position);
		boolean isComMsg = entity.getMsgType();
		
		

		ViewHolder viewHolder = null;
		
//		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (isComMsg) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
				viewHolder.iv_userhead = (ImageView)convertView
						.findViewById(R.id.iv_userhead);
				
				 Resources resources = ctx.getResources();
//				 if(type==0){ //表示单聊
//					 System.out.println("coll.get(position).getName():单聊"+coll.get(position).getName());
//					 Drawable d = ((MyApplication)ctx.getApplicationContext()).DrawableOfNameMap.get(coll.get(position).getName());
				 Drawable d = null;
					if(type==0){
						for(int i = 0 ;i<allContactsVcard.size();i++){
							if(coll.get(position).getName().equals(allContactsVcard.get(i).get("friend_name"))){
								d = (Drawable)allContactsVcard.get(i).get("friend_avatar");
								break;
							}
						}
					}
					
					else if(type==1){
						for(int i = 0 ;i<allContactsVcard.size();i++){
							if(coll.get(position).getName().equals(allContactsVcard.get(i).get("friend_nickName"))){
								d = (Drawable)allContactsVcard.get(i).get("friend_avatar");
								break;
							}
						}
					}
					 if(d!=null){
						 viewHolder.iv_userhead.setImageDrawable(d);
					 }
					 else viewHolder.iv_userhead.setImageDrawable(resources.getDrawable(R.drawable.abaose));
//					 viewHolder.iv_userhead.setImageDrawable(((MyApplication)ctx.getApplicationContext()).DrawableOfNameMap.get(coll.get(position).getName()));
//				 }
//				 
//				 else if(type ==1){  //表示群聊
//					 System.out.println("coll.get(position).getName():单聊"+coll.get(position).getName());
//					 Drawable d = ((MyApplication)ctx.getApplicationContext()).DrawableOfNameMap.get(coll.get(position).getName());
//					 if(d!=null){
//						 viewHolder.iv_userhead.setImageDrawable(d);
//					 }
//					 else viewHolder.iv_userhead.setImageDrawable(resources.getDrawable(R.drawable.abaose));
////					 viewHolder.iv_userhead.setImageDrawable(((MyApplication)ctx.getApplicationContext()).DrawableOfNickNameMap.get(coll.get(position).getName()));
//				 }
				
				
//				 
//        		 try{
//        			 int indentify = resources.getIdentifier(ctx.getPackageName()+":drawable/"+((MyApplication)ctx.getApplicationContext()).iconMap.get(chatContact), null, null);
//	        			if(indentify>0){ 
//	        				viewHolder.iv_userhead.setImageDrawable(ctx.getResources().getDrawable(indentify));
//	        			}
//	        			else 
//	        				viewHolder.iv_userhead.setImageDrawable(ctx.getResources().getDrawable(R.drawable.dota_1));
//        		 }catch(NullPointerException e){
//        			
//        			 e.printStackTrace();
//        		 }
        		 
				
			} else {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);
				viewHolder.iv_userhead = (ImageView)convertView
						.findViewById(R.id.iv_userhead);
				Resources resources = ctx.getResources();
				 Drawable d = ((MyApplication)ctx.getApplicationContext()).myIconDrawable;
				if(d!=null)
//				viewHolder.iv_userhead.setImageDrawable(((MyApplication)ctx.getApplicationContext()).myIconDrawable);
//				 else 
					 viewHolder.iv_userhead.setImageDrawable(((MyApplication)ctx.getApplicationContext()).myIconDrawable);
				else viewHolder.iv_userhead.setImageDrawable(resources.getDrawable(R.drawable.shuaige));
//				 if(type==0){ //表示群聊
//					 System.out.println("coll.get(position).getName():群聊"+coll.get(position).getName());
//					 Drawable d = ((MyApplication)ctx.getApplicationContext()).DrawableOfNameMap.get(coll.get(position).getName());
//					 if(d!=null){
//						 viewHolder.iv_userhead.setImageDrawable(d);
//					 }
//					 else viewHolder.iv_userhead.setImageDrawable(resources.getDrawable(R.drawable.shuaige));
//				 }
//				 
//				 else if(type ==1){
//					 System.out.println("coll.get(position).getName():群聊"+coll.get(position).getName());
//					 Drawable d = ((MyApplication)ctx.getApplicationContext()).DrawableOfNickNameMap.get(coll.get(position).getName());
//					 if(d!=null){
//						 viewHolder.iv_userhead.setImageDrawable(d);
//					 }
//					 else viewHolder.iv_userhead.setImageDrawable(resources.getDrawable(R.drawable.shuaige));
//				 }
//       		  try{
//       			 int indentify = resources.getIdentifier(ctx.getPackageName()+":drawable/"+((MyApplication)ctx.getApplicationContext()).iconMap.get(((MyApplication)ctx.getApplicationContext()).userName), null, null);
//	        			if(indentify>0){ 
//	        				viewHolder.iv_userhead.setImageDrawable(ctx.getResources().getDrawable(indentify));
//	        			}
//	        			else 
//	        				viewHolder.iv_userhead.setImageDrawable(ctx.getResources().getDrawable(R.drawable.dota_1));
//       		 }catch(NullPointerException e){
//       			
//       			 e.printStackTrace();
//       		 }
				
			}

			
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView
					.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.isComMsg = isComMsg;
			viewHolder.iv_userhead = (ImageView)convertView
					.findViewById(R.id.iv_userhead);
			
			

			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) convertView.getTag();
//		}
		tempTvContent = viewHolder.tvContent;
		viewHolder.tvSendTime.setText(entity.getDate());
//		//检测到是音频
//		if (entity.getText().contains(".amr")) {
//			viewHolder.tvContent.setText("");
//			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
//			viewHolder.tvTime.setText(entity.getTime());
//		} else {
		if(entity.getTextType().equals("unnomal")){
			viewHolder.tvContent.setText("");
			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			if (entity.getTextType().equals("unnomal") &&entity.getText().contains(".amr")) {
			viewHolder.tvContent.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.question_add_record_icon_default));
			}
			else if(entity.getTextType().equals("unnomal") &&entity.getText().toLowerCase().contains(".png")||entity.getText().toLowerCase().contains(".jpg")){
				
				Bitmap bm = DecodeFile.decodeFile(new File(entity.getText().toString()),256);
				Drawable d = ((MyApplication)ctx.getApplicationContext()).bitmap2Drawable(bm);
				viewHolder.tvContent.setBackgroundDrawable(d);
			}
			else{
				if(isComMsg){
					viewHolder.tvContent.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.chatfrom_bg));
				}
				else{
					viewHolder.tvContent.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.chatto_bg));
				}
			}
		}
		else{
			viewHolder.tvContent.setText(entity.getText());			
			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			if(isComMsg){
				viewHolder.tvContent.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.chatfrom_bg));
			}
			else{
				viewHolder.tvContent.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.chatto_bg));
			}
			
		}
			
		viewHolder.tvTime.setText("");
//		}
		viewHolder.tvContent.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//点击文本，如果是音频则播放
				if (entity.getTextType().equals("unnomal") &&entity.getText().toLowerCase().contains(".amr")) {
					try {
						if (mMediaPlayer != null&&mMediaPlayer.isPlaying()) {
							mMediaPlayer.stop();
						}
						else
						mMediaPlayer = new MediaPlayer();
						mMediaPlayer.reset();
						mMediaPlayer.setDataSource(entity.getText());
						mMediaPlayer.prepare();
						mMediaPlayer.start();
						mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							public void onCompletion(MediaPlayer mp) {
								mMediaPlayer.release();
								mMediaPlayer = null;
//								animaition.stop();
//								tempTvContent.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.question_add_record_icon_default));
							}
							});
//						tempTvContent.setBackgroundDrawable(ctx.getResources().getDrawable(R.anim.playvoice));
//						animaition = (AnimationDrawable) tempTvContent
//								.getBackground();
//						// 最后，就可以启动动画了，代码如下：
//
//						// 是否仅仅启动一次？
//
//						animaition.setOneShot(false);
//
//						if (animaition.isRunning())// 是否正在运行？
//
//						{
//							animaition.stop();// 停止
//
//						}
//						animaition.start();// 启动
							
						

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				else if(entity.getTextType().equals("unnomal") &&entity.getText().toLowerCase().contains(".png")||entity.getText().toLowerCase().contains(".jpg")){
					Intent intent = new Intent(ctx, ScanPhotosActivity.class);
					Bundle bundle = new Bundle();
					ArrayList<String> bitmaps = new ArrayList<String>();
					bitmaps.add("file:///"+entity.getText().toString());
					bundle.putStringArrayList("photos",(ArrayList<String>) bitmaps);
					
					
					intent.putExtras(bundle);
					ctx.startActivity(intent);
				}
				else{
					
				}
				
				
			}
		});
		viewHolder.tvUserName.setText(entity.getName());
		
		return convertView;
	}

	static class ViewHolder {
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public TextView tvTime;
		public ImageView iv_userhead;
		public boolean isComMsg = true;
	}

	/**
	 * @Description
	 * @param name
	 */
//	private void playMusic(String name) {
//		try {
//			if (mMediaPlayer.isPlaying()) {
//				mMediaPlayer.stop();
//			}
//			mMediaPlayer.reset();
//			mMediaPlayer.setDataSource(name);
//			mMediaPlayer.prepare();
//			mMediaPlayer.start();
//			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//				public void onCompletion(MediaPlayer mp) {
//					mMediaPlayer.release();
//					mMediaPlayer = null;
//					animaition.stop();
//					viewHolder.tvContent
//							.setImageResource(R.drawable.question_add_record_icon_default);
//				}
//			});
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	private void stop() {

	}

//	@Override
//	public void onClick(View arg0) {
//		// TODO Auto-generated method stub
//		if (entity.getTextType().equals("unnomal") &&entity.getText().contains(".amr")) {
//			try {
//				if (mMediaPlayer.isPlaying()) {
//					mMediaPlayer.stop();
//				}
//				mMediaPlayer.reset();
//				mMediaPlayer.setDataSource(entity.getText());
//				mMediaPlayer.prepare();
//				mMediaPlayer.start();
//				mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//					public void onCompletion(MediaPlayer mp) {
//						mMediaPlayer.release();
//						mMediaPlayer = null;
//						animaition.stop();
//						tempTvContent.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.question_add_record_icon_default));
//					}
//					});
//				tempTvContent.setBackgroundDrawable(ctx.getResources().getDrawable(R.anim.playvoice));
//				animaition = (AnimationDrawable) tempTvContent
//						.getBackground();
//				// 最后，就可以启动动画了，代码如下：
//
//				// 是否仅仅启动一次？
//
//				animaition.setOneShot(false);
//
//				if (animaition.isRunning())// 是否正在运行？
//
//				{
//					animaition.stop();// 停止
//
//				}
//				animaition.start();// 启动
//					
//				
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

}
