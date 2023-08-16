package thercn.swampy.leveleditor;

import android.graphics.Bitmap;
import android.os.Environment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class NewLevel {
    public static String LevelsDir = Environment.getExternalStorageDirectory().toString() + "/SLE/Levels";
    public static void createLevelFolder (String str){
        File levelfolder = new File(LevelsDir + "/" + str);
        if (!levelfolder.exists()) {
            levelfolder.mkdir();
            createLevelFile(str);
        }
    }
    public static void createLevelFile (String str){
        File xmlFile = new File(LevelsDir + "/" + str + "/" + str + ".xml");
        File pngFile = new File(LevelsDir + "/" + str + "/" + str + ".png");
        Bitmap bitmap = Bitmap.createBitmap(90, 120, Bitmap.Config.RGBA_F16);
        OutputStream out = null;
        try {
            out = new FileOutputStream(pngFile);
        } catch (FileNotFoundException e) {
            AppLog.WriteLog(e.toString());
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
        
        try {
            xmlFile.createNewFile();
            
        } catch (IOException e) {
            AppLog.WriteLog(e.toString());
        }
        String xmlFilePath = LevelsDir + "/" + str + "/" + str + ".xml";
        FileWriter fwriter = null;
        try {
            fwriter = new FileWriter(xmlFilePath, true);
            //反斜杠用来转义
            fwriter.write("<?xml version=\"" + "1.0\" encoding=\"" + "utf-8\"/>");
        } catch (IOException e) {
            AppLog.WriteLog(e.toString());
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException e) {
                AppLog.WriteLog(e.toString());
            }
            
        }
		AppLog.WriteLog("已创建关卡:" + str);
    }
}

