package scu.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DecodeFile {
	public static Bitmap decodeFile(File f,int REQUIRED_SIZE){
	        try {
	            //����ͼƬ��С
	            BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	            //������Ҫ���µ�ͼƬ��С
	           // final int REQUIRED_SIZE=70;
	            int scale=1;
	            while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	                scale*=2;

	            //��inSampleSize����
	            BitmapFactory.Options o2 = new BitmapFactory.Options();
	            o2.inSampleSize=scale;
	            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	        } catch (FileNotFoundException e) {}
	        return null;
	    }

}
