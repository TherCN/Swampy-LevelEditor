package thercn.swampy.leveleditor.CustomWidget;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import thercn.swampy.leveleditor.AppUtils.AppLog;
import thercn.swampy.leveleditor.ImageModify.ImageUtils;

public class ObjectView extends ImageView {
    private int mSmallImageCount;
    private String[] mSmallImagePaths;
    private String[] mSmallImageNames;
    private double[][] mSmallImagePositions;
	private double[] mSmallImageAngles;
    private OnObjectViewClickedListener mListener;
	private int mClickedIndex;
	private Bitmap ClickedImage[];
	private long touchStartTime;
	float centerX;
	float centerY;

	double scaleX = -5.5555555555555;
	double scaleY = 5.5555541666666;
	private float[] ObjectLocation = new float[2];
    public ObjectView(Context context) {
        super(context);
    }

    public ObjectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObjectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnObjectViewClickedListener(OnObjectViewClickedListener listener) {
        mListener = listener;
    }

    public void setData(String[] smallImagePaths, String[] smallImageNames, double[][] smallImagePositions, double[] samllImageAngles) {
		mSmallImageAngles = samllImageAngles;
        mSmallImagePaths = smallImagePaths;
        mSmallImageNames = smallImageNames;
        mSmallImagePositions = smallImagePositions;
        mSmallImageCount = smallImagePaths.length;
        mClickedIndex = -1; // 设置新的数据时重置点击索引
        invalidate(); // 重新绘制视图，以显示新的数据。
    }


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mSmallImagePaths == null || mSmallImagePaths.length == 0) {
			return;
		}
		Paint paint = new Paint();
		ClickedImage = new Bitmap[mSmallImageCount];

		centerX = canvas.getWidth() / 2.0f;
		centerY = canvas.getHeight() / 2.0f;

		for (int i = 0; i < mSmallImageCount; i++) {
			Matrix matrix = new Matrix();
			ClickedImage[i] = BitmapFactory.decodeFile(mSmallImagePaths[i]);
			/*
			double x = centerX - ClickedImage[i].getWidth() / 2;
			double y = centerY - ClickedImage[i].getHeight() / 2;
			*/
			
			Bitmap rotatedBitmap = rotateBitmap(ClickedImage[i], (float)mSmallImageAngles[i]);
			//ImageUtils.saveBitmapImage(rotatedBitmap,"/sdcard/testbmp/" + mSmallImageNames[i] + ".png");
			double x = centerX - rotatedBitmap.getWidth() / 2;
			double y = centerY - rotatedBitmap.getHeight() / 2;
			
			
			matrix.postTranslate((float)(x - scaleX * mSmallImagePositions[i][0]), (float)(y - scaleY * mSmallImagePositions[i][1]));
			canvas.drawBitmap(rotatedBitmap, matrix, paint);
		}
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchStartTime = System.currentTimeMillis();
				for (int i = 0; i < mSmallImageCount; i++) {
					ObjectLocation[0] = (float)((centerX - ClickedImage[i].getWidth() / 2) - mSmallImagePositions[i][0]);
					ObjectLocation[1] = (float)((centerX - ClickedImage[i].getHeight() / 2) - mSmallImagePositions[i][1]);

					double[] position = mSmallImagePositions[i];
					if (event.getX() > ObjectLocation[0] && event.getX() < (ObjectLocation[0] + ClickedImage[i].getHeight())
						&& 
						event.getY() > ObjectLocation[1] && event.getY() < (ObjectLocation[1] + ClickedImage[i].getWidth())) {
						mClickedIndex = i; // 记录点击的索引
						if (mListener != null) {
							mListener.onObjectViewClicked(mSmallImageNames[mClickedIndex]);
						}
						invalidate(); // 重新绘制视图，以更新点击状态
						return true;
					}

				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mClickedIndex >= 0 && mClickedIndex < mSmallImageCount && System.currentTimeMillis() - touchStartTime > 500) {
					double[] position = mSmallImagePositions[mClickedIndex];
					position[0] = ObjectLocation[0];
					position[1] = ObjectLocation[1];
					invalidate(); // 重新绘制视图，以更新位置
				}
				break;
			case MotionEvent.ACTION_UP:
				mClickedIndex = -1; // 重置点击索引
				invalidate(); // 重新绘制视图，以取消点击状态
				break;
		}
		return false;
	}

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mClickedIndex >= 0 && mClickedIndex < mSmallImageCount) {
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            double[] position = mSmallImagePositions[mClickedIndex];
            canvas.drawRect((float) position[0], (float) position[1], (float) position[0] + ClickedImage[mClickedIndex].getWidth(), (float) position[1] + ClickedImage[mClickedIndex].getHeight(), paint);
        }
    }

    public interface OnObjectViewClickedListener {
        void onObjectViewClicked(String name);
    }
	
	
	/*public Bitmap rotateBitmap(Bitmap bitmap,float angle) {
        try {
            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(new File("/sdcard/test.png"));
            // 创建一个新的BufferedImage，它的尺寸足够大以包含旋转后的图像
            int maxsize = originalImage.getWidth() + originalImage.getHeight();
            BufferedImage rotatedImage = new BufferedImage(maxsize,
														   maxsize, BufferedImage.TYPE_INT_ARGB);
            // 获取旋转图像的Graphics2D对象
            Graphics2D g = rotatedImage.createGraphics();
            // 定义旋转的角度（以弧度为单位）
            double var = Math.toRadians(angle);
            // 创建一个旋转矩阵并应用到Graphics2D对象
            AffineTransform at = new AffineTransform();
            at.translate(rotatedImage.getWidth() / 2, rotatedImage.getHeight() / 2);
            at.rotate(var);
            g.setTransform(at);
            // 将原始图像绘制到旋转后的图像上
            g.drawImage(originalImage, -originalImage.getWidth() / 2, -originalImage.getHeight() / 2,null);
						// 释放此图形的上下文以及它使用的所有系统资源
			g.dispose();
            // 将旋转后的图像写入新文件
            File outputfile = new File("/sdcard/out.png");
            ImageIO.write(rotatedImage, "png", outputfile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		return bitmap;
    }*/
	
	
	
	
	//有裁剪问题，正在努力寻找修复方法
	
 	public Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
		// 创建一个新的Bitmap，以适应旋转后的图片尺寸
		//int maxsize = bitmap.getWidth() + getHeight();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		double radians = Math.toRadians(degrees % 360);
		//if (radians < 0) {radians += Math.PI * 2;}
		int newWidth = (int) Math.round(Math.abs(width * Math.cos(degrees)) + (int) Math.abs(height * Math.sin(degrees)));
		int newHeight = (int) Math.round(Math.abs(width * Math.sin(degrees)) + (int) Math.abs(height * Math.cos(degrees)));	
		AppLog.WriteLog(newWidth + " " + newHeight + "," + degrees);
		Bitmap rotatedBitmap = Bitmap.createBitmap(35, 35, bitmap.getConfig());
		// 创建一个画布（Canvas）对象，将旋转后的图片绘制到该画布上
		Canvas canvas = new Canvas(rotatedBitmap);
		//double var = Math.toRadians(degrees);
		int centerx = canvas.getWidth() / 2;
		int centery = canvas.getHeight() / 2;
		
		canvas.rotate(-degrees,centerx, centery);
		//canvas.translate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
		//canvas.drawBitmap(bitmap,0,0,null);
		canvas.drawBitmap(bitmap, centerx - bitmap.getWidth() / 2, centery - bitmap.getHeight() / 2, null);
		//canvas.setMatrix(m);
		return rotatedBitmap;
	}

}
