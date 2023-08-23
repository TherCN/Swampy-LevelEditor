package thercn.swampy.leveleditor.CustomContent;

// generic by ChatGPT
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ImageEditorView extends View {
  private Bitmap mBitmap;
  private Canvas mCanvas;
  private Path mPath;
  private Paint mPaint;

  public ImageEditorView(Context context) {
    super(context);
    init();
  }

  public ImageEditorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    mPath = new Path();
    mPaint = new Paint();
    mPaint.setColor(Color.RED);
    mPaint.setStrokeWidth(5);
    mPaint.setStyle(Paint.Style.STROKE);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    mCanvas = new Canvas(mBitmap);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawBitmap(mBitmap, 0, 0, null);
    canvas.drawPath(mPath, mPaint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    float x = event.getX();
    float y = event.getY();

    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
      mPath.moveTo(x, y);
      break;
    case MotionEvent.ACTION_MOVE:
      mPath.lineTo(x, y);
      break;
    case MotionEvent.ACTION_UP:
      mCanvas.drawPath(mPath, mPaint);
      mPath.reset();
      break;
    }

    invalidate();
    return true;
  }
}
