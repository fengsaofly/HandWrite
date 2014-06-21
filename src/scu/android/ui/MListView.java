package scu.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义ListView，解决ListView中嵌套只显示一行的问题
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class MListView extends ListView {
	public MListView(Context context) {
		super(context);
	}

	public MListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
