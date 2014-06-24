package scu.android.base;

import java.util.ArrayList;
import scu.android.util.BitmapUtils;
import scu.android.util.Constants;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 涂鸦板
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class DoodleCanvas extends SurfaceView implements
		SurfaceHolder.Callback, OnTouchListener {
	private SurfaceHolder holder;// 控制
	private Runnable drawThread;// 绘制线程
	private Canvas canvas, bitCanvas;// 绘制画布
	private boolean canDraw, isDrawing;// 绘制状态
	private Paint paint;
	private float startX, stopX, startY, stopY;// touch点
	private int minX, maxX, minY, maxY;// 截图大小
	private Bitmap bitmap;// 绘制图片
	private ArrayList<DrawPath> paths;// 路径
	private int paintColor;
	private int paintSize;
	private long lastDrawTime;

	public DoodleCanvas(Context context) {
		super(context);
		init();
	}

	public DoodleCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DoodleCanvas(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		paths = new ArrayList<DrawPath>();
		paintColor = Color.BLACK;
		paintSize = getWidth() / 50;
		paint = getPaint(paintColor, paintSize);
		paths.add(new DrawPath(new Path(), paint));
		maxX = maxY = 0;
		minX = getWidth();
		minY = getHeight();
		bitmap = Bitmap.createBitmap(minX, minY, Config.ARGB_8888);
		bitCanvas = new Canvas(bitmap);
		drawThread = new DrawThread();
		this.canDraw = this.isDrawing = true;
		lastDrawTime = SystemClock.uptimeMillis();
		new Thread(drawThread).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.canDraw = false;
	}

	public void init() {
		holder = getHolder();
		holder.addCallback(this);
		setOnTouchListener(this);
		canDraw = true;
	}

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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		startX = event.getX();
		startY = event.getY();
		DrawPath tmpDoodlePath = paths.get(paths.size() - 1);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			tmpDoodlePath.path.moveTo(startX, startY);
			Log.i("onTouch", "ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			tmpDoodlePath.path.quadTo(stopX, stopY, startX, startY);
			break;
		case MotionEvent.ACTION_UP:
			tmpDoodlePath = new DrawPath(new Path(), getPaint(paintColor,
					paintSize));
			synchronized (paths) {
				paths.add(tmpDoodlePath);
			}
			Log.i("onTouch", "ACTION_UP:" + paths.size());
			break;
		}
		stopX = startX;
		stopY = startY;
		return true;
	}

	// 撤销
	public void withdraw(boolean all) {
		if (!all) {
			if (paths.size() >= 2)
				paths.remove(paths.size() - 2);
		} else {
			while (paths.size() > 1)
				paths.remove(paths.size() - 2);
		}
		Log.i("withdraw", "withdraw:" + paths.size());

	}

	class DrawPath {
		public DrawPath(Path path, Paint paint) {
			this.paint = paint;
			this.path = path;
		}

		Path path;
		Paint paint;

	}

	public void drawPath(Canvas canvas) {
		synchronized (paths) {
			canvas.drawColor(Color.WHITE);
			for (DrawPath doodlePath : paths)
				canvas.drawPath(doodlePath.path, doodlePath.paint);
		}
	}

	// 绘图
	public void drawBitmap() {
		try {
			canvas = holder.lockCanvas();
			if (canvas != null) {
				drawPath(bitCanvas);
				canvas.drawBitmap(bitmap, 0, 0, paint);
			}
		} catch (Exception e) {
		} finally {
			if (canvas != null) {
				holder.unlockCanvasAndPost(canvas);

			}
		}
	}

	// 绘制线程
	class DrawThread implements Runnable {
		@Override
		public void run() {
			while (canDraw) {
				if (isDrawing) {
					long now = SystemClock.uptimeMillis();
					if (now - lastDrawTime >= 100) {
						drawBitmap();
					}
				}
			}
		}
	}

	// 保存涂鸦
	public String getDoodlePath() {
		canDraw = false;
		return BitmapUtils.saveBitmap(getContext(), bitmap,
				Constants.DOODLE_DIR);
	}

	public void setPaintColor(String color) {
		this.paintColor = Color.parseColor(color);
		this.paths.get(paths.size() - 1).paint.setColor(paintColor);
	}

	public int getPaintSize() {
		return paintSize;
	}

	public void setPaintSize(int paintSize) {
		this.paintSize = paintSize;
		this.paths.get(paths.size() - 1).paint.setStrokeWidth(paintSize);
	}

}
