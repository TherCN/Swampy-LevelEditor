package thercn.swampy.leveleditor;

import android.icu.text.SimpleDateFormat;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class AppLog {

	File Logfile;
	
    public AppLog(String logfile) {
		Logfile = new File(logfile);
	}

    public void InitLogFile() {

        if (!Logfile.exists()) {
			try {
				Logfile.createNewFile();
			} catch (IOException e) {
				Log.e("SLE", e.getMessage());
			}
        }
		
    }
	public <T> void WriteErrorLog(T string) {
        WriteLog("'[ERROR]'", string);
    }
    public <T> void WriteNormalLog(T string) {
        WriteLog("'[INFO]'", string);
    }
	public <T> void WriteLog(String suffix,T string) {
		String str = String.valueOf(string);
        if (string instanceof String != true) {
            str = String.valueOf(string);
        }
        try {
            FileWriter WriteLogText = new FileWriter(Logfile, true);
            SimpleDateFormat formatter= new SimpleDateFormat("'['yyyy-MM-dd HH:mm:ss']'");
            Date date = new Date(System.currentTimeMillis());
            WriteLogText.write(suffix + formatter.format(date) + str + "\n");
            WriteLogText.flush();
            WriteLogText.close();
        } catch (IOException e) {}
	}

}
