package scu.android.handwrite;

import java.util.ArrayList;
import java.util.List;
import scu.android.util.BitmapUtils;
import scu.android.util.Constants;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * 展示手写效果绘制的自定义view
 * 如果实例化此类，则可以会自动初始化initView()方法，当需要自定义文字颜色，背景颜色时，则调用initView()带参方法。
 */
@SuppressLint("HandlerLeak")
public class FingerShowView extends SurfaceView implements Callback {

	/* view高度 */
	private int viewHeight;
	/* view宽度 */
	private int viewWidth;
	/* 行高 */
	private int lineHeight;
	/* 行数 */
	private int lineCount = 10;
	/* 光标高度 */
	private int cursorHeight;
	/* 光标颜色 */
	private int cursorCorlor = Color.parseColor("#000000");;
	/* view背景颜色 */
	private int bgColor = Color.parseColor("#EEEEEE");
	/* 换行线与view边缘的距离 */
	private int paddingLine = 5;
	/**
	 * 针对空格和回车对应的bitmap 由于目前手机最大分辨率为1280*800 太大尺寸的bitmap会影响内存消耗
	 */
	/* 空格对应bitmap宽度 */
	public static final int spaceWidth = 999;
	/* 回车对应的bitmap宽度 */
	public static final int enterWidth = 888;
	/* 光标x轴位置 */
	private int xLocationCursor;

	/* 是否支持空格和换行 */
	private boolean hasEndter;
	/* 需要填充的数据bitmap集合 */
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private Bitmap bitmap;
	/* 光标是否显示 */
	public static boolean mIsDrawTime = true;
	/* 光标闪烁周期 */
	private long mFlashDelay = 500;
	private long mLastFlash = SystemClock.uptimeMillis();
	private SurfaceHolder mHolder;
	private Canvas canvas, bitCanvas;
	private LoopHandler mLoopHandler = new LoopHandler();
	/* 界面更新周期 */
	private static final int interval = 70;
	/* 画笔 */
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int spacesWidth = 50;// 空格的宽度
	private int cursorWidth = 3;// 光标的宽度
	private int linesColor = Color.GRAY;// 横线的颜色
	private int linesWidth = 2;// 横线的宽度
	// add bitmap的位置
	private int xLocation;
	private int yLocation;
	public int lines;
	public int linesTemp;
	private int baseLine;

	/**
	 * 初始化view，实例化view之后可以不调用该方法，当需要自定义颜色属性的时候才需要。
	 */
	public void initView() {
		// 计算行高
		lineHeight = viewHeight / lineCount;
		cursorHeight = lineHeight - 10;// 光标比行高少8xp
		baseLine = lineHeight;
		// 计算光标闪烁位置
		xLocationCursor = paddingLine;

		lines = 1;
		linesTemp = 1;

		xLocation = paddingLine;
		yLocation = 0;

	}

	/**
	 * 初始化view 该方法会仅自定义背景颜色和文字颜色，光标颜色和文字颜色显示相同，行数为默认值8行
	 * 
	 * @param bgColor
	 *            背景色
	 * @param textColor
	 *            文字颜色
	 */
	public void initView(int bgColor, int textColor) {
		this.bgColor = bgColor;
		this.cursorCorlor = textColor;
	}

	/**
	 * 初始化view 该方法光标显示颜色和文字颜色显示相同
	 * 
	 * @param lineCount
	 *            显示行数
	 * @param bgColor
	 *            背景颜色
	 * @param textColor
	 *            文字颜色
	 */
	public void initView(int lineCount, int bgColor, int textColor) {
		initView(bgColor, textColor);
		this.lineCount = lineCount;
		initView();
	}

	/**
	 * 初始化view 获得该view的宽度和高度
	 * 
	 * @param viewHeight
	 *            宽度
	 * @param viewWidth
	 *            高度
	 */
	public void initViewHW(int viewWidth, int viewHeight) {
		this.viewHeight = viewHeight;
		this.viewWidth = viewWidth;
		bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);
		bitCanvas = new Canvas(bitmap);
		initView();
	}

	/**
	 * 初始化view 该方法自定义各项参数，字体大小根据行数的大小进行判断
	 * 
	 * @param lineCount
	 *            显示行数
	 * @param bgColor
	 *            背景颜色
	 * @param textColor
	 *            文字颜色
	 * @param cursorCorlor
	 *            光标颜色
	 */
	public void initView(int lineCount, int bgColor, int textColor,
			int cursorCorlor) {
		initView(lineCount, bgColor, textColor);
		this.cursorCorlor = cursorCorlor;
	}

	public FingerShowView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHolder = getHolder();
		mHolder.addCallback(this);
		hasEndter = true;

		viewHeight = getHeight();
		viewWidth = getWidth();

		initView();
	}

	/**
	 * 在surface的大小发生改变时激发
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mLoopHandler.start();
	}

	/**
	 * 在创建时激发，一般在这里调用画图的线程。
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	/**
	 * 销毁时激发，一般在这里将画图的线程停止、释放。
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mLoopHandler.stop();
	}

	/**
	 * 界面实时更新 更新间隔都是自己用逻辑实现的
	 */
	class LoopHandler extends Handler {
		private boolean bStop = true;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (bStop == false) {
				loop();
			}
		}

		public void start() {
			// TODO Auto-generated method stub
			bStop = false;
			loop();
		}

		public void stop() {
			// TODO Auto-generated method stub
			bStop = true;
		}

		public void sleep(int i) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), i);
		}
	}

	public void loop() {
		mLoopHandler.sleep(interval / 10);
		doDraw();
	}

	private void doDraw() {
		canvas = mHolder.lockCanvas(null);
		initViewHW(canvas.getWidth(), canvas.getHeight());
		addBgLine(bitCanvas);
		addContent(bitCanvas);
		canvas.drawBitmap(bitmap, 0, 0, mPaint);
		mHolder.unlockCanvasAndPost(canvas);
		canvas = null;

	}

	/**
	 * 用于draw背景和lines
	 */
	private void addBgLine(Canvas canvas) {
		mPaint.setColor(bgColor);// 背景颜色
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);

		mPaint.setColor(linesColor);// 横线
		mPaint.setStrokeWidth(linesWidth);
		while (baseLine < canvas.getHeight()) {
			canvas.drawLine(paddingLine, baseLine, viewWidth - paddingLine,
					baseLine, mPaint);
			baseLine += lineHeight;
		}
		baseLine = lineHeight;
	}

	/**
	 * 用于显示bitmap集合中的图片
	 * 
	 * @param canvas
	 *            画布
	 */
	private void addContent(Canvas canvas) {
		if (bitmaps != null) {
			if (hasEndter) {
				for (int i = 0; i < bitmaps.size(); i++) {
					// 根据bitmap的宽度判断空格
					canvas.drawBitmap(bitmaps.get(i), xLocation, yLocation,
							null);
					if (bitmaps.get(i).getWidth() == spaceWidth) {
						xLocation += spacesWidth;
						int tempWidth = 0;
						if (bitmaps.size() >= i + 2) {
							if (bitmaps.get(i + 1).getWidth() == spaceWidth) {
								tempWidth = spacesWidth;
							} else if (bitmaps.get(i + 1).getWidth() == enterWidth) {

							} else {
								tempWidth = bitmaps.get(i + 1).getWidth();
							}
						}
						if (bitmaps.size() >= i + 2
								&& xLocation >= viewWidth - tempWidth) {
							xLocation = paddingLine;
							linesTemp++;
							yLocation += lineHeight;
						}
					} else if (bitmaps.get(i).getWidth() == enterWidth) {
						xLocation = paddingLine;
						linesTemp++;
						yLocation += lineHeight;
					} else {
						xLocation += bitmaps.get(i).getWidth();
						int tempWidth = 0;
						if (bitmaps.size() >= i + 2) {
							if (bitmaps.get(i + 1).getWidth() == spaceWidth) {
								tempWidth = spacesWidth;
							} else if (bitmaps.get(i + 1).getWidth() == enterWidth) {

							} else {
								tempWidth = bitmaps.get(i + 1).getWidth();
							}
						}
						if (bitmaps.size() >= i + 2
								&& xLocation >= viewWidth - tempWidth) {
							xLocation = paddingLine;
							linesTemp++;
							yLocation += lineHeight;
						}
					}

				}
				xLocationCursor = xLocation + paddingLine;
				lines = linesTemp;
				if (xLocationCursor >= viewWidth) {
					xLocationCursor = paddingLine;
					lines++;
				}
				xLocation = paddingLine;
				yLocation = 0;
				linesTemp = 1;
				if (lines == lineCount
						&& xLocationCursor > viewWidth - paddingLine
								- getWidth() / 8)
					return;
			} else {
				for (int i = 0; i < bitmaps.size(); i++) {

					canvas.drawBitmap(bitmaps.get(i), xLocation, yLocation,
							null);
					Log.i("IA", "" + bitmaps.get(i).getWidth() + "  "
							+ bitmaps.get(i).getHeight());
					xLocation += bitmaps.get(i).getWidth();
					if (bitmaps.size() >= i + 2
							&& xLocation >= viewWidth
									- bitmaps.get(i + 1).getWidth()) {
						xLocation = paddingLine;
						linesTemp++;
						yLocation += lineHeight;
					}
				}
				xLocationCursor = xLocation + paddingLine;
				lines = linesTemp;
				if (xLocationCursor >= viewWidth) {
					xLocationCursor = paddingLine;
					lines++;
				}
				xLocation = paddingLine;
				yLocation = 0;
				linesTemp = 1;
			}
		}
		drawCursor(canvas);// 根据draw的内容，判断光标的位置
	}

	/**
	 * 光标
	 * 
	 * @param canvas
	 */
	public void drawCursor(Canvas canvas) {
		if (mIsDrawTime) {
			// 画条黑线
			mPaint.setColor(cursorCorlor);// 光标
			mPaint.setStrokeWidth(cursorWidth);
			if (xLocationCursor == 2 * paddingLine) {
				xLocationCursor = paddingLine;
			}
			canvas.drawLine(xLocationCursor, (lineHeight - cursorHeight) / 2
					+ (lines - 1) * lineHeight, xLocationCursor,
					(lineHeight - cursorHeight) / 2 + (lines - 1) * lineHeight
							+ cursorHeight, mPaint);
			if (lines == 8 && xLocationCursor > viewWidth * 2 / 3) {
				Log.i("IA", "输入文字已满");// 不再向bitmap集合插入数据
			}
		} else {
			// 啥都不画
		}
		long time = SystemClock.uptimeMillis();
		if ((time - mLastFlash) >= mFlashDelay) {
			mIsDrawTime = !mIsDrawTime;
			// 保证定时刷新
			postInvalidateDelayed(mFlashDelay);
			mLastFlash = time;
		}
	}

	/**
	 * 清除所有展示内容
	 */
	public void clean() {
		if (bitmaps != null)
			bitmaps.clear();
	}

	public void withdraw() {
		if (bitmaps != null && bitmaps.size() >= 1)
			bitmaps.remove(bitmaps.size() - 1);
	}

	public void setDataBitmaps(List<Bitmap> bitmaps2) {
		this.bitmaps = bitmaps2;
	}

	/**
	 * 空格
	 */
	public void space() {
		if ((this.xLocationCursor + this.spacesWidth < this.viewWidth
				- paddingLine)
				|| this.lines < lineCount) {
			Bitmap spaceBitmap = Bitmap.createBitmap(spaceWidth, 1,
					Config.ARGB_8888);
			this.bitmaps.add(spaceBitmap);
		}
	}

	/**
	 * 换行
	 */
	public void newline() {
		if (this.lines < lineCount) {
			Bitmap enterBitmap = Bitmap.createBitmap(enterWidth, 1,
					Config.ARGB_8888);
			this.bitmaps.add(enterBitmap);
			this.lines++;
		}
	}

	// 保存
	public String getHandWritePath() {
		mIsDrawTime = false;
		doDraw();
		Bitmap dst = Bitmap.createBitmap(bitmap, 0, 0, viewWidth,
				lines < lineCount ? (lines * lineHeight + lineHeight / 2)
						: (lines * lineHeight - paddingLine));
		return BitmapUtils.saveBitmap(getContext(), dst,
				Constants.HANDWRITE_DIR);
	}

}