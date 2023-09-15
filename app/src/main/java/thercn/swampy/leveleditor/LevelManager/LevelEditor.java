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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import thercn.swampy.leveleditor.AppUtils.AppLog;
import thercn.swampy.leveleditor.AppUtils.AppTools;
import thercn.swampy.leveleditor.CustomWidget.MyAdapter;
import thercn.swampy.leveleditor.CustomWidget.ObjectView;
import thercn.swampy.leveleditor.ImageModify.SpriteCroper;
import thercn.swampy.leveleditor.LevelManager.LevelEditor;
import thercn.swampy.leveleditor.MainActivity;
import thercn.swampy.leveleditor.R;
import thercn.swampy.leveleditor.ThridPartsWidget.SpinnerEditText;


public class LevelEditor extends AppCompatActivity {

	double posX;
	double posY;
    boolean isScrolling;
    long touchStartTime;
	String OpenLevel;
	LevelXMLParser currentLevel;
	ObjectView level_image;
	File levelObjectImagePath;
	String levelPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.level_editor);
        OpenLevel = getIntent().getStringExtra("LevelName");
        Button reload = findViewById(R.id.reload);
		levelPath =
            NewLevel.LevelsDir + "/" + OpenLevel + "/" + OpenLevel;

		String Custom_PNG_Path = getIntent().getStringExtra("LevelPNGPath");
        String levelimage_path = levelPath + ".png";
        if (Custom_PNG_Path != "") {
            try {
                Runtime.getRuntime().exec("cp " + Custom_PNG_Path + " " +
                                          levelimage_path);
            } catch (IOException e) {
            }
        }

        Bitmap levelimage = BitmapFactory.decodeFile(levelimage_path);
        level_image = findViewById(R.id.level_image);
		level_image.setImageBitmap(levelimage);

        reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImage(OpenLevel);
                    showObjects(OpenLevel);
					showObjectImage(levelPath + ".xml");
                }
            });
    }

	@Override
	protected void onResume() {
		super.onResume();
		showImage(OpenLevel);
        showObjects(OpenLevel);
		AppLog.WriteLog(level_image.getHeight() + " " + level_image.getWidth());
		
	}


    public void showImage(String currentLevel) {


		final TextView ClickLocation = findViewById(R.id.click);
        ClickLocation.setTextSize(10);
		showObjectImage(levelPath + ".xml");
		level_image.setOnTouchListener(new View.OnTouchListener() {
                double[] lastPos = {0, 0};

                @Override
                public boolean onTouch(View v, MotionEvent event) {
					level_image.onTouchEvent(event);
					posX = event.getX();
					posY = event.getY();
                    float x = event.getX();
                    float y = event.getY();
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

                    double[] pos = px2gl(relativeX, relativeY);
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
		AppLog.WriteLog(level_image.getHeight() + " " + level_image.getWidth());
		
		showObjectImage(levelPath + ".xml");
    }
    //这里我自己定义了一个单位"gl"，全名Game Location，也就是游戏内的坐标
    public static double[] px2gl(double x, double y) {
        double NumX = 5.5555555555555;
        double NumY = -5.5555541666666;
        double realX = x / NumX;
        double realY = y / NumY;
        return new double[] {realX, realY};
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
		level_image.setOnObjectViewClickedListener(new ObjectView.OnObjectViewClickedListener() {
				@Override
				public void onObjectViewClicked(String name) {
					// 当点击小图片时执行的代码。
					String[][] currentObjectInfo = currentLevel.getObjectProperties(name);
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
					/*
					 for (int i = 0; i < level.getObjects().length; i++) {
					 if (ObjectName.getText().toString() == level.getObjects()[i]) {
					 AppTools.printText(LevelEditor.this, "已存在相同名字的物体！");
					 return;
					 } 

					 }
					 */
					if (level.addObject(ObjectName.getText().toString(), ObjectFile.getText().toString())) {
						level.saveModifyToFile();
						AppTools.printText(LevelEditor.this, "添加成功" + ObjectName.getText().toString());
						Dialog.dismiss();
						showObjects(OpenLevel);
						showObjectImage(levelPath + ".xml");
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
			}
			return fileNames;
		}
		return null;
	}

	private void showObjectImage(final String level) {
		Thread thread = new Thread(new Runnable(){
				public void run() {
					currentLevel = new LevelXMLParser(level);
					String[] names = currentLevel.getObjects();
					String[] locationStr = new String[names.length];
					float[][] reallocation = new float[names.length][2],fixedLocation = new float[names.length][2];
					String[] path = new String[names.length];
					double[] angle = new double[names.length];
					String filename = null,angleValue = null;
					levelObjectImagePath = new File("/sdcard/SLE/ImageListCache/" + OpenLevel);
					if (!levelObjectImagePath.exists()) {
						levelObjectImagePath.mkdirs();
					}
					for (int i = 0; i < names.length; i++) {
						for (int a = 0; a < currentLevel.getObjectProperties(i)[0].length; a++) {
							if (currentLevel.getObjectProperties(i)[0][a].equals("Filename")) {
								filename = currentLevel.getObjectProperties(i)[1][a];
							}
						}

						// 将过滤后的字符串存回数组

						path[i] = CropObjectImage(filename);

						locationStr[i] = currentLevel.getObjectProperties(i)[1][0];

						for (int a = 0; a < currentLevel.getObjectProperties(i)[0].length; a++) {
							if (currentLevel.getObjectProperties(i)[0][a].equals("Angle")) {
								angleValue = currentLevel.getObjectProperties(i)[1][a];
							}
						}
						angle[i] = Double.parseDouble(angleValue);
						reallocation[i][0] = Float.parseFloat(locationStr[i].split(" ")[0]);
						reallocation[i][1] = Float.parseFloat(locationStr[i].split(" ")[1]);


					}
					for (int i = 0; i < names.length; i++) {
						Bitmap a = BitmapFactory.decodeFile(path[i]);
						Bitmap rotatedBitmap = level_image.rotateBitmap(a, (float)angle[i]);

						float[] input = reallocation[i];
						//AppLog.WriteLog("视图大小:" + level_image.getHeight() + " " + level_image.getWidth());

						float x = level_image.getWidth() / 2 - rotatedBitmap.getWidth() / 2;
						float y = level_image.getHeight() / 2 - rotatedBitmap.getHeight() / 2;
						double scaleX = -5.5555555555555;
						double scaleY = 5.5555541666666;
						fixedLocation[i][0] = (float)(x - scaleX * reallocation[i][0]);
						fixedLocation[i][1] = (float)(y - scaleY * reallocation[i][1]);
					}


					level_image.setData(path, names, fixedLocation, angle);
				}
			});
		thread.start();

	}

	public float[] fixLocation(float[] origLocation, Bitmap image) {

		float[] returnLocation = new float[2];

		float x = level_image.getWidth() / 2 - image.getWidth() / 2;
		float y = level_image.getHeight() / 2 - image.getHeight() / 2;
		double scaleX = -5.5555555555555;
		double scaleY = 5.5555541666666;
		returnLocation[0] = (float)(x - scaleX * origLocation[0]);
		returnLocation[1] = (float)(y - scaleY * origLocation[1]);

		return returnLocation;





	}











	public String CropObjectImage(String object) {
		SpriteCroper objectImage = new SpriteCroper(new File("/sdcard/SLE" + object));
		File file = new File(levelObjectImagePath + "/" + object.split("/")[2] + ".png");
		try {
			if (!file.exists()) {
				String[] sprites = objectImage.getSpritesForObject();
				for (int i = 0; i < sprites.length; i++) {
					String[] imagelist = objectImage.getImageListForSprite(sprites[i]);
					String[] imageFiles = objectImage.getImageFileForSprite(sprites[i]);
					objectImage.cropImageList(imagelist[0]);
					Bitmap objectBitmap = objectImage.mergeBitmaps(imageFiles);
					OutputStream outputStream = new FileOutputStream(file);
					objectBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
				}
			}

			return file.toString();
		} catch (Exception e) {
			AppLog.WriteExceptionLog(e);
			return null;
		}
	}

    public void EditProperty() {
    }
    public void writePNG() {}

}
