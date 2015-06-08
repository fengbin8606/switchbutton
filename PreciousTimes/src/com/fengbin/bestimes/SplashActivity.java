
package com.fengbin.bestimes;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

    private final int SPLASH_DELAY = 2000;
    private Handler mhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // changeActivity();
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // Intent intent = new Intent(SplashActivity.this, LoginPage.class);
            // startActivity(intent);
            finish();

        }
    };

    private void changeActivity() {
        mhandler.postDelayed(runnable, SPLASH_DELAY);
    }
}
