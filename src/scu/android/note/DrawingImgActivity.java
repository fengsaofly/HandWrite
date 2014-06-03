package scu.android.note;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import com.demo.note.R;

 

public class DrawingImgActivity extends Activity {
    // SurfaceHolder负责维护SurfaceView上绘制的内容 
    private SurfaceHolder holder;
    private Paint paint;
    Bitmap bitmap = null;
    private float cur_x,cur_y;
    Canvas canvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        paint = new Paint();
        // 获取SurfaceView实例 
        SurfaceView surface = (SurfaceView) findViewById(R.id.surface);
        // 初始化SurfaceHolder对象 
        holder = surface.getHolder();
        holder.addCallback(new Callback() {
            //当surface将要被销毁时回调该方法 
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
  
            }
            //当surface被创建时回调该方法 
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // 锁定整个SurfaceView 
                Canvas canvas = holder.lockCanvas();
                // 获取背景资源 
               
				
                bitmap = Bitmap.createBitmap(canvas.getWidth(),
	            			   canvas.getHeight(), Bitmap.Config.ARGB_8888);
			
                
                
                // 绘制背景 
                canvas.drawBitmap(bitmap, 0, 0, null);
                // 绘制完成，释放画布，提交修改 
                holder.unlockCanvasAndPost(canvas);
                // 重新锁两次，避免下次lockCanvas遮挡 
                holder.lockCanvas(new Rect(0, 0, 0, 0));
                holder.unlockCanvasAndPost(canvas);
                holder.lockCanvas(new Rect(0, 0, 0, 0));
                holder.unlockCanvasAndPost(canvas);
            }
            //当一个surface的格式或大小发生改变时回调该方法 
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                    int width, int height) {
  
            }
        });
        surface.setOnTouchListener(new OnTouchListener() {
  
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 只处理按下事件 
            	
                
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    System.out.println("sdasdas");
//                }
            	switch(event.getAction()){
            		case  MotionEvent.ACTION_DOWN:
            			
            			
            			cur_x  = (int) event.getX();
                        cur_y  = (int) event.getY();
            			System.out.println("谁说的撒");
            			break;
            		case  MotionEvent.ACTION_MOVE:
            			System.out.println("移动了");
            		    float cx = event.getX();
                        float cy = event.getY();
//                        System.out.println(cx+"==="+cy);
                        // 锁定SurfaceView的局部区域，只更新局部内容 
                      
                        Canvas canvas = holder.lockCanvas(new Rect((int)cx-60,(int)cy-60,(int)cx+60,(int)cy+60));
                        // 保存canvas的当前状态 
                        canvas.save();
                        // 旋转画布 
                  //      canvas.rotate(30, cx, cy);
                        paint.setColor(Color.RED);
                        paint.setAntiAlias(true);
                        paint.setStrokeWidth(10);
                        canvas.drawLine(cur_x, cur_y, cx, cy, paint);
                        
                
               
                        // 绘制完成，释放画布，提交修改 
//                        canvas = holder.lockCanvas(new Rect(0,0,0,0));
                        holder.unlockCanvasAndPost(canvas);
                        
                        cur_x = cx;
                        cur_y = cy;
            			break;
            		case  MotionEvent.ACTION_UP:
            			break;
            	}
              
                return true;
            }
        });
    }
  
}