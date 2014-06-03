package scu.android.note;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class DrawCanvas extends SurfaceView implements SurfaceHolder.Callback,
		OnTouchListener {

	private SurfaceHolder holder;
	private boolean run;
	private DrawThread drawThread;
	private Paint paint;

	public DrawCanvas(Context context) {
		super(context);
		holder = this.getHolder();
		holder.addCallback(this);
		this.setOnTouchListener(this);
		drawThread = new DrawThread();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.run = true;
		new Thread(drawThread).start();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(20);
		paint.setDither(true);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.run = false;
	}

	class DrawThread implements Runnable {
		@Override
		public void run() {
			while (run) {
				Canvas canvas = null;
				try {
					canvas = holder.lockCanvas();
//					canvas.drawLine(startX, startY, stopX, stopY, paint);
				} catch (Exception e) {
				} finally {
					if(canvas!=null){
						holder.unlockCanvasAndPost(canvas);
						holder.lockCanvas(new Rect(0,0,0,0));
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
		return false;
	}

}
