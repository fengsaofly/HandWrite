package scu.android.base;

import java.util.ArrayList;

import scu.android.base.HwCanvas.HwBitmap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
/**
 * 显示手写图片，暂未使用
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class HwDisView extends SurfaceView implements Callback {

	private int vWidth, vHeight;// view宽。高
	private int lHeight;// 行高
	private int lCount;
	private int lColor;
	private int lSize;
	private int cHeight;// 光标高度
	private int cColor;// 光标颜色
	private int cWidth;
	private int vColor;// view背景颜色
	private int lPadding;// 行边距
	private int lines;// 行数
	private int cX, cY;// 光标位置
	private int bX, bY;// 手写图片插入位置
	private boolean canDrawCaret;
	private boolean canSet;
	private long lastTime;
	private long cDelay = 500;// 光标刷新
	private long vDelay = 100;

	private Thread drawThread;
	public static ArrayList<HwBitmap> hwBitmaps;// 手写图片
	private Bitmap bitmap;//
	private SurfaceHolder holder;
	private Canvas canvas, bitCanvas;
	private Paint paint;
	private int paintColor;// 画笔颜色
	private int paintSize;// 画笔粗细
	private Handler handler;

	public HwDisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder = getHolder();
		holder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		vWidth = getWidth();
		vHeight = getHeight();
		vColor = Color.parseColor("#EEEEEE");
		lCount = 9;
		lSize = 2;
		lHeight = vHeight / lCount;
		lColor = Color.LTGRAY;
		lPadding = 10;
		cWidth = 2;
		cHeight = lHeight - 10;
		cColor = Color.BLACK;
		cX = lPadding;
		paintColor = Color.BLACK;
		lines = 1;
		lastTime = SystemClock.uptimeMillis();
		canDrawCaret = false;
		canSet = true;

		paintSize = getWidth() / 100;
		paint = getPaint(paintColor, paintSize);
		bitmap = Bitmap.createBitmap(vWidth, vHeight, Config.ARGB_8888);
		hwBitmaps = new ArrayList<HwCanvas.HwBitmap>();
		bitCanvas = new Canvas(bitmap);
		drawThread = new Thread(new DrawRunnable());
		drawThread.start();// 绘制线程开启
		handler = new Handler();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (drawThread.isAlive()) {
			drawThread.interrupt();// 线程退出
		}
	}

	// 获取画笔
	public Paint getPaint(int paintColor, int paintSize) {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(paintColor);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(paintSize);// 设置画笔宽度
		paint.setDither(true);
		return paint;
	}

	// 绘制光标
	public void drawCaret(final Canvas canvas) {
		if (canDrawCaret) {
			paint.setColor(cColor);
			paint.setStrokeWidth(cWidth);
			if (lines <= 10) {
				canvas.drawLine(cX, (lHeight - cHeight) / 2 + (lines - 1)
						* lHeight, cX, (lHeight - cHeight) / 2 + (lines - 1)
						* lHeight + cHeight, paint);
				System.out.println((lHeight - cHeight) / 2 + (lines - 1)
						* lHeight);
			}
			canDrawCaret = false;
			canSet = true;
		}
		if (!canDrawCaret && canSet) {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					canDrawCaret = true;
				}
			}, cDelay);
		}
		canSet = false;
	}

	// 绘制笔记线条
	public void drawLines(Canvas canvas) {
		canvas.drawColor(vColor);
		paint.setColor(lColor);
		paint.setStrokeWidth(lSize);
		int baseline = lHeight;
		while (baseline < vHeight) {
			canvas.drawLine(lPadding, baseline, vWidth - lPadding, baseline,
					paint);
			baseline += lHeight;
		}
	}

	// 绘制手写图片
	public void drawHwBitmap(Canvas canvas) {
		cX = lPadding;
		bX = lPadding;
		bY = 0;
		lines = 1;
		synchronized (hwBitmaps) {
			for (int i = 0; i < hwBitmaps.size(); i++) {
				HwBitmap hwBitmap = hwBitmaps.get(i);
				canvas.drawBitmap(hwBitmap.bitmap, bX, bY, null);
				switch (hwBitmap.type) {
				case 0:
					bX += hwBitmap.bitmap.getWidth();
					if (hwBitmaps.size() >= i + 2) {
						HwBitmap nHwBitmap = hwBitmaps.get(i + 1);
						int tmpWidth = 0;
						switch (nHwBitmap.type) {
						case 0:
							tmpWidth = nHwBitmap.bitmap.getWidth();
							break;
						case 1:
							tmpWidth = nHwBitmap.bitmap.getWidth();
							break;
						case 2:
							break;
						}
						if (bX >= vWidth - tmpWidth) {
							bX = lPadding;
							lines++;
							bY += lHeight;
						}
					}
					break;
				case 1:

					break;
				case 2:
					bX = lPadding;
					bY += lHeight;
					lines++;
					break;
				}
			}
			cX = bX;
			drawCaret(canvas);
		}
	}

	public void onDraw() {
		canvas = holder.lockCanvas();
		drawLines(bitCanvas);
		drawHwBitmap(bitCanvas);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		holder.unlockCanvasAndPost(canvas);
	}

	class DrawRunnable implements Runnable {

		@Override
		public void run() {
			while (true) {
				long time = SystemClock.uptimeMillis();
				if ((time - lastTime) > vDelay) {
					onDraw();
					lastTime = time;
				}
			}
		}

	}

	public static void addHwBitmap(HwBitmap hwBitmap) {
		synchronized (hwBitmaps) {
			hwBitmaps.add(hwBitmap);
		}
	}

}
