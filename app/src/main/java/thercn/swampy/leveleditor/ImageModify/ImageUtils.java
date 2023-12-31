package thercn.swampy.leveleditor.ImageModify;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import thercn.swampy.leveleditor.AppUtils.AppLog;

public class ImageUtils {
	public static void resizePNG(String inputFile, String outputFile) {
		Bitmap originalBitmap = BitmapFactory.decodeFile(inputFile);

		// 获取原始图片的宽度和高度
		int originalWidth = originalBitmap.getWidth();
		int originalHeight = originalBitmap.getHeight();

		// 计算放大后的宽度和高度
		int targetWidth = 384;
		int targetHeight = 512;
		if (targetWidth == originalWidth) {
			return;
		}
		if (targetHeight == originalHeight) {
			return;
		}

		// 创建一个新的Bitmap对象，并设定放大后的宽度和高度
		Bitmap resizedBitmap =
			Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

		// 将原始图片绘制到新的Bitmap对象中，实现放大功能
		Canvas canvas = new Canvas(resizedBitmap);
		RectF sourceRect = new RectF(0, 0, originalWidth, originalHeight);
		RectF targetRect = new RectF(0, 0, targetWidth, targetHeight);
		Matrix matrix = new Matrix();
		matrix.setRectToRect(sourceRect, targetRect, Matrix.ScaleToFit.FILL);
		canvas.drawBitmap(originalBitmap, matrix, null);

		// 最终获取放大后的图片
		Bitmap finalBitmap = resizedBitmap;
		File output = new File(outputFile);
		try {
			OutputStream outputimg = new FileOutputStream(output);
			finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputimg);
		} catch (FileNotFoundException e) {
			AppLog.WriteExceptionLog(e);
		}
	}
	// 用于检查大小是否相等
	public static boolean isSameSize(String inputFile) {
		Bitmap originalBitmap = BitmapFactory.decodeFile(inputFile);
		int originalWidth = originalBitmap.getWidth();
		int originalHeight = originalBitmap.getHeight();

		// 计算放大后的宽度和高度
		int targetWidth = 384;
		int targetHeight = 512;
		if (targetWidth == originalWidth) {
			return true;
		}
		if (targetHeight == originalHeight) {
			return true;
		}
		return false;
	}

	public static void RenderPNG(String levelPNGFile, int target,
								 String saveIMGFile) {
		String texture = "";
		int[] dirt = {112, 91, 49};
		int[] rock = {71, 71, 71};
		int[] rock_hilight = {160, 160, 160};
		int[] rock_shadow = {40, 40, 40};
		int r = 0, g = 0, b = 0;

		switch (target) {
			case 0:
				r = dirt[0];
				g = dirt[1];
				b = dirt[2];
				texture = "dirt";
				break;
			case 1:
				r = rock[0];
				g = rock[1];
				b = rock[2];
				texture = "rock";
				break;
			case 2:
				r = rock_hilight[0];
				g = rock_hilight[1];
				b = rock_hilight[2];
				texture = "rock_hilight";
				break;
			case 3:
				r = rock_shadow[0];
				g = rock_shadow[1];
				b = rock_shadow[2];
				texture = "rock_shadow";
				break;
		}

		Bitmap originalBitmap = BitmapFactory.decodeFile(levelPNGFile);
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();

		Bitmap modifiedBitmap =
			Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		int targetRGB = Color.rgb(r, g, b);
		Bitmap replacementBitmap =
			BitmapFactory.decodeFile("/sdcard/SLE/image/" + texture + ".png");
		int tolerance = 10;
		for (int i = 0; i < width * height; i++) {
			int x = i % width;
			int y = i / width;
			int pixel = originalBitmap.getPixel(x, y);

			int originalR = Color.red(pixel);
			int originalG = Color.green(pixel);
			int originalB = Color.blue(pixel);

			int diffR = Math.abs(Color.red(targetRGB) - originalR);
			int diffG = Math.abs(Color.green(targetRGB) - originalG);
			int diffB = Math.abs(Color.blue(targetRGB) - originalB);

			if (diffR <= tolerance && diffG <= tolerance && diffB <= tolerance) {
				int replacementPixel = replacementBitmap.getPixel(x, y);
				modifiedBitmap.setPixel(x, y, replacementPixel);
			} else {
				modifiedBitmap.setPixel(x, y, pixel);
			}
		}
		File outputFile = new File(saveIMGFile);
		try {
			OutputStream outputimg = new FileOutputStream(outputFile);
			modifiedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputimg);
			Runtime.getRuntime().exec("rm " + levelPNGFile);
		} catch ( FileNotFoundException e) {
			AppLog.WriteExceptionLog(e);
		} catch ( IOException e) {
			AppLog.WriteExceptionLog(e);
		}
	}

	public static boolean saveBitmapImage(Bitmap bitmap, String path) {
		try {
			File file = new File(path);
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}
			OutputStream bitmapOtputStream = new FileOutputStream(path);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapOtputStream);
			return true;
		} catch (FileNotFoundException e) {
			AppLog.WriteExceptionLog(e);
			return false;
		}
	}





}
