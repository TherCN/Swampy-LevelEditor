package thercn.swampy.leveleditor;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import thercn.swampy.leveleditor.R;
import thercn.swampy.leveleditor.TitanicTools.Titanic;
import thercn.swampy.leveleditor.TitanicTools.TitanicTextView;

public class MainActivity extends AppCompatActivity {

  public Activity mActivity = MainActivity.this;
  public static String APPDIR =
      Environment.getExternalStorageDirectory().toString() + "/SLE";
  Stopwatch stopwatch = new Stopwatch();
  String LevelsDir = APPDIR + "/Levels";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    stopwatch.start();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // You can modify this if judgment to view the functionality of Chinese
    // updates
    if (AppUtils.getLanguage() != "zh") {
      setContentView(R.layout.main_act_english);
    }
    // End
    if (!Permission.checkPermission(this)) {
      print("请同意权限申请，否则将无法在内部存储创建关卡");
      Thread.currentThread().suspend();
    }
    // 申请权限
    InitAppDir(); // 检测应用文件夹
    AppLog.WriteLog("初始化应用");
    try {
      Runtime.getRuntime().exec("logcat >" + AppLog.Logcat_Log);
    } catch (IOException e) {
      AppLog.WriteLog(e.toString());
    }
    getExistLevel();
    InitLayout();
    stopwatch.stop();
    long elapsedTime = stopwatch.getElapsedTime();

    AppLog.WriteLog("应用已初始化，耗时" + elapsedTime / 1000000 + "毫秒");

    // throw new NullPointerException("你抛出了空指针异常");
  }

  public void InitLayout() {
    popupWindow();
    final TitanicTextView gen_info = findViewById(R.id.gen_info);
    gen_info.setTextColor(Color.parseColor("#FFFFFF"));
    gen_info.setGravity(Gravity.CENTER);
    new Titanic().start(gen_info);

    final EditText level_png_path = findViewById(R.id.level_png_path);
    final EditText output_img_path = findViewById(R.id.output_png_path);
    Button goto_imageeditor = findViewById(R.id.goto_image_editor);
    Button generic_png = findViewById(R.id.generic_btn);

    goto_imageeditor.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(MainActivity.this, EditImage.class));
      }
    });
    generic_png.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String LPP = level_png_path.getText().toString();
        File file = new File(LPP);
        String OIP = output_img_path.getText().toString();
        if (LPP.isEmpty()) {
          print("关卡PNG路径为空！(The level image path is empty!)");
          return;
        }
        if (OIP.isEmpty()) {
          print("输出图片路径为空！(The output image path is empty!)");
          return;
        }
        if (!file.exists()) {
          print("关卡PNG文件不存在！(The image does not exist!)");
          return;
        }

        String input = LPP;
        stopwatch.start();
        if (!AppUtils.isSameSize(input)) {
          AppUtils.resizePNG(LPP, OIP + "_resized.png");
          input = OIP + "_resized.png";
        }
        AppUtils.RenderPNG(input, 0, OIP + ".tmp0.png");
        AppUtils.RenderPNG(OIP + ".tmp0.png", 1, OIP + ".tmp1.png");
        AppUtils.RenderPNG(OIP + ".tmp1.png", 2, OIP + ".tmp2.png");
        AppUtils.RenderPNG(OIP + ".tmp2.png", 3, OIP);
        stopwatch.stop();
        long elapsedTime = stopwatch.getElapsedTime();
        if (AppUtils.getLanguage() != "zh") {
          gen_info.setText("Done！The generated image is " + OIP + "，Taking " +
                           elapsedTime / 1000000 + "ms");
        }
        gen_info.setText("完成！生成的图片在" + OIP + "，耗时" +
                         elapsedTime / 1000000 + "毫秒");
        try {
          Runtime.getRuntime().exec("rm " + OIP + ".tmp*");
        } catch (IOException e) {
        }
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    getExistLevel();
  }

  public void InitAppDir() {
    File Appdir = new File(APPDIR);
    File Levelsdir = new File(LevelsDir);
    if (!Appdir.exists() || !Levelsdir.exists()) {
      Levelsdir.mkdirs();
      AppLog.InitLogFile();
      AppUtils.ExportAssets(this, APPDIR + "/image/", "dirt.png");
      AppUtils.ExportAssets(this, APPDIR + "/image/", "rock.png");
      AppUtils.ExportAssets(this, APPDIR + "/image/", "rock_hilight.png");
      AppUtils.ExportAssets(this, APPDIR + "/image/", "rock_shadow.png");
    }
    AppLog.InitLogFile();
  }

  public void getExistLevel() {
    File[] files = new File(LevelsDir).listFiles();
    if (files != null) {
      String[] fileNames = new String[files.length];
      for (int i = 0; i < files.length; i++) {
        fileNames[i] = files[i].getName();
      }
      ListView listView = findViewById(R.id.level_list);
      final ArrayAdapter<String> adapter = new ArrayAdapter<>(
          this, android.R.layout.simple_list_item_1, fileNames);
      listView.setAdapter(adapter);
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                                int position, long id) {
          String clickedText = adapter.getItem(position);
          print("你点击了" + clickedText);
          Intent intent = new Intent(MainActivity.this, EditLevel.class);
          intent.putExtra("LevelName", clickedText);
          startActivity(intent);
        }
      });
    }
  }
  public void print(String text) {
    Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
  }
  public void popupWindow() {

    final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    final Button btn1 = findViewById(R.id.new_level);
    btn1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (v.getId() == R.id.new_level) {
          View view =
              getLayoutInflater().inflate(R.layout.new_level_dialog, null);

          dialog.setView(view);
          dialog.setTitle("请输入关卡信息");
          final AlertDialog alertDialog = dialog.create();
          alertDialog.show();
          final EditText editlevelname =
              view.findViewById(R.id.edit_level_name);
          final EditText custom_image = view.findViewById(R.id.image_path);
          final CheckBox checkBox = view.findViewById(R.id.custom_image_btn);

          checkBox.setOnCheckedChangeListener(
              new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                  // 当CheckBox状态改变时执行的操作
                  if (isChecked) {
                    custom_image.setEnabled(true);
                  } else {
                    custom_image.setEnabled(false);
                  }
                }
              });
          Button newlevelconfirmbtn = view.findViewById(R.id.new_level_confirm);

          newlevelconfirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String levelname = editlevelname.getText().toString();

              if (!levelname.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, EditLevel.class);

                File LevelDir = new File(LevelsDir + "/" + levelname);

                if (LevelDir.exists()) {
                  print("关卡文件名重复！");
                  return;
                }
                String Custom_Level_PNG_Path =
                    custom_image.getText().toString();
                if (Custom_Level_PNG_Path.isEmpty() && checkBox.isChecked()) {
                  print("自定义关卡图片路径为空！");
                  return;
                }
                if (!Custom_Level_PNG_Path.isEmpty() && checkBox.isChecked()) {
                  intent.putExtra("LevelPNGPath", Custom_Level_PNG_Path);
                  intent.putExtra("LevelName", levelname);
                  NewLevel.createLevelFolder(levelname);
                  NewLevel.createLevelXMLFile(levelname);
                  alertDialog.dismiss();
                  startActivity(intent);
                  return;
                }
                NewLevel.createLevelFolder(levelname);
                NewLevel.createLevelPNGFile(levelname);
                NewLevel.createLevelXMLFile(levelname);
                alertDialog.dismiss();
                intent.putExtra("LevelName", levelname);
                startActivity(intent);

              } else {

                print("关卡文件名为空！");
              }
            }
          });
        }
      }
    });
  }
}
