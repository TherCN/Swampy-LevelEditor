package thercn.swampy.leveleditor.AppUtils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AppTools {

		public static void unZip(Context context, String assetName, String outputDirectory, boolean isReWrite) throws IOException {
        File file = new File(outputDirectory);
        if (!file.exists()) {
            file.mkdirs();
        }
        InputStream inputStream = context.getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        byte[] buffer = new byte[1024 * 1024];
        int count = 0;
        while (zipEntry != null) {
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                if (isReWrite || !file.exists()) {
                    file.mkdir();
                }
            } else {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                if (isReWrite || !file.exists()) {
					file.getParentFile().mkdirs();
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }
		public static void ExportAssets(Activity activity, String outPath,
																		String fileName) {
				File outdir = new File(outPath);
				if (!outdir.exists()) {
						outdir.mkdirs();
				}
				try {
						InputStream inputStream = activity.getAssets().open(fileName);
						File outFile = new File(outdir, fileName);
						if (outFile.exists()) {
								return;
						}
						FileOutputStream fileOutputStream = new FileOutputStream(outFile);
						byte[] buffer = new byte[1024];
						int byteRead;
						while (-1 != (byteRead = inputStream.read(buffer))) {
								fileOutputStream.write(buffer, 0, byteRead);
						}
						inputStream.close();
						fileOutputStream.flush();
						fileOutputStream.close();
				} catch (IOException e) {
						AppLog.WriteLog(e.getMessage());
				}
		}



		public static void printText(Context context, CharSequence str) {
				Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
		}

		public static String getLanguage() {
				String language = System.getProperty("user.language").toString();
				// Toast.makeText(activity, language, Toast.LENGTH_SHORT).show();
				return language;
		}
		
}
