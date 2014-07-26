package scu.android.util;

import com.demo.note.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialogs {
	private static Button leftButton;
	private static Button rightButton;
	private static TextView textView; // 提示信息
	public static AlertDialog aDialog;

	/**
	 * 
	 * @param context
	 * @param title
	 *            弹出框 上面显示的文字
	 * @param btsString1
	 *            左边按钮的名字
	 * @param btString2
	 *            右边按钮的名字
	 * @param leftAction
	 *            左边按钮的点击事件 * @param rightAction 右边按钮的点击事件
	 * @param activity
	 *            activity
	 */
	public static void alertDialog(final Context context, String title,
			String btsString1, String btString2, final String leftAction,
			final String rightAction) {
		final View view;
		view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_common, null); // 自定义alertDialog布局
		leftButton = (Button) view.findViewById(R.id.bt1);
		rightButton = (Button) view.findViewById(R.id.bt2);
		leftButton.setText(btsString1);
		rightButton.setText(btString2);
		textView = (TextView) view.findViewById(R.id.d_title);
		textView.setText(title);
		if (aDialog != null && aDialog.isShowing()) { // 如果在显示，先关闭，防止弹出多个Dialog
			aDialog.dismiss();
		}
		aDialog = new AlertDialog.Builder(context).create(); // 创建一个AlertDialog对象
		try {
			aDialog.show(); // 显示AlertDialog
		} catch (Exception e) {
		}
		WindowManager.LayoutParams params = aDialog.getWindow().getAttributes(); // 得到AlertDialog属性
		params.gravity = Gravity.CENTER; // 显示其显示在中间
		params.width = (int) (ScreenSizeTool.getWidth() * 0.8); // 设置对话框的宽度为手机屏幕的0.8
		params.height = (int) (ScreenSizeTool.getHight() * 0.25);// 设置对话框的高度为手机屏幕的0.25
		aDialog.getWindow().setAttributes(params); // 設置屬性
		aDialog.getWindow().setContentView(view); // 把自定義view加上去

		leftButton.setOnClickListener(new OnClickListener() { // 左边按钮的点击事件
					/*
					 * 这里根据你自己的需求，自己定义和处理了
					 * 
					 * @see
					 * android.view.View.OnClickListener#onClick(android.view
					 * .View)
					 */
					@Override
					public void onClick(View v) {
						if ("no".equals(leftAction)) { // 我这里如果传进来的值等于no,就隱藏AlertDialog,不做其他事
							aDialog.dismiss();
						} else if ("resh".equals(leftAction) //  如果左边值等于resh，并且传进来的activity等于MainActivity，就执行doSomething方法
								&& context.getClass().getName()
										.equals("com.alertdialogs.MainActivity")) {
//							((MainActivity) context).doSomething();
							System.out.println("1");
							aDialog.dismiss();
						}
					}
				});
		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("hck","你好"+context.getClass().getName());
				if ("no".equals(rightAction)) { // 我这里如果传进来的值等于no,就隱藏AlertDialog
					aDialog.dismiss();
				} else if ("resh".equals(rightAction) //  如果左边值等于resh，并且传进来的activity等于MainActivity，就执行doSomething2方法
						&& context.getClass().getName()
								.equals("com.alertdialogs.MainActivity")) {
//					((MainActivity) context).doSomething2();
					System.out.println("2");
					aDialog.dismiss();
				}
			}
		});
		aDialog.setCancelable(false);
	}
}
