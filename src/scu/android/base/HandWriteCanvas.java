//package scu.android.base;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Bitmap.Config;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PorterDuff.Mode;
//import android.graphics.PorterDuffXfermode;
//import android.os.Handler;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.SurfaceHolder;
//import android.view.View;
//
///*
// * 手写板
// */
//public class HandWriteCanvas extends SuperCanvas {
//	private Handler handler;
//	private Runnable broadcastThread;// 通知显示进程
//	private String imgPath;
//
//	public HandWriteCanvas(Context context) {
//		super(context);
//	}
//
//	public HandWriteCanvas(Context context, AttributeSet attrs) {
//		super(context, attrs);
//	}
//
//	public HandWriteCanvas(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//	}
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		initPaint();
////		setZOrderOnTop(true);
////
////		holder.setFormat(PixelFormat.TRANSLUCENT);
//		
//		path = new Path();
//		maxX = maxY = 0;
//		minX = getWidth();
//		minY = getHeight();
//		bitmap = Bitmap.createBitmap(minX, minY, Config.ARGB_8888);
//		bitCanvas = new Canvas(bitmap);
//		drawThread = new DrawThread();
//		new Thread(drawThread).start();
//		handler = new Handler();
//		broadcastThread = new BroadcastThread();
//		this.canDraw = true;
//		this.isDrawing = false;
//		setFocusable(true);
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		canDraw = false;
//	}
//
//	public void initPaint() {
//		paint = new Paint();
//		paint.setAntiAlias(true);
//		paint.setColor(Color.BLUE);
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setStrokeJoin(Paint.Join.ROUND);
//		paint.setStrokeCap(Paint.Cap.ROUND);
//		paint.setStrokeWidth(getWidth() / 50);// 设置画笔宽度
//		paint.setDither(true);
//	}
//
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		startX = event.getX();
//		startY = event.getY();
//		if ((int) startX > maxX) {
//			maxX = (int) startX;
//		} else if ((int) startX < minX) {
//			minX = (int) startX;
//		}
//		if ((int) startY > maxY) {
//			maxY = (int) startY;
//		} else if ((int) startY < minY) {
//			minY = (int) startY;
//		}
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			this.isDrawing = true;
//			path.moveTo(startX, startY);
//			handler.removeCallbacks(broadcastThread);
//			Log.i("onTouch", "ACTION_DOWN");
//			break;
//		case MotionEvent.ACTION_MOVE:
//			path.quadTo(stopX, stopY, startX, startY);
//			break;
//		case MotionEvent.ACTION_UP:
//			this.isDrawing = false;
//			handler.postDelayed(broadcastThread, 800);
//			Log.i("onTouch", "ACTION_UP");
//			break;
//		}
//		stopX = startX;
//		stopY = startY;
//		return true;
//	}
//
//	// 发送图片通知，清空SurfaceView
//	class BroadcastThread implements Runnable {
//		@Override
//		public void run() {
//			Log.i("isDrawing", "" + isDrawing);
//			if (!isDrawing) {
//				// 通知显示
////				imgPath=BitmapUtils.saveBitmap(getContext(), bitmap, handwritePath, null, 100, 100);
////				Intent intent=new Intent();
////				intent.setAction("scu.android.base.handwriteCanvas");
////				intent.putExtra("imgPath", imgPath);
////				getContext().sendBroadcast(intent);
//				// 清空bitmap和path
//				path.reset();
//				paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
//				bitCanvas.drawPaint(paint);
//				paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
//				// 重绘
//				drawBitmap();
//			}
//		}
//	}
//
//	public String getImgPath(){
//		return imgPath;
//	}
//
//}
