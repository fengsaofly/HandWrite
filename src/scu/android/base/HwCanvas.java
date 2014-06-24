package scu.android.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * 自己写的手写板，暂未使用
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class HwCanvas extends View {
	private int vWidth;
	private int vHeight;
	private Bitmap bitmap;
	private Canvas bitCanvas;
	private Path path;
	private HwMatrix hwMatrix;
	private Paint paint;
	private int paintColor;
	private int paintSize;

	private float startX, startY, stopX, stopY;
	private Handler handler;
	private Runnable broadRunnable;

	public HwCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		vWidth = getWidth();
		vHeight = getHeight();
		paintColor = Color.RED;
		paintSize = vWidth / 100;
		paint = getPaint(paintColor, paintSize);
		hwMatrix = new HwMatrix();
		bitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
		bitCanvas = new Canvas(bitmap);
		path = new Path();
		handler = new Handler();
		broadRunnable = new BroadRunnable();
	}

	public Paint getPaint(int paintColor, int paintSize) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(paintColor);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(paintSize);// 设置画笔宽度
		paint.setDither(true);
		return paint;
	}

	class HwBitmap {
		Bitmap bitmap;
		int type;

		public HwBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
			this.type = 0;
		}
	}

	class DrawPath {
		public Path path;// 路径
		public Paint paint;// 画笔
	}

	class HwMatrix {

		private float maxX = 0;
		private float maxY = 0;
		private float minX = 0;
		private float minY = 0;

		public void init(float x, float y) {
			maxX = x;
			maxY = y;
		}

		public void setX(float x) {
			if (x < 0)
				return;
			if (x > maxX) {
				maxX = x;
			}
		}

		public void setY(float y) {
			if (y < 0)
				return;
			if (y > maxY) {
				maxY = y;
			}
		}

		public float getMaxX() {
			return maxX;
		}

		public float getMaxY() {
			return maxY;
		}

		public float getMinX() {
			return minX;
		}

		public float getMinY() {
			return minY;
		}

	}

	class BroadRunnable implements Runnable {

		@Override
		public void run() {
			Bitmap tmpBitmap = bitmap;
			// 1.得到绘制的区域坐标
			if (null != hwMatrix) {
				float maxX = hwMatrix.getMaxX();
				float minX = hwMatrix.getMinX();
				float maxY = hwMatrix.getMaxY();
				float minY = hwMatrix.getMinY();
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
				if (cutMaxX > tmpBitmap.getWidth()) {
					cutMaxX = tmpBitmap.getWidth() - 1;
				}
				if (cutMaxY > tmpBitmap.getHeight()) {
					cutMaxY = tmpBitmap.getHeight() - 1;
				}

				int width = (int) (cutMaxX - cutMinX);
				int height = (int) (cutMaxY - cutMinY);

				tmpBitmap = Bitmap.createBitmap(tmpBitmap, cutMinX, cutMinY,
						width, height);
			}
			hwMatrix = null;
			int dstWidth = 0;
			int dstHeight = vHeight / 10;
			if (tmpBitmap.getWidth() > getWidth() / 2) {
				dstWidth = tmpBitmap.getWidth() / 8;
			} else if (tmpBitmap.getWidth() > getWidth() / 4
					&& tmpBitmap.getWidth() < getWidth() / 2) {
				dstWidth = tmpBitmap.getWidth() / 6;
			} else if (tmpBitmap.getWidth() > getWidth() / 8
					&& tmpBitmap.getWidth() < getWidth() / 4) {
				dstWidth = tmpBitmap.getWidth() / 4;
			} else if (tmpBitmap.getWidth() > getWidth() / 16
					&& tmpBitmap.getWidth() < getWidth() / 8) {
				dstWidth = tmpBitmap.getWidth() / 2;
			} else if (tmpBitmap.getWidth() < getWidth() / 16) {
				dstWidth = tmpBitmap.getWidth();
			}
			if (dstWidth <= 0)
				dstWidth = 1;
			if (dstHeight <= 0)
				dstHeight = 1;
			Bitmap aBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth,
					dstHeight, true);
			HwDisView.addHwBitmap(new HwBitmap(aBitmap));
			path.reset();
			paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			bitCanvas.drawPaint(paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
			invalidate();
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		bitCanvas.drawPath(path, paint);
		canvas.drawBitmap(bitmap, 0, 0, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		startX = event.getX();
		startY = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			handler.removeCallbacks(broadRunnable);
			path.moveTo(startX, startY);
			break;
		case MotionEvent.ACTION_MOVE:
			path.quadTo(stopX, stopY, startX, startY);
			break;
		case MotionEvent.ACTION_UP:
			handler.postDelayed(broadRunnable, 800);
			break;
		}
		stopX = startX;
		stopY = startY;
		invalidate();
		return true;
	}

}