package com.atguigu.mobileplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import com.atguigu.mobileplayer.R;

public class SplashActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    //动态的会随着类的名字改变而改变
private static String TAG = SplashActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后才执行到这里
                // 执行在主线程里
                startMainActivity();
                Log.e(TAG,"当前线程的名称=="+Thread.currentThread().getName());
            }
        }, 2000);
    }

    /**
     * 跳转到主页面，并且将当前页面关闭掉
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //关闭当前页面
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"onTouchEvent==Action"+event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果这个参数是null，所有的消息都会被移除，
        //现在点击进入MainActivity再退出，就不会再打开MainActivity了
        handler.removeCallbacksAndMessages(null);
    }
}
