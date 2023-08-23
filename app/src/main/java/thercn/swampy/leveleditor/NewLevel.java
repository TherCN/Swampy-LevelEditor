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
  public static String LevelsDir =
      Environment.getExternalStorageDirectory().toString() + "/SLE/Levels";
  public static void createLevelFolder(String str) {
    File levelfolder = new File(LevelsDir + "/" + str);
    if (!levelfolder.exists()) {
      levelfolder.mkdir();
    }
  }
  public static void createLevelPNGFile(String str) {
    File pngFile = new File(LevelsDir + "/" + str + "/" + str + ".png");
    Bitmap bitmap = Bitmap.createBitmap(90, 120, Bitmap.Config.RGBA_F16);
    OutputStream out = null;
    try {
      out = new FileOutputStream(pngFile);
    } catch (FileNotFoundException e) {
      AppLog.WriteLog(e.toString());
    }
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
  }
  public static void createLevelXMLFile(String str) {
    File xmlFile = new File(LevelsDir + "/" + str + "/" + str + ".xml");
    try {
      xmlFile.createNewFile();
      FileWriter fwriter = new FileWriter(xmlFile, true);
      fwriter.write("<?xml version=\""
                    + "1.0\" encoding=\""
                    + "utf-8\"/>");
      fwriter.flush();
      fwriter.close();
    } catch (IOException e) {
      AppLog.WriteLog(e.toString());
    }
  }
}
