package com.example.better.rocketman.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.better.rocketman.R;
import com.example.better.rocketman.service.RocketService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉页面标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= 23) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(Color.parseColor("#90c653"));
        }
        setContentView(R.layout.activity_main);
        Button btn_start = (Button) findViewById(R.id.btn_start);
        Button btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启火箭所在的服务
                startService(new Intent(getApplicationContext(), RocketService.class));
                finish();
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启关闭火箭所在的服务
                stopService(new Intent(getApplicationContext(), RocketService.class));
            }
        });
    }
}
