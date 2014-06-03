package scu.android.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FingerView extends View {

	FingerMatrix fingerMatrix; // 用于计算触摸矩阵坐标

	// 用于对外部activity设置画笔的对应属性
	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	/****************************/
	// 背景颜色
	public static int color = Color.parseColor("#6699CC");
	public static int srokeWidth = 15;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;// 画布的画笔
	private Paint mPaint;// 真实的画笔
	private float mX, mY;// 临时点坐标
	private static final float TOUCH_TOLERANCE = 4;

	// 保存Path路径的集合,用List集合来模拟栈
	private static List<DrawPath> savePath;// 这个存放路径和画笔的集合没有用到，在涂鸦中有用到
	// 记录Path路径的对象
	private DrawPath dp;

	private static List<Bitmap> bitmaps;

	private int screenWidth, screenHeight;// 屏幕長寬

	private class DrawPath {
		public Path path;// 路径
		public Paint paint;// 画笔
	}

	private static final int CUT_BITMAP_SEND_TO_ACTIVITY = 1;

	public FingerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		init(getWidth(), getHeight());
	}

	private void init(int width, int Height) {
		screenWidth = width;
		screenHeight = Height;
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		// 保存一次一次绘制出来的图形
		mCanvas = new Canvas(mBitmap);
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
		mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
		mPaint.setStrokeWidth(15);// 画笔宽度
		mPaint.setColor(color);

		savePath = new ArrayList<DrawPath>();
		bitmaps = new ArrayList<Bitmap>();

		timer = new Timer(true); // 初始化timer

		fingerMatrix = new FingerMatrix();
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		if (mPath != null) {
			canvas.drawPath(mPath, mPaint);
		}
	}

	private void touch_start(float x, float y) {
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(mY - y);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		mCanvas.drawPath(mPath, mPaint);
		savePath.add(dp);
		mPath = null;// 重新置空
	}

	/**
	 * 撤销的核心思想就是将画布清空， 将保存下来的Path路径最后一个移除掉， 重新将路径画在画布上面。
	 */
	public void undo() {
		mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
		// 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉…
		if (savePath != null && savePath.size() > 0) {
			// 移除最后一个path,相当于出栈操作
			savePath.remove(savePath.size() - 1);

			Iterator<DrawPath> iter = savePath.iterator();
			while (iter.hasNext()) {
				DrawPath drawPath = iter.next();
				mCanvas.drawPath(drawPath.path, drawPath.paint);
			}
			invalidate();// 刷新
		}
	}

	/**
	 * 重做的核心思想就是将撤销的路径保存到另外一个集合里面(栈)， 然后从redo的集合里面取出最顶端对象， 画在画布上面即可。
	 */
	public void redo() {
		// 如果撤销你懂了的话，那就试试重做吧。
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (null != task) {
				task.cancel(); // 将原任务从队列中移除
				task = new TimerTask() {
					public void run() {
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				};
			}
			if (null == fingerMatrix) {
				fingerMatrix = new FingerMatrix();
				// fingerMatrix.init(x, y);
				fingerMatrix.init(0, getHeight());
				System.out.println("fingerView的最大高度为：" + getHeight());
			} else {
				fingerMatrix.setX(x);
				fingerMatrix.setY(getHeight());
			}

			// 每次down下去重新new一个Path
			mPath = new Path();
			// 每一次记录的路径对象是不一样的
			dp = new DrawPath();
			dp.path = mPath;
			dp.paint = mPaint;
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if (null != task) {
				task.cancel(); // 将原任务从队列中移除
				task = new TimerTask() {
					public void run() {
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				};
			}
			if (null != fingerMatrix) {
				fingerMatrix.setX(x);
				fingerMatrix.setY(y);
			}

			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			if (null != fingerMatrix) {
				fingerMatrix.setX(x);
				fingerMatrix.setY(y);
			}

			touch_up();
			invalidate();

			if (null != timer) {
				if (null != task) {
					task.cancel(); // 将原任务从队列中移除
					task = new TimerTask() {
						public void run() {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
						}
					};
				}
				timer.schedule(task, 900, 900);
			} else {
				timer = new Timer(true);
				timer.schedule(task, 900, 900);
			}
			break;
		}
		return true;
	}

	Timer timer;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CUT_BITMAP_SEND_TO_ACTIVITY:
				Bitmap tempBitmap = mBitmap;
				// 1.得到绘制的区域坐标
				if (null != fingerMatrix) {
					float maxX = fingerMatrix.getMaxX();
					float minX = fingerMatrix.getMinX();
					float maxY = fingerMatrix.getMaxY();
					float minY = fingerMatrix.getMinY();
					int cutMinX = (int) (minX - 15);
					int cutMinY = (int) (minY - 15);
					int cutMaxX = (int) (maxX + 15);
					int cutMaxY = (int) (maxY + 15);
					// 处理设置裁剪位置
					if (cutMinX < 0) {
						cutMinX = 1;
					}
					if (cutMinY < 0) {
						cutMinY = 1;
					}
					if (cutMaxX > tempBitmap.getWidth()) {
						cutMaxX = tempBitmap.getWidth() - 1;
					}
					if (cutMaxY > tempBitmap.getHeight()) {
						cutMaxY = tempBitmap.getHeight() - 1;
					}

					int width = (int) (cutMaxX - cutMinX);
					int height = (int) (cutMaxY - cutMinY);

					tempBitmap = Bitmap.createBitmap(tempBitmap, cutMinX,
							cutMinY, width, height);
				}
				fingerMatrix = null;

				int dstWidth = 0; // 宽度通过计算缩放
				int dstHeight = getHeight() / 8; // 高度根据行高和显示的行数比例进行缩放

				if (tempBitmap.getWidth() > getWidth() / 2) {
					dstWidth = tempBitmap.getWidth() / 8;
				} else if (tempBitmap.getWidth() > getWidth() / 4
						&& tempBitmap.getWidth() < getWidth() / 2) {
					dstWidth = tempBitmap.getWidth() / 6;
				} else if (tempBitmap.getWidth() > getWidth() / 8
						&& tempBitmap.getWidth() < getWidth() / 4) {
					dstWidth = tempBitmap.getWidth() / 4;
				} else if (tempBitmap.getWidth() > getWidth() / 16
						&& tempBitmap.getWidth() < getWidth() / 8) {
					dstWidth = tempBitmap.getWidth() / 2;
				} else if (tempBitmap.getWidth() < getWidth() / 16) {
					dstWidth = tempBitmap.getWidth();
				}
				if (dstWidth <= 0)
					dstWidth = 1;
				if (dstHeight <= 0)
					dstHeight = 1;
				Bitmap mBitmap02 = Bitmap.createScaledBitmap(tempBitmap,
						dstWidth, dstHeight, true);
				mBitmap02.getWidth();
				mBitmap02.getHeight();
				bitmaps.add(mBitmap02);
				Intent intent = new Intent();
				intent.setAction("com.demo.new");
				getContext().sendBroadcast(intent);
				renovate();
				break;
			case 2:
				break;
			}
			super.handleMessage(msg);
		}
	};

	TimerTask task = new TimerTask() {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	/**
	 * 初始化数据并刷新屏幕
	 */
	private void renovate() {
		if (savePath != null && savePath.size() > 0) {
			savePath.removeAll(savePath);
			mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
					Bitmap.Config.ARGB_8888);
			mCanvas.setBitmap(mBitmap);
		}
		invalidate();// 刷新屏幕
		if (null != timer) {
			task.cancel();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mBitmap.recycle();
		mBitmap = null;
	}

	public List<Bitmap> getDataBitmaps() {
		return bitmaps;
	}
}