package thercn.swampy.leveleditor.LevelManager;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import thercn.swampy.leveleditor.AppUtils.AppTools;
import thercn.swampy.leveleditor.CustomWidget.MyAdapter;
import thercn.swampy.leveleditor.CustomWidget.ObjectView;
import thercn.swampy.leveleditor.LevelManager.LevelEditor;
import thercn.swampy.leveleditor.MainActivity;
import thercn.swampy.leveleditor.R;
import thercn.swampy.leveleditor.ThridPartsWidget.SpinnerEditText;
import android.widget.EditText;
//import thercn.swampy.leveleditor.ThridPartsWidget.SpinnerEditText.SpinnerEditText;


public class LevelEditor extends AppCompatActivity {

    boolean isScrolling;
    long touchStartTime;
		String OpenLevel;
		LevelXMLParser currentLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_editor);
        OpenLevel = getIntent().getStringExtra("LevelName");
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

		@Override
		protected void onResume() {
				super.onResume();
				Scanner
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
        final ObjectView level_image = findViewById(R.id.level_image);
				String[] path = {"/sdcard/a.png","/sdcard/b.png"},names = {"a","b"};
				double location[][] = {{20,10},{50,70}};

				level_image.setData(path, names, location); // 设置小图片的路径、名字和位置。

				level_image.setOnObjectViewClickedListener(new ObjectView.OnObjectViewClickedListener() {
								@Override
								public void onObjectViewClicked(String name) {
										// 当点击小图片时执行的代码。
										AppTools.printText(LevelEditor.this, "You clicked the object: " + name);
								}
						});
				level_image.setImageBitmap(levelimage);

        ClickLocation.setTextSize(10);
	      level_image.setOnTouchListener(new View.OnTouchListener() {
                double[] lastPos = {0, 0};

                @Override
                public boolean onTouch(View v, MotionEvent event) {
										level_image.onTouchEvent(event);
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
    public void showObjects(String currentLevelFile) {
        String levelxmlPath =
            NewLevel.LevelsDir + "/" + currentLevelFile + "/" + currentLevelFile + ".xml";
				currentLevel = new LevelXMLParser(levelxmlPath);
				String[] Objects = currentLevel.getObjects();
				final ArrayAdapter<String> Adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Objects);
				ListView ObjectList = findViewById(R.id.object_list);
				final ListView PropertiesList = findViewById(R.id.property_list);
				ObjectList.setAdapter(Adapter);

				ObjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long itemId) {
										String[][] currentObjectInfo = currentLevel.getObjectProperties((int)itemId);
										MyAdapter ObjectProperties = new MyAdapter(LevelEditor.this, currentObjectInfo);
										PropertiesList.setAdapter(ObjectProperties);
								}
						});
				final Button add_object = findViewById(R.id.add_object);
				add_object.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View view) {
										addObjectDialog(currentLevel);
								}
						});



    }

		public void addObjectDialog(final LevelXMLParser level) {
				AlertDialog.Builder newObjectDialog = new AlertDialog.Builder(this);
				View view =
						getLayoutInflater().inflate(R.layout.add_object, null);

				newObjectDialog.setView(view);
				final SpinnerEditText ObjectFile = view.findViewById(R.id.objectFile);
				final EditText ObjectName = view.findViewById(R.id.objectName);
				final Button addObject = view.findViewById(R.id.addObject);
				final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getObjectFiles());
				ObjectFile.setAdapter(adapter);
				newObjectDialog.setTitle("请输入物体信息");
				final AlertDialog Dialog = newObjectDialog.create();
				Dialog.show();
				addObject.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View view) {
										if (ObjectName.getText().toString().isEmpty()) {
												AppTools.printText(LevelEditor.this, "物体名字为空！");
												return;
										}
										if (ObjectFile.getText().toString().isEmpty()) {
												AppTools.printText(LevelEditor.this, "物体文件为空！");
												return;
										}
										for (int i = 0; i < level.getObjects().length; i++) {
												if (ObjectName.getText().toString() == level.getObjects()[i]) {
														AppTools.printText(LevelEditor.this, "已存在相同名字的物体！");
														return;
												} 
												
										}
										if (level.addObject(ObjectName.getText().toString(), ObjectFile.getText().toString()))
										{
										level.saveModifyToFile();
										AppTools.printText(LevelEditor.this,"添加成功" + ObjectName.getText().toString());
										Dialog.dismiss();
										showObjects(OpenLevel);
										}
								}


						});


		}
		
		public String[] getObjectFiles() {
				File[] files = new File(MainActivity.APPDIR + "/Objects/").listFiles();
				if (files != null) {
						String[] fileNames = new String[files.length];
						for (int i = 0; i < files.length; i++) {
								fileNames[i] = "/Objects/" + files[i].getName();
								//AppLog.WriteLog(fileNames[i]);
						}
						return fileNames;
				}
				return null;
		}
    public void EditProperty() {
    }
    public void writePNG() {}

}
