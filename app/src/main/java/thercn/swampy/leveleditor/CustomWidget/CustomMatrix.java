package thercn.swampy.leveleditor.CustomWidget;

import android.graphics.Matrix;
import androidx.transition.MatrixUtils;

public class CustomMatrix extends Matrix {
	private Matrix matrix = new Matrix();
	private float centerX;
	private float centerY;
	public CustomMatrix(float centerX, float centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
	}
	@Override
	public boolean postRotate(float degrees) {
		matrix.postRotate(degrees);
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
		return true;
	}
	// 您可能还需要提供其他Matrix方法，例如preTranslate，postTranslate，preScale，postScale等的重写实现
	




}
