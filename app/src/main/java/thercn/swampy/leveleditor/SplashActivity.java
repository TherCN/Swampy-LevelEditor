package thercn.swampy.leveleditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import thercn.swampy.leveleditor.R;

public class SplashActivity extends Activity {

  public boolean SplashActivity = false;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!SplashActivity) {
      Intent intent = new Intent(SplashActivity.this, MainActivity.class);

      startActivity(intent);
      return;
    }
    setContentView(R.layout.splash);
    Window window = getWindow();
    window.addFlags(
        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(Color.WHITE);

    new Handler().postDelayed(new Runnable() {
      @Override

      public void run() {

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        startActivity(intent);

        finish();
      }
    }, 1000);
  }
}
