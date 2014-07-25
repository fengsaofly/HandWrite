package scu.android.util;

import com.demo.note.R;

import scu.android.application.MyApplication;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SideBar extends View {  
	 private char[] l;  
	    private SectionIndexer sectionIndexter = null;  
	    private ListView list;  
	    private TextView mDialogText;
	    
//	    private   int m_nItemHeight = (MyApplication.mHeight * 4/5 -30)/26;  
	    public int m_nItemHeight = 1;
	    public SideBar(Context context) {  
	        super(context); 
//	        DisplayMetrics dm = new DisplayMetrics();
//	         //获取屏幕信息
//	        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//	         
//	                int screenWidth = dm.widthPixels;
//	         
//	                int screenHeigh = dm.heightPixels;
//	                m_nItemHeight =  dm.heightPixels/26 - 3; 
	        m_nItemHeight = getHeight()/26;
	        init();  
	    }  
	    public SideBar(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	        m_nItemHeight = getHeight()/26;
	        init();  
	    }  
	    private void init() {  
	        l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',  
	                'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };  
	    }  
	    public SideBar(Context context, AttributeSet attrs, int defStyle) {  
	        super(context, attrs, defStyle); 
	        init();  
	    }  
	    public void setListView(ListView _list) {  
	        list = _list;  
	        sectionIndexter = (SectionIndexer) _list.getAdapter();  
	    }  
	    public void setTextView(TextView mDialogText) {  
	    	this.mDialogText = mDialogText;  
	    }  
	    public boolean onTouchEvent(MotionEvent event) {  
	        super.onTouchEvent(event);  
	        int i = (int) event.getY(); 
	        m_nItemHeight = getHeight()/26;
	        int idx = i / m_nItemHeight;  
	        if (idx >= l.length) {  
	            idx = l.length - 1;  
	        } else if (idx < 0) {  
	            idx = 0;  
	        }  
	        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {  
	        	mDialogText.setVisibility(View.VISIBLE);
	        	mDialogText.setBackgroundDrawable(getResources().getDrawable(R.drawable.contact_index_popup_bg));
	        	mDialogText.setText(""+l[idx]);
	            if (sectionIndexter == null) {  
	                sectionIndexter = (SectionIndexer) list.getAdapter();  
	            }  
	            int position = sectionIndexter.getPositionForSection(l[idx]);  
	            if (position == -1) {  
	                return true;  
	            }  
	            list.setSelection(position);  
	        }else{
	        	mDialogText.setVisibility(View.INVISIBLE);
	        }  
	        return true;  
	    }  
	    protected void onDraw(Canvas canvas) {  
	    	m_nItemHeight = getHeight()/26;
	        Paint paint = new Paint();  
	        paint.setColor(0xff595c61);  
	        paint.setTextSize(22);  
	        paint.setTextAlign(Paint.Align.CENTER);  
	        float widthCenter = getMeasuredWidth() / 2;  
	        for (int i = 0; i < l.length; i++) {  
	            canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);  
	        }  
	        super.onDraw(canvas);  
	    }  
}
