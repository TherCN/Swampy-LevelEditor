package thercn.swampy.leveleditor.CustomWidget;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ObjectView extends ImageView {
    private int mSmallImageCount;
    private String[] mSmallImagePaths;
    private String[] mSmallImageNames;
    private double[][] mSmallImagePositions;
    private OnObjectViewClickedListener mListener;
		private int mClickedIndex;
		private Bitmap ClickedImage[];
		private long touchStartTime;
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

    public void setData(String[] smallImagePaths, String[] smallImageNames, double[][] smallImagePositions) {
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
        for (int i = 0; i < mSmallImageCount; i++) {
            ClickedImage[i] = BitmapFactory.decodeFile(mSmallImagePaths[i]);
            canvas.drawBitmap(ClickedImage[i], (float) mSmallImagePositions[i][0], (float) mSmallImagePositions[i][1], paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
				switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
								touchStartTime = System.currentTimeMillis();
								for (int i = 0; i < mSmallImageCount; i++) {
										double[] position = mSmallImagePositions[i];
										if (event.getX() > (float) position[0] && event.getX() < ((float) position[0] + ClickedImage[i].getHeight()) 
												&& event.getY() > (float) position[1] && event.getY() < ((float) position[1]) + ClickedImage[i].getWidth()) {
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
}

