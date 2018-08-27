package com.atguigu.mobileplayer.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.atguigu.mobileplayer.R;

/**
 * 系统播放器
 */
public class SystemVideoPlayer extends Activity{
    private VideoView videoView;
    private Uri uri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_system_video_player);
        videoView = (VideoView) findViewById(R.id.video_view);
        //得到播放地址
        uri = getIntent().getData();
        if (uri!=null){
            videoView.setVideoURI(uri);
        }

        //设置控制面板
        //准备好的监听,实现这个接口
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放出错的监听
        videoView.setOnErrorListener(new MyOnErrorListener());
        //播放完成的监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());

        //显示控制面板
        videoView.setMediaController(new MediaController(this));
    }
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{
        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.start();//开始播放了。
        }
    }
    class MyOnErrorListener implements MediaPlayer.OnErrorListener{
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this,"播放出错了哦", Toast.LENGTH_SHORT).show();
            //如果返回false，会默认弹出一个对话框，
            return false;
        }
    }
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(SystemVideoPlayer.this,"播放完成了="+uri, Toast.LENGTH_SHORT).show();
        }
    }
}
