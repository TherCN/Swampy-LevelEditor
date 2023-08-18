package thercn.swampy.leveleditor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import thercn.swampy.leveleditor.R;
import android.widget.FrameLayout;

public class EditLevel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_editor);
        loadImage();
    }

    public void loadImage() {
        String OpenLevel = getIntent().getStringExtra("LevelName");
        String Custom_PNG_Path = getIntent().getStringExtra("LevelPNGPath");
        //File Custom_PNG_path = new File(Custom_PNG_Path);
        String levelimage_path = NewLevel.LevelsDir + "/" + OpenLevel + "/" + OpenLevel + ".png";
        if (Custom_PNG_Path != "") {
            try {
                Runtime.getRuntime().exec("cp " + Custom_PNG_Path + " " + levelimage_path);
            } catch (IOException e) {}
        }

        Bitmap levelimage = BitmapFactory.decodeFile(levelimage_path);
        FrameLayout FL = findViewById(R.id.leveleditorFrameLayout1);
        final ImageView level_image = findViewById(R.id.level_image);
        //FL.setY(levelimage.getHeight() * (float)4.2666666666666);
        //level_image.setY(levelimage.getHeight() * (float)4.2666666666666);
        level_image.setImageBitmap(levelimage);
        
        level_image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent Event) {
                    float x = Event.getX();
                    float y = Event.getY();
                    float centerX = v.getWidth() / 2;
                    float centerY = v.getHeight() / 2;
                    float relativeX = x - centerX;
                    float relativeY = y - centerY;
                    int[] location = new int[2];
                    level_image.getLocationOnScreen(location);
                    int imageViewX = location[0];
                    int imageViewY = location[1];
                    int imageViewWidth = level_image.getWidth();
                    int imageViewHeight = level_image.getHeight();

                    // 获取触摸事件的位置信息
                    float touchX = Event.getRawX();
                    float touchY = Event.getRawY();

                    // 判断触摸事件是否在ImageView内
                    boolean isInside = touchX >= imageViewX && touchX <= imageViewX + imageViewWidth &&
                        touchY >= imageViewY && touchY <= imageViewY + imageViewHeight;
                    
                    if (isInside) {
                        // 处理滑动事件
                        switch (Event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                getClickLocation(relativeX, relativeY);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                getClickLocation(relativeX, relativeY);
                                break;
                        }
                        return true;
                    } else {
                        return false;
                    }


                }
            });
    }
    public void getClickLocation(double x, double y) {
        double NumX = 5.5555555555555;
        double NumY = -5.5555541666666;
        String resX = String.format("%.6f", x / NumX);
        String resY = String.format("%.6f", y / NumY);
        TextView ClickLocation = findViewById(R.id.click);
        ClickLocation.setText("点击位置:X:" + resX + ",Y:" + resY);
    }
    public void writeXML() {

    }
    public void showObject() {

    }
    public void showObjectProperty(Object obj) {
        //RelativeLayout ObjectPropertyLayout = findViewById(R.id.RL_CanEdit);
        //RelativeLayout b = new RelativeLayout(this);

    }
    public void writePNG() {

    }
}
