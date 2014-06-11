package scu.android.ui;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import scu.android.application.MyApplication;
import scu.android.util.DecodeFile;
import scu.android.util.ImageAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.demo.note.R;
  
 
/** 
 * 图片浏览 
 * @author aokunsang 
 * @Date 2011-12-6 
 */  
public class PictureViewActivity extends Activity implements ViewFactory{  
      
   // private static String imgPath = Environment.getExternalStorageDirectory().getPath() + "/"+"tempPic";  
   // private static String imgPath = Environment.getExternalStorageDirectory().getPath() + "/"+"image" ;  
//    private static String imgPath = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera" ;
//	 private static String imgPath = "/mnt/sdcard/DCIM/";
	 private static String imgPath;
	 
    
    Button back = null;
    TextView selectPhoto = null;
    private ImageSwitcher imageSwitcher;    
    private Gallery gallery;  
    private List<String> photoList;  
    private int downX,upX;  
    private String newFilePath;  
    int width = 200;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photoscan);  
        imgPath =  ((MyApplication)getApplication()).getSDCardPath()+"/DCIM/";
        back = (Button)findViewById(R.id.find_photo_back);
        selectPhoto = (TextView)findViewById(R.id.find_selectPhoto);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        photoList = readFileList();  
        imageSwitcher = (ImageSwitcher)findViewById(R.id.switcher);  
        imageSwitcher.setFactory(this);  
        /* 
         * 淡入淡出效果 
         */  
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,    
                android.R.anim.fade_in));    
        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,    
                android.R.anim.fade_out));  
        imageSwitcher.setOnTouchListener(touchListener);  
        gallery = (Gallery)findViewById(R.id.gallery);  
        gallery.setAdapter(new ImageAdapter( photoList,this));  
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {  
            public void onItemSelected(AdapterView<?> arg0, View arg1,  
                    int position, long when) {  
            	
                newFilePath = photoList.get(position);  
                Bitmap bm = null;  
                File file = new File(newFilePath);
        		bm = DecodeFile.decodeFile(file,width-100);
        		if(bm!=null){
        			 BitmapDrawable bd = new BitmapDrawable(bm);  
                     imageSwitcher.setImageDrawable(bd);  
        		}
        			//bm =ZoomImage.zoomImage(bm, 100, 100);
//        		image.setImageBitmap(bm);
//        		image.setScaleType(ImageView.ScaleType.FIT_XY);
           
//        		image.setImageBitmap(bm);
               
            }  
            public void onNothingSelected(AdapterView<?> arg0) {}  
        });  
        
      
    }  
    
    
    public void myOnclick(View v){
    	switch(v.getId()){
    	case R.id.find_photo_back:
    		finish();
    		break;
    	case R.id.find_selectPhoto:
    		Intent data=new Intent();  
            data.putExtra("path", photoList.get(gallery.getSelectedItemPosition()));  
            
            //请求代码可以自己设置，这里设置成20  
            setResult(20, data);  
            //关闭掉这个Activity  
            finish();  
    		break;
    	}
    }
      
    /** 
     * 注册一个触摸事件 
     */  
    private OnTouchListener touchListener = new View.OnTouchListener() {  
        public boolean onTouch(View v, MotionEvent event) {  
             if(event.getAction()==MotionEvent.ACTION_DOWN)    
                {    
                    downX=(int) event.getX();//取得按下时的坐标    
                    return true;    
                }    
                else if(event.getAction()==MotionEvent.ACTION_UP)    
                {    
                    upX=(int) event.getX();//取得松开时的坐标    
                    int index=0;    
                    if(upX-downX>100)//从左拖到右，即看前一张    
                    {    
                        //如果是第一，则去到尾部    
                        if(gallery.getSelectedItemPosition()==0)    
                           index=gallery.getCount()-1;    
                        else    
                            index=gallery.getSelectedItemPosition()-1;    
                    }    
                    else if(downX-upX>100)//从右拖到左，即看后一张    
                    {    
                        //如果是最后，则去到第一    
                        if(gallery.getSelectedItemPosition()==(gallery.getCount()-1))    
                            index=0;    
                        else    
                            index=gallery.getSelectedItemPosition()+1;    
                    }    
                    //改变gallery图片所选，自动触发ImageSwitcher的setOnItemSelectedListener    
                    gallery.setSelection(index, true);    
                    return true;    
                }    
                return false;    
            }  
    };  
    /** 
     * 获取SD卡中的所有图片路径 
     * @return 
     */  
    private List<String> readFileList(){  
    	
      
    	       File sdDir = null; 
    	       boolean sdCardExist = Environment.getExternalStorageState()   
    	                           .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
    	       if   (sdCardExist)   
    	       {                               
    	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
    	       } 
    	       else {
    	    	   Toast.makeText(this, "sd卡不存在或不可用", 3).show();
    	    	   finish();
    	       }
    	     
    	       
    	
        List<String> fileList = new ArrayList<String>();  
//        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File fileDir = new File(imgPath+"Camera/");
        System.out.println("fileDir:   "+fileDir.getAbsolutePath()+"----------");
        System.out.println("imgPath:  "+fileDir.getAbsolutePath());
        File[] files = fileDir.listFiles();  
        if(files!=null){  
            for(File file:files){  
                String fileName = file.getName();
                System.out.println("fileName:  "+fileName);
                if (fileName.lastIndexOf(".") > 0    
                        && fileName.substring(fileName.lastIndexOf(".") + 1,    
                        fileName.length()).equals("jpg")){  
                    fileList.add(file.getPath());  
                }  
            }  
        }  
        return fileList;  
    }  
  
    public View makeView() {  
        ImageView imageView = new ImageView(this);  
        imageView.setScaleType(ImageView.ScaleType.CENTER);  
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(//自适应图片大小    
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));  
        return imageView;  
    }  
    
    
   
    

    
}  

