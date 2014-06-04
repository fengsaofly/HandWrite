package scu.android.base;

import java.util.ArrayList;

import scu.android.util.AppUtils;
import scu.android.util.BitmapUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

public class DoodleCanvas extends SuperCanvas {

	private ArrayList<DrawPath> paths;// 路径
	private Paint tmpPaint;

	public DoodleCanvas(Context context) {
		super(context);
	}

	public DoodleCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DoodleCanvas(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initTmpPaint();
		paths = new ArrayList<DrawPath>();
		paths.add(new DrawPath(new Path(), tmpPaint));
		maxX = maxY = 0;
		minX = getWidth();
		minY = getHeight();
		bitmap = Bitmap.createBitmap(minX, minY, Config.ARGB_8888);
		bitCanvas = new Canvas(bitmap);
		drawThread = new DrawThread();
		new Thread(drawThread).start();
		this.canDraw = this.isDrawing = true;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.canDraw = false;
	}

	public void initTmpPaint() {
		tmpPaint = new Paint();
		tmpPaint.setAntiAlias(true);
		tmpPaint.setColor(Color.RED);
		tmpPaint.setStyle(Paint.Style.STROKE);
		tmpPaint.setStrokeJoin(Paint.Join.ROUND);
		tmpPaint.setStrokeCap(Paint.Cap.ROUND);
		tmpPaint.setStrokeWidth(getWidth() / 50);// 设置画笔宽度
		tmpPaint.setDither(true);
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
			tmpDoodlePath = new DrawPath(new Path(), tmpPaint);
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

	public void drawPath(Canvas canvas) {
		synchronized (paths) {
			canvas.drawColor(Color.WHITE);
			for (DrawPath doodlePath : paths)
				canvas.drawPath(doodlePath.path, doodlePath.paint);
		}
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

	// 保存涂鸦
	public String getDoodlePath() {
		return BitmapUtils.saveBitmap(getContext(), bitmap,
				AppUtils.DOODLE_DIR, new Rect(), getWidth(), getHeight());
	}
}
