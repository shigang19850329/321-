package com.atguigu.notificationtext;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "聊天信息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId,channelName,importance);

            channelId = "subscribe";
            channelName = "订阅信息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId,channelName,importance);

        }
    }
       @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId,String channelName,int importance){
           NotificationChannel channel = new NotificationChannel(channelId,channelName,importance);
           NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
           notificationManager.createNotificationChannel(channel);
      }
   public void sendCheckMsg(View v){
       NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
       Notification notification = new NotificationCompat.Builder(this,"chat")
               .setContentTitle("收到一条聊天消息")
               .setContentText("今天中午吃什么")
               .setWhen(System.currentTimeMillis())
               .setSmallIcon(R.drawable.notification_music_playing)
               .setAutoCancel(true)
               .build();
       manager.notify(1,notification);
   }
   public void sendSubscribeMsg(View view) {
      NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       Notification notification = new NotificationCompat.Builder(this,"subscribe")
               .setContentTitle("收到一条订阅信息")
               .setContentText("地铁沿线30万商铺抢购中")
               .setWhen(System.currentTimeMillis())
               .setSmallIcon(R.drawable.notification_music_playing)
               .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.base_bg))
               .setAutoCancel(true)
               .build();
       manager.notify(2,notification);
   }

}

