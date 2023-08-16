package thercn.swampy.leveleditor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import thercn.swampy.leveleditor.R;
import thercn.swampy.leveleditor.TitanicTools.Titanic;
import thercn.swampy.leveleditor.TitanicTools.TitanicTextView;

public class MainActivity extends AppCompatActivity {
    @Override
	private PopupWindow mPoup;
    public Context mContext = this;
    Stopwatch stopwatch = new Stopwatch();
    //long elapsedTime = stopwatch.getElapsedTime();
    static String APPDIR = Environment.getExternalStorageDirectory().toString() + "/SLE";
    String LevelsDir = APPDIR + "/Levels";
    
    protected void onCreate(Bundle savedInstanceState) {
        stopwatch.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Permission.checkPermission(this);  //申请权限
		checkAppDir();	//检测应用文件夹
		AppLog.WriteLog("初始化应用");
		try {
			Runtime.getRuntime().exec("logcat >" + AppLog.Logcat_Log);
		} catch (IOException e) {
			AppLog.WriteLog(e.toString());
		}
		getExistLevel();
        InitLayout();
		AppUtils.ExportAssets(this, APPDIR + "/image/", "dirt.png");
        AppUtils.ExportAssets(this, APPDIR + "/image/", "rock.png");
        AppUtils.ExportAssets(this, APPDIR + "/image/", "rock_hilight.png");
        AppUtils.ExportAssets(this, APPDIR + "/image/", "rock_shadow.png");
        stopwatch.stop();
        long elapsedTime = stopwatch.getElapsedTime();
        AppLog.WriteLog("应用已初始化，耗时" + elapsedTime / 1000000 + "毫秒");

    }


    public void InitLayout() {
		popupWindow();
        final TitanicTextView gen_info = findViewById(R.id.gen_info);
        gen_info.setTextColor(Color.parseColor("#FFFFFF"));
        gen_info.setGravity(Gravity.CENTER);
        new Titanic().start(gen_info);

		final EditText level_png_path = findViewById(R.id.level_png_path);
        final EditText output_img_path = findViewById(R.id.output_png_path);
        Button generic_png = findViewById(R.id.generic_btn);

        generic_png.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    String LPP = level_png_path.getText().toString();
                    File file = new File(LPP);
                    String OIP = output_img_path.getText().toString();
                    if (LPP.isEmpty()) {
                        AppUtils.printText(MainActivity.this, "关卡PNG路径为空！");
                        return;
                    }
                    if (OIP.isEmpty()) {
                        AppUtils.printText(MainActivity.this, "输出路径为空！");
                        return;
                    }
                    if (!file.exists()) {
                        AppUtils.printText(MainActivity.this, "关卡PNG文件不存在！");
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
                    AppLog.WriteLog("代码执行耗时：" + elapsedTime / 1000000 + "毫秒");
                    gen_info.setText("完成！生成的图片在" + OIP + "，耗时" + elapsedTime / 1000000 + "毫秒");
                    try {
                        Runtime.getRuntime().exec("rm " + OIP + ".tmp*");
                    } catch (IOException e) {}
                }
            });

    }


    public void checkAppDir() {
        File Appdir = new File(APPDIR);
		File Levelsdir = new File(LevelsDir);
        if (!Appdir.exists()) {
            Levelsdir.mkdirs();
        }
    }

    public void getExistLevel() {
        File[] files = new File(LevelsDir).listFiles();
        if (files != null) {
            String[] fileNames = new String[files.length];
            for (int i=0; i < files.length; i++) {
                fileNames[i] = files[i].getName();

            }
            ListView listView = findViewById(R.id.level_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        print("你点击了" + id);
                    }                                                                                           
                });
        }
    }
    public void print(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
    public void popupWindow() {
        final Button btn1 = findViewById(R.id.new_level);
        btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (v.getId() == R.id.new_level) {
                        View view = getLayoutInflater().inflate(R.layout.new_level_dialog, null);

                        mPoup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        //在弹窗外点击自动收回
                        mPoup.setFocusable(true);

                        mPoup.setElevation(100f);
                        //显示在指定位置
                        mPoup.showAtLocation(MainActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

                        final EditText editlevelname = view.findViewById(R.id.edit_level_name);

                        Button newlevelconfirmbtn = view.findViewById(R.id.new_level_confirm);

                        newlevelconfirmbtn.setOnClickListener(new View.OnClickListener() {                      
								@Override public void onClick(View view) {
                                    String levelname = editlevelname.getText().toString();

                                    if (!levelname.isEmpty()) {

                                        File LevelDir = new File(LevelsDir + "/" + levelname);

                                        if (LevelDir.exists()) {
                                            print("关卡文件名重复！");
                                            return;
                                        }

                                        NewLevel.createLevelFolder(levelname);
                                        mPoup.dismiss();
                                        startActivity(new Intent(MainActivity.this, EditLevel.class));

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