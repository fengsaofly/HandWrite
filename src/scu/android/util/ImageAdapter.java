package scu.android.util;



import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private List<String> photoList; // 图片地址list
	private Context context;

	public ImageAdapter(List<String> photoList, Context context) {
		this.photoList = photoList;
		this.context = context;
	}

	public void addImage(String imageUrl) {
		photoList.add(imageUrl);
	}

	public int getCount() {
		return photoList.size();
	}

	public Object getItem(int position) {
		return photoList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView image = new ImageView(context);
		String newFilePath = photoList.get(position); 
		Bitmap bm = null;

//		BitmapFactory.Options optsa = new BitmapFactory.Options();
//		optsa.inSampleSize = 10;
		File file = new File(newFilePath);
		bm = DecodeFile.decodeFile(file,200);
		if(bm!=null)
			//bm =ZoomImage.zoomImage(bm, 100, 100);
//		image.setImageBitmap(bm);
//		image.setScaleType(ImageView.ScaleType.FIT_XY);
   
		image.setImageBitmap(bm);
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		image.setLayoutParams(new Gallery.LayoutParams(100, 100));
		bm=null;
		return image;

	}

}

