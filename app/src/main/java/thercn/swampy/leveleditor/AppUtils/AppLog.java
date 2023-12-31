package thercn.swampy.leveleditor.AppUtils;

import android.icu.text.SimpleDateFormat;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import thercn.swampy.leveleditor.MainActivity;

public class AppLog {
	static File Logfile = new File(MainActivity.APPDIR + "/SLELog.log");
	public static File Logcat_Log = new File(MainActivity.APPDIR + "/SLELog-Logcat.log");

	public static void InitLogFile() {

		if (!Logfile.exists()) {
			try {
				Logfile.createNewFile();
			} catch (IOException e) {
				Log.e("SLE", e.getMessage());
			}
		}
	}

	public static void WriteExceptionLog(Exception error) {
		WriteLog("ERROR: " + error.getMessage());
		for (int i = 0; i < error.getStackTrace().length; i++) {
			WriteLog("ERROR: " + error.getStackTrace()[i]);
		}
	}

	public static void WriteLog(Object string) {
		try {
			FileWriter WriteLogText = new FileWriter(Logfile, true);
			SimpleDateFormat formatter =
				new SimpleDateFormat("'['yyyy-MM-dd HH:mm:ss']'");
			Date date = new Date(System.currentTimeMillis());
			WriteLogText.write(formatter.format(date) + string + "\n");
			WriteLogText.flush();
			WriteLogText.close();
		} catch (IOException e) {
			Log.e("SLE", e.getMessage());
		}
	}
}
