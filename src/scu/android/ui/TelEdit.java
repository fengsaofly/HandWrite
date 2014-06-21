package scu.android.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.EditText;
public class TelEdit extends EditText{
	Context mContext;
	public TelEdit(Context context) {
		super(context);	
		mContext = context;
	}
	
	public TelEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	public TelEdit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}
	protected void onDraw(Canvas canvas)
	{
		WindowManager wm = (WindowManager) mContext.getSystemService("window");
		int windowWidth = wm.getDefaultDisplay().getWidth();
		int windowHeight = wm.getDefaultDisplay().getHeight();
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		
		int paddingTop    = getPaddingTop();
		int paddingBottom = getPaddingBottom();
        
		int scrollY       = getScrollY();
		int scrollX       = getScrollX()+windowWidth;
		int innerHeight   = scrollY + getHeight() - paddingTop - paddingBottom;
		int lineHeight    = getLineHeight();
		int baseLine      = scrollY + (lineHeight - (scrollY % lineHeight));
		
		int x = 8;
		while (baseLine < innerHeight) {
			//canvas.drawBitmap(line, x, baseLine + paddingTop, paint);	
			canvas.drawLine(x, baseLine + paddingTop,scrollX, baseLine + paddingTop, paint);
			baseLine += lineHeight;
		}
		
		super.onDraw(canvas);
	}
}
