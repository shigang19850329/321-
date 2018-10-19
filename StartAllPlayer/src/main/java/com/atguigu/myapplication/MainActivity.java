package com.atguigu.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
     public void startAllPlayer(View view) {
         Intent intent = new Intent();
         //这个地址一会传一个网络地址
         //intent.setDataAndType(Uri.parse("http://192.168.1.12:8080/WebServer/3.mp4"),"video/*");
         //intent.setDataAndType(Uri.parse("http://cctv13.vtime.cntv.wscdns.com/live/no/23_/seg0/index.m3u8?uid=default&AUTH=6+sb7H/DDgZ9MYff0mJ1rpMUyksw8zC6nQhOykNIpXaTZdEDg6huYnsWRW7KsatosnIXEVhU2Yr6gZJ5V8xEUw=="),"video/*");
         intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2016/07/19/mp4/160719095812990469.mp4"),"video/*");
         startActivity(intent);
    }
}
