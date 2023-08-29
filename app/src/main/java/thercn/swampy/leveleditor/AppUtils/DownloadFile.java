package thercn.swampy.leveleditor.AppUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
public class DownloadFile extends AsyncTask<String, Integer, Void> {
    private Context context;
    private ProgressDialog progressDialog;
		private File outputFile;
    public DownloadFile(Context context, String outputPath) {
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
				outputFile = new File(outputPath);
    }
    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("下载中...");
        progressDialog.show();
    }
    @Override
    protected Void doInBackground(String... sUrl) {
        String url = sUrl[0];
        try {
            URL u = new URL(url);
            InputStream is = u.openStream();
            byte[] buffer = new byte[1024];
            int len;
            FileOutputStream fos = new FileOutputStream(outputFile);
            int total = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 更新进度条
                publishProgress(total);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            Log.e("DownloadFileAsyncTask", "Error downloading file", e);
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setProgress(progress[0]);
    }
    @Override
    protected void onPostExecute(Void result) {
        progressDialog.dismiss(); // 关闭对话框
        // 可以在这里添加下载完成后的操作，比如打开文件等。
    }
}

