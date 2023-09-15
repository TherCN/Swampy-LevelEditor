package thercn.swampy.leveleditor.CustomWidget;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ObjectView extends ImageView {
    private int mSmallImageCount;
    private String[] mSmallImagePaths;
    private String[] mSmallImageNames;
    private float[][] mSmallImagePositions;
	private double[] mSmallImageAngles;
    private OnObjectViewClickedListener mListener;
	private int mClickedIndex;
	private Bitmap ClickedImage[];
	private long touchStartTime;
	float centerX;
	float centerY;

	double scaleX = -5.5555555555555;
	double scaleY = 5.5555541666666;
	
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

    public void setData(String[] smallImagePaths, String[] smallImageNames, float[][] smallImagePositions, double[] samllImageAngles) {
		mSmallImageAngles = samllImageAngles;
        mSmallImagePaths = smallImagePaths;
        mSmallImageNames = smallImageNames;
		mSmallImageCount = smallImagePaths.length;
		mSmallImagePositions = smallImagePositions;
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
		
		for (int i = 0; i < mSmallImageCount; i++) {
			ClickedImage[i] = BitmapFactory.decodeFile(mSmallImagePaths[i]);
			Bitmap rotatedBitmap = rotateBitmap(ClickedImage[i], (float)mSmallImageAngles[i]);
			//matrix.postTranslate((float)(x - scaleX * mSmallImagePositions[i][0]), (float)(y - scaleY * mSmallImagePositions[i][1]));
			//Log.e("SLE",mSmallImagePositions[i][0] + " " + mSmallImagePositions[i][1]);
			canvas.drawBitmap(rotatedBitmap, mSmallImagePositions[i][0], mSmallImagePositions[i][1], paint);
		}
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		float centerX = getWidth() / 2;
		float centerY = getHeight() / 2;
		float relativeX = x - centerX;
		float relativeY = y - centerY;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchStartTime = System.currentTimeMillis();
				for (int i = 0; i < mSmallImageCount; i++) {
					float[] position = mSmallImagePositions[i];
					if (event.getX() > position[0] && event.getX() < (position[0] + ClickedImage[i].getHeight())
						&& 
						event.getY() > position[1] && event.getY() < (position[1] + ClickedImage[i].getWidth())) {
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
					float[] position = mSmallImagePositions[mClickedIndex];
					position[0] = event.getX() - ClickedImage[mClickedIndex].getWidth() / 2;
					position[1] = event.getY() - ClickedImage[mClickedIndex].getHeight() / 2;
					invalidate(); // 重新绘制视图，以更新位置

				}
				break;
			case MotionEvent.ACTION_UP:

				mClickedIndex = -1; // 重置点击索引
				invalidate(); // 重新绘制视图，以取消点击状态
				break;
		}
		return true;
	}

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mClickedIndex >= 0 && mClickedIndex < mSmallImageCount) {
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            float[] position = mSmallImagePositions[mClickedIndex];
            canvas.drawRect(position[0], position[1], position[0] + ClickedImage[mClickedIndex].getWidth(), position[1] + ClickedImage[mClickedIndex].getHeight(), paint);
        }
    }

    public interface OnObjectViewClickedListener {
        void onObjectViewClicked(String name);
    }

	public float[] fixLocation(float[] loacation, Bitmap bitmapInfo) {
		float[] returnLocation = new float[2];

		float x = getWidth() / 2 - bitmapInfo.getWidth() / 2;
		float y = getHeight() / 2 - bitmapInfo.getHeight() / 2;
		returnLocation[0] = (float)(x - scaleX * loacation[0]);
		returnLocation[1] = (float)(y - scaleY * loacation[1]);

		return returnLocation;
	}



	//有缩放问题，先凑活着用吧
	//TODO:Please try a better solution to replace here
 	public Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		//int newWidth = (int) Math.round(Math.abs(width * Math.cos(degrees)) + (int) Math.abs(height * Math.sin(degrees)));
		//int newHeight = (int) Math.round(Math.abs(width * Math.sin(degrees)) + (int) Math.abs(height * Math.cos(degrees)));	
		//AppLog.WriteLog(newWidth + " " + newHeight + "," + degrees);
		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap.getWidth() + 10, bitmap.getHeight() + 10, bitmap.getConfig());
		// 创建一个画布（Canvas）对象，将旋转后的图片绘制到该画布上
		Canvas canvas = new Canvas(rotatedBitmap);
		int centerx = canvas.getWidth() / 2;
		int centery = canvas.getHeight() / 2;

		canvas.rotate(-degrees, centerx, centery);
		canvas.drawBitmap(bitmap, centerx - bitmap.getWidth() / 2, centery - bitmap.getHeight() / 2, null);
		return rotatedBitmap;
	}

}
