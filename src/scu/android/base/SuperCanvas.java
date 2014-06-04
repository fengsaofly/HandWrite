package scu.android.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View.OnTouchListener;

/*
 * 画板基类
 */
public abstract class SuperCanvas extends SurfaceView implements
		SurfaceHolder.Callback, OnTouchListener {

	SurfaceHolder holder;// 控制
	boolean canDraw, isDrawing;// 绘制状态
	Runnable drawThread;// 绘制线程
	Canvas canvas, bitCanvas;// 绘制画布
	Paint paint;// 画笔
	float startX, stopX, startY, stopY;// touch点
	int minX, maxX, minY, maxY;// 截图大小
	Path path;// 路径
	Bitmap bitmap;// 绘制图片

	public SuperCanvas(Context context) {
		super(context);
		init();
	};

	public SuperCanvas(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init();
	}

	public SuperCanvas(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void init() {
		holder = getHolder();
		holder.addCallback(this);
		setOnTouchListener(this);
		canDraw = true;
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
		canvas.drawPath(path, paint);
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
					drawBitmap();
				}
			}
		}
	}

	
}
