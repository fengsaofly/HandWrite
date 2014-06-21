package scu.android.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 解决PhotoView类的PointIndex out of range异常
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class PhotosViewPager extends ViewPager {

	public PhotosViewPager(Context context) {
		super(context);
	}

	public PhotosViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e) {
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return false;
	}

}
