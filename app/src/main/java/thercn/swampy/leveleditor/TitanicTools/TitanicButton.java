package thercn.swampy.leveleditor.TitanicTools;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class TitanicButton extends Button {
    // callback fired at first onSizeChanged
    private AnimationSetupCallback animationSetupCallback;
    // wave shader coordinates
    private float maskX, maskY;
    // if true, the shader will display the wave
    private boolean sinking;
    // true after the first onSizeChanged
    private boolean setUp;

    // shader containing a repeated wave
    private BitmapShader shader;
    // shader matrix
    private Matrix shaderMatrix;
    // wave drawable
    private android.graphics.drawable.Drawable wave;
    // (getHeight() - waveHeight) / 2
    private float offsetY;

    public TitanicButton(Context context) {
        super(context);
        init();
    }

    public TitanicButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitanicButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        shaderMatrix = new Matrix();
    }

    public AnimationSetupCallback getAnimationSetupCallback() {
        return animationSetupCallback;
    }

    public void setAnimationSetupCallback(AnimationSetupCallback animationSetupCallback) {
        this.animationSetupCallback = animationSetupCallback;
    }

    public float getMaskX() {
        return maskX;
    }

    public void setMaskX(float maskX) {
        this.maskX = maskX;
        invalidate();
    }

    public float getMaskY() {
        return maskY;
    }

    public void setMaskY(float maskY) {
        this.maskY = maskY;
        invalidate();
    }

    public boolean isSinking() {
        return sinking;
    }

    public void setSinking(boolean sinking) {
        this.sinking = sinking;
    }

    public boolean isSetUp() {
        return setUp;
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        createShader(getContext());
    }

    @Override
    public void setTextColor(android.content.res.ColorStateList colors) {
        super.setTextColor(colors);
        createShader(getContext());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        createShader(getContext());

        if (!setUp) {
            setUp = true;
            if (animationSetupCallback != null) {
                animationSetupCallback.onSetupAnimation(TitanicButton.this);
            }
        }
    }

    /**
     * Create the shader
     * draw the wave with current color for a background
     * repeat the bitmap horizontally, and clamp colors vertically
     */
    @SuppressWarnings("deprecation")
    private void createShader(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            Drawable img = Drawable.createFromStream(assetManager.open("rainbow.png"), null);

            if (wave == null) {
                wave = img;
            }

            int waveW = wave.getIntrinsicWidth();
            int waveH = wave.getIntrinsicHeight();

            Bitmap b = Bitmap.createBitmap(waveW, waveH, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);

            c.drawColor(getCurrentTextColor());

            wave.setBounds(0, 0, waveW, waveH);
            wave.draw(c);

            shader = new BitmapShader(b, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
            getPaint().setShader(shader);

            offsetY = (getHeight() - waveH) / 2;
        } catch (IOException iOException) {
            Toast.makeText(context, iOException.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // modify text paint shader according to sinking state
        if (sinking && shader != null) {

            // first call after sinking, assign it to our paint
            if (getPaint().getShader() == null) {
                getPaint().setShader(shader);
            }

            // translate shader accordingly to maskX maskY positions
            // maskY is affected by the offset to vertically center the wave
            shaderMatrix.setTranslate(maskX, maskY + offsetY);

            // assign matrix to invalidate the shader
            shader.setLocalMatrix(shaderMatrix);
        } else {
            getPaint().setShader(null);
        }

        super.onDraw(canvas);
    }
}
