package scu.android.activity;

import scu.android.util.AppUtils;
import scu.android.util.BitmapUtils;
import scu.android.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.demo.note.R;
import com.edmodo.cropper.CropImageView;

/**
 * 图片剪切
 * 
 * @author YouMingyang
 * @version 1.0
 */
public class ImageCropperActivity extends Activity {

	private CropImageView cropImageView;
	private Intent intent;
	// Static final constants
	private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
	// private static final int ROTATE_NINETY_DEGREES = 90;
	// private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
	// private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
	// private static final int ON_TOUCH = 1;

	// Instance variables
	private int aspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
	private int aspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

	// // Saves the state upon rotating the screen/restarting the activity
	// @Override
	// protected void onSaveInstanceState(Bundle bundle) {
	// super.onSaveInstanceState(bundle);
	// bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
	// bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
	// }
	//
	// // Restores the state upon rotating the screen/restarting the activity
	// @Override
	// protected void onRestoreInstanceState(Bundle bundle) {
	// super.onRestoreInstanceState(bundle);
	// mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
	// mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actitvity_image_cropper);
		init();
	}

	public void init() {
		cropImageView = (CropImageView) findViewById(R.id.crop_image);
		cropImageView.setFixedAspectRatio(true);
		cropImageView.setAspectRatio(aspectRatioX, aspectRatioY);

		intent = getIntent();
		final String imgPath = intent.getStringExtra("imgPath");
		final int width = AppUtils.getWindowMetrics(this).widthPixels;
		final Bitmap image = BitmapUtils.getThumbnails(this, imgPath, width,
				width);
		// Sets initial aspect ratio to 10/10, for demonstration purposes
		cropImageView.setImageBitmap(image);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.imagecropper_actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.crop:
			final Bitmap croppedImage = cropImageView.getCroppedImage();
			final String cropPath = BitmapUtils.saveBitmap(this, croppedImage,
					Constants.CROP_DIR);
			intent.putExtra("cropPath", cropPath);
			setResult(Activity.RESULT_OK, intent);
			AppUtils.delete(intent.getStringExtra("imgPath"));
			ImageCropperActivity.this.finish();
			break;
		}
		return true;
	}

}
