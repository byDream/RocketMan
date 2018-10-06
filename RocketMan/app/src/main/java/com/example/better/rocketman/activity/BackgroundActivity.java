package com.example.better.rocketman.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.example.better.rocketman.R;

public class BackgroundActivity extends Activity {
    private static final String TAG = "tag";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    alphaAnimation = new AlphaAnimation(1, 0);
                    alphaAnimation.setDuration(600);
                    iv_top.startAnimation(alphaAnimation);
                    iv_bottom.startAnimation(alphaAnimation);
                    break;
                case 1:
                    iv_top.setBackground(null);
                    iv_bottom.setBackground(null);
                    finish();
                    break;
            }
        }
    };
    private AlphaAnimation alphaAnimation;
    private ImageView iv_top;
    private ImageView iv_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_background);
        iv_top = (ImageView) findViewById(R.id.iv_top);
        iv_bottom = (ImageView) findViewById(R.id.iv_bottom);
        alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(600);
        iv_top.startAnimation(alphaAnimation);
        iv_bottom.startAnimation(alphaAnimation);
        mHandler.sendEmptyMessageDelayed(0, 600);
        mHandler.sendEmptyMessageDelayed(1, 1200);
    }
}
