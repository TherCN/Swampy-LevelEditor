package thercn.swampy.leveleditor.LevelManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import thercn.swampy.leveleditor.CustomContent.FixedListView;
import thercn.swampy.leveleditor.CustomContent.MyAdapter;
import thercn.swampy.leveleditor.R;

public class EditLevel extends AppCompatActivity {

    boolean isScrolling;
    long touchStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_editor);
        final String OpenLevel = getIntent().getStringExtra("LevelName");

        Button reload = findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImage(OpenLevel);
                    showObjects(OpenLevel);
                }
            });
        showImage(OpenLevel);
        showObjects(OpenLevel);
    }

    public void showImage(String currentLevel) {

        String Custom_PNG_Path = getIntent().getStringExtra("LevelPNGPath");
        String levelimage_path =
            NewLevel.LevelsDir + "/" + currentLevel + "/" + currentLevel + ".png";
        if (Custom_PNG_Path != "") {
            try {
                Runtime.getRuntime().exec("cp " + Custom_PNG_Path + " " +
                                          levelimage_path);
            } catch (IOException e) {
            }
        }

        Bitmap levelimage = BitmapFactory.decodeFile(levelimage_path);
        final TextView point = findViewById(R.id.dot);
        final TextView ClickLocation = findViewById(R.id.click);
        final ImageView level_image = findViewById(R.id.level_image);
        level_image.setImageBitmap(levelimage);

        ClickLocation.setTextSize(10);
        level_image.setOnTouchListener(new View.OnTouchListener() {
                double[] lastPos = {0, 0};

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float x = event.getX();
                    float y = event.getY();
                    point.setText("");
                    point.setText(".");
                    point.setX(x);
                    point.setY(y);
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
                    float touchX = event.getRawX();
                    float touchY = event.getRawY();

                    // 判断触摸事件是否在ImageView内
                    boolean isInside =
                        touchX >= imageViewX && touchX <= imageViewX + imageViewWidth &&
                        touchY >= imageViewY && touchY <= imageViewY + imageViewHeight;

                    double[] pos = dp2gl(relativeX, relativeY);
                    double offsetX = pos[0] - lastPos[0];
                    double offsetY = pos[1] - lastPos[1];

                    if (isInside) {
                        // 处理滑动事件
                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:
                                touchStartTime = System.currentTimeMillis();
                                ClickLocation.setText("点击位置:X:" + pos[0] + ",Y:" + pos[1] +
                                                      ",偏移量:X:" + offsetX + ",Y:" + offsetY);

                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (System.currentTimeMillis() - touchStartTime > 500) {
                                    isScrolling = true;
                                    ClickLocation.setText("点击位置:X:" + pos[0] + ",Y:" + pos[1] +
                                                          ",偏移量:X:" + offsetX + ",Y:" + offsetY);
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                isScrolling = false;
                                lastPos[0] = pos[0];
                                lastPos[1] = pos[1];
                                break;
                        }

                        return true;
                    } else {
                        return false;
                    }
                }
            });
    }
    //这里我自己定义了一个单位"gl"，全名Game Location，也就是游戏内的坐标
    public double[] dp2gl(double x, double y) {
        double NumX = 5.5555555555555;
        double NumY = -5.5555541666666;
        double realX = x / NumX;
        double realY = y / NumY;
        return new double[] {realX, realY};
    }
    public double[] gl2dp(double x, double y) {
        double[] a = {0.001,0.001};
        return a;
    }
    public void addPoint(View v) {}
    public void writeXML() {}
    public void showObjects(String currentLevel) {
        String levelxmlPath =
            NewLevel.LevelsDir + "/" + currentLevel + "/" + currentLevel + ".xml";
				LevelXMLParser currentLevelxml = new LevelXMLParser(levelxmlPath);
				String[] Objects = currentLevelxml.getObjects();
				ArrayAdapter<String> Adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Objects);
				ListView ObjectList = findViewById(R.id.object_list);
				ObjectList.setAdapter(Adapter);

				ListView PropertiesList = findViewById(R.id.property_list);
    }


    public void EditProperty() {
        FixedListView PropertyList = findViewById(R.id.property_list);
        ArrayList<String> a = new ArrayList<>();
        MyAdapter Adapter = new MyAdapter(this, a);
        PropertyList.setAdapter(Adapter);

    }
    public void writePNG() {}
}
