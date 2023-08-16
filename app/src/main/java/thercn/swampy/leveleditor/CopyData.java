package thercn.swampy.leveleditor;

import android.os.Environment;
import java.io.File;
import java.io.IOException;

public class CopyData {
    static String 目标Dir = Environment.getExternalStorageDirectory().toString() + "/SLE/Data";
    static void CopyData (){
        File 目标dir = new File(目标Dir);
        if (!目标dir.exists()) {
            目标dir.mkdirs();
        }
        try {
            Runtime.getRuntime().exec("cp -r /data/user/0/com.disney.WMW/ " + 目标Dir);
            AppLog.WriteLog("已执行:" + "cp -r /data/user/0/com.disney.WMW/ " + 目标Dir);
        } catch (IOException e) {
            AppLog.WriteLog("发生错误:" + e.toString());
        }
    }
    
}
