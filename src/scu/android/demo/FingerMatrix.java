package scu.android.demo;

/**
 * 用于计算手写缩放的矩阵
 */
public class FingerMatrix {

	private float maxX = 0;
	private float maxY = 0;
	private float minX = 0;
	private float minY = 0;

	public void init(float x, float y) {
		maxX = x;
		maxY = y;
	}

	public void setX(float x) {
		if (x < 0)
			return;
		if (x > maxX) {
			maxX = x;
		}
	}

	public void setY(float y) {
		if (y < 0)
			return;
		if (y > maxY) {
			maxY = y;
		}
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMaxY() {
		return maxY;
	}

	public float getMinX() {
		return minX;
	}

	public float getMinY() {
		return minY;
	}

}