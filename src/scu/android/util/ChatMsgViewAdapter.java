package scu.android.util;

import java.util.List;

import scu.android.application.MyApplication;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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

	private Context ctx;

	private LayoutInflater mInflater;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private String chatContact;

	public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> coll,String chatContact) {
		ctx = context;
		this.coll = coll;
		this.chatContact = chatContact;
		mInflater = LayoutInflater.from(context);
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
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (isComMsg) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
				viewHolder.iv_userhead = (ImageView)convertView
						.findViewById(R.id.iv_userhead);
				
				 Resources resources = ctx.getResources();
				 viewHolder.iv_userhead.setImageDrawable(resources.getDrawable(R.drawable.abaose));
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
				viewHolder.iv_userhead.setImageDrawable(resources.getDrawable(R.drawable.shuaige));
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
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvSendTime.setText(entity.getDate());
//		//检测到是音频
//		if (entity.getText().contains(".amr")) {
//			viewHolder.tvContent.setText("");
//			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
//			viewHolder.tvTime.setText(entity.getTime());
//		} else {
			viewHolder.tvContent.setText(entity.getText());			
			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			viewHolder.tvTime.setText("");
//		}
		viewHolder.tvContent.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				//点击文本，如果是音频则播放
//				if (entity.getText().contains(".amr")) {
//					playMusic(android.os.Environment.getExternalStorageDirectory()+"/"+entity.getText()) ;
//				}
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
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void stop() {

	}

}
