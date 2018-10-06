package com.example.better.rocketman.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.better.rocketman.R;
import com.example.better.rocketman.activity.BackgroundActivity;

public class RocketService extends Service {
    private WindowManager mWM;
    private int mScreenHeight;
    private int mScreenWidth;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mRocketView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            params.y = (int) msg.obj;
            //告知窗体更新view的所在位置
            mWM.updateViewLayout(mRocketView, params);
        }
    };
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        //获取窗体对象
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mScreenWidth = mWM.getDefaultDisplay().getWidth();
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            } else {
                //Android6.0以上
                //开启火箭
                showRocket();
            }
        } else {
            //Android6.0以下，不用动态声明权限
            //开启火箭
            showRocket();
        }

        super.onCreate();
    }

    private void showRocket() {
        //自定义吐司
        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //在响铃的时候显示吐司,和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;// 将类型修改成打电话的级别
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                // 删掉了原有吐司中定义的不能被触摸flag  WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        //指定吐司的所在位置
        params.gravity = Gravity.TOP + Gravity.LEFT;
        //吐司显示效果(吐司布局文件),xml--->(吐司),将吐司挂载到windowManager窗体上
        mRocketView = View.inflate(this, R.layout.rocket_view, null);
        ImageView iv_rocket = mRocketView.findViewById(R.id.iv_rocket);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_rocket.getBackground();
        animationDrawable.start();
        mWM.addView(mRocketView, params);
        mRocketView.setOnTouchListener(new View.OnTouchListener() {
            private int startY;
            private int startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        int disX = moveX - startX;
                        int disY = moveY - startY;
                        params.x = params.x + disX;
                        params.y = params.y + disY;
                        //容错处理
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > mScreenWidth - mRocketView.getWidth()) {
                            params.x = mScreenWidth - mRocketView.getWidth();
                        }
                        if (params.y > mScreenHeight - mRocketView.getHeight() - 22) {
                            params.y = mScreenHeight - mRocketView.getHeight() - 22;
                        }
                        //告知窗体吐司需要按照手势的移动,去做位置的更新
                        mWM.updateViewLayout(mRocketView, params);
                        //重置一次起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 小火箭拖拽到手机屏幕下方的中间时，触发小火箭发射
                        if (mParams.x > mScreenWidth / 2 - 150 && mParams.x < mScreenWidth / 2 - mRocketView.getWidth() / 2 + 50
                                && mParams.y > mScreenHeight - mRocketView.getHeight() - 50) {
                            // 小火箭发射升空
                            sendRocket();
                            //开启产生尾气的activity
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), BackgroundActivity.class);
                                    //开启火箭后,关闭了唯一的activity对应的任务栈,所以在此次需要告知新开启的activity的任务栈
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }, 120);
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void sendRocket() {
        //在向上的移动过程中,一直去减少y轴的大小,直到减少为0为止
        //在主线程中不能去睡眠,可能会导致主线程阻塞
        new Thread() {
            @Override
            public void run() {
                int disY = mScreenHeight / 5;
                for (int i = 0; i < 6; i++) {
                    int height = mScreenHeight - i * disY;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.obj = height;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if (mWM != null && mRocketView != null) {
            mWM.removeView(mRocketView);
        }
        super.onDestroy();
    }
}
