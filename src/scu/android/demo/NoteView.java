package scu.android.demo;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class NoteView extends SurfaceView implements SurfaceHolder.Callback {

	// 笔记
	private int noteWidth;
	private int noteHeight;
	private int noteColor = Color.parseColor("#ffffcc");// 笔记背景
	// 行
	private int linesWidth = 2;
	private int lineHeight;
	private int lineCount = 10;// 10行
	private int paddingLine = 5;
	private int linesColor = Color.parseColor("#000000");
	// 空格
	private static final int spaceWidth = 101;
	private static final int enterWidth = 102;
	private int spacesWidth = 20;
	// 光标
	private int caretWidth;
	private int caretHeight;
	private int caretColor;
	private int caretX;
	// 图片
	private int px;
	private int py;
	private ArrayList<Bitmap> bitmaps;// 手写图片

	private SurfaceHolder holder;
	private Canvas canvas;
	private Paint paint;
	private Runnable drawRunable;

	public NoteView(Context context, AttributeSet attrs) {
		super(context);
		init();
	}

	public void init() {
		lineHeight = noteHeight / lineCount;

		caretWidth = 3;
		caretHeight = lineHeight - 10;
		caretX = paddingLine;
		caretColor = Color.parseColor("#000000");

		noteWidth = getWidth();
		noteHeight = getHeight();

		holder = getHolder();
		holder.addCallback(this);
		paint=new Paint();
		bitmaps=new ArrayList<Bitmap>();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread(new DrawRunnable()).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	public void draw() {
		canvas = holder.lockCanvas();
		drawNoteAndLines(canvas);
		drawNotes(canvas);
		holder.unlockCanvasAndPost(canvas);
	}

	// 绘制行线
	public void drawNoteAndLines(Canvas canvas) {
		canvas.drawColor(noteColor);// 绘制笔记背景

		paint.setColor(linesColor);
		paint.setStrokeWidth(linesWidth);
		int baseLine = lineHeight;
		while (baseLine < canvas.getHeight()) {
			canvas.drawLine(paddingLine, baseLine, noteWidth - paddingLine,
					baseLine, paint);
			baseLine += lineHeight;
		}
		Log.i("drawLines", "drawLines"+noteHeight);
	}

	// 绘制手写图片
	public void drawNotes(Canvas canvas) {
		for (Bitmap bitmap : bitmaps) {
			canvas.drawBitmap(bitmap, px, py, paint);
			if (bitmap.getWidth() == spacesWidth) {
				px += spaceWidth;
			} else if (bitmap.getWidth() == enterWidth) {
				px = paddingLine;
				py += lineHeight;
			}
		}
		caretX = px;
		drawCaret(canvas);
	}

	public void drawCaret(Canvas canvas) {
		paint.setColor(caretColor);
		paint.setStrokeWidth(caretWidth);
		canvas.drawLine(caretX, py, caretX, py + caretHeight, paint);
	}
	
	public void clean(){
		bitmaps.clear();
	}

	private class DrawRunnable implements Runnable {

		@Override
		public void run() {
			while (true) {
				draw();
			}
		}

	}

}
