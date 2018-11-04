package com.atguigu.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileplayer.IMusicPlayerService;
import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.domain.MediaItem;
import com.atguigu.mobileplayer.service.MusicPlayerService;
import com.atguigu.mobileplayer.utils.LyricUtils;
import com.atguigu.mobileplayer.utils.Utils;
import com.atguigu.mobileplayer.view.BaseVisualizerView;
import com.atguigu.mobileplayer.view.ShowLyricView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class AudioPlayerActivity extends Activity implements View.OnClickListener {
    //private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlayMode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyric;
    private ShowLyricView showLyricView;
    private BaseVisualizerView baseVisualizerView;

    private MyReceiver receiver;
    private Utils utils;
    //进度更新
    private static final int PROGRESS = 1;
    /**
     * true从状态栏进入的，不需要重新播放，状态还原到从前的状态
     * false从播放列表进入的。
     */
    private boolean notification;
    /**
     * 显示歌词
     */
    private static final int SHOW_LYRIC = 2;


    private int position;
    private IMusicPlayerService service;
    private ServiceConnection con = new ServiceConnection() {
        /**
         * 当连接成功的时候回调这个方法
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if (service!=null){
                try {
                    if (!notification){//从列表
                        service.openAudio(position);
                    }else{
                        //从状态栏，不处理也不行，
                        //System.out.println("onServiceConnected==Thread-name=="+Thread.currentThread().getName());
                        showViewData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当断开连接的时候回调这个方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
           try {
               if (service!=null){
                   service.stop();
                   service = null;
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        bindAndStartService();
    }

    private void initData() {
        utils = new Utils();
//        //注册广播
//        receiver = new MyReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
//        registerReceiver(receiver, intentFilter);

        //1.EventBus注册
        EventBus.getDefault().register(this);//this是当前类
    }
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showData(null);
        }
    }
    //第三步，订阅方法
   @Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 0)
    public void showData(MediaItem mediaItem) {
       //发消息开始歌词同步
       showLyric();
       showViewData();
       checkPlayMode();
       setupVisualizerFxAndUi();
   }
   private Visualizer mVisualizer;
    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到visualizerView上
     */
   private void setupVisualizerFxAndUi(){
       try {
           int audioSessionid = service.getAudioSessionId();
           System.out.println("audioSessionid=="+audioSessionid);
           mVisualizer = new Visualizer(audioSessionid);
           // 参数内必须是2的位数
           mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
           // 设置允许波形表示，并且捕获它
           baseVisualizerView.setVisualizer(mVisualizer);
           mVisualizer.setEnabled(true);
       } catch (RemoteException e) {
           e.printStackTrace();
       }

   }

    /**
     *显示歌词
     */
    private void showLyric() {
        //解析歌词
        LyricUtils lyricUtils = new LyricUtils();

        try {
            String path = service.getAudioPath();

            //传歌词文件
            //mnt/sdcard/audio/beijingbeijing.mp3
            //mnt/sdcard/auddio/beijingbeijing.lyr或者txt,因为下载的歌词和歌曲一般是指在同一个目录下面

            path = path.substring(0,path.lastIndexOf("."));
            File file = new File(path+".lrc");
            if (!file.exists()){//如果这种格式文件不存在，再创建一个txt文件
                file = new File(path + ".txt");
            }
           lyricUtils.readLyricFile(file);//解析歌词

            showLyricView.setLyrics(lyricUtils.getLyrics());

        } catch (RemoteException e) {
            e.printStackTrace();
        }



        if (lyricUtils.isExistsLyric()){
            handler.sendEmptyMessage(SHOW_LYRIC);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_LYRIC://显示歌词
                    //1.得到当前的进度
                    try {
                        int currentPosition = service.getCurrentPosition();
                        //2.把进度传入ShowLyricView控件，并且计算该高亮那一句
                        showLyricView.setShowNextLyric(currentPosition);
                        //3.实时的发消息
                        handler.removeMessages(SHOW_LYRIC);//如果注释掉，整个系统变慢
                        handler.sendEmptyMessage(SHOW_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                 break;

                case PROGRESS:
                    try {
                        //1.得到当前进度
                        int currentPosition = service.getCurrentPosition();

                        //2.设置SeekBar.setProgress(进度)
                        seekbarAudio.setProgress(currentPosition);

                        //3.时间进度更新
                        tvTime.setText(utils.stringForTime(currentPosition)+"/"+utils.stringForTime(service.getDuration()));

                        //4.每秒更新一次
                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    /**
     * 显示歌曲信息
     */
    private void showViewData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());
            //设置进度条的最大值
            seekbarAudio.setMax(service.getDuration());

            //发消息
            handler.sendEmptyMessage(PROGRESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.atguigu.mobileplayer_OPENAUDIO");
        //自动创建绑定
        bindService(intent,con, Context.BIND_AUTO_CREATE);
        startService(intent);//不至于实例化多个服务。

    }

    /**
     * 得到数据
     */
    private void getData(){
        //默认是false
        notification = getIntent().getBooleanExtra("Notification",false);
        //重新打开，没有传位置进去，默认打开的是位置0，默认播放的是第一首歌
        if (!notification){
            position = getIntent().getIntExtra("position",0);
        }
    }
    private void findViews(){
        setContentView(R.layout.activity_audio_player);

        //ivIcon = (ImageView) findViewById(R.id.iv_icon);
        //ivIcon.setBackgroundResource(R.drawable.animation_list);
        //AnimationDrawable rocketAnimation =(AnimationDrawable)ivIcon.getBackground();
        //rocketAnimation.start();
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlayMode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnLyric = (Button) findViewById(R.id.btn_lyrc);
        showLyricView = (ShowLyricView) findViewById(R.id.showLyricView);
        baseVisualizerView = (BaseVisualizerView) findViewById(R.id.baseVisualizerView);

        btnAudioPlayMode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyric.setOnClickListener(this);
        //设置视频的拖动
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }
    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
           if (fromUser){
               //拖动进度
               try {
                   service.seekTo(progress);
               } catch (RemoteException e) {
                   e.printStackTrace();
               }
           }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
    @Override
    public void onClick(View v) {
        if (v == btnAudioPlayMode){
           setPlayMode();
        }else if (v == btnAudioPre){
           if (service!=null){
               try {
                   service.pre();
               } catch (RemoteException e) {
                   e.printStackTrace();
               }
           }
        }else if (v == btnAudioStartPause){
            if (service!=null)
                try {
                    if (service.isPlaying()){
                        //暂停
                        service.pause();
                        //按钮播放
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    }else{
                        //播放
                        service.start();
                        //按钮-暂停
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }else if (v == btnAudioNext){
            if (service!=null){
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }else if (v == btnLyric){

        }
    }

    private void setPlayMode() {
        try {
            int playMode = service.getPlayMode();
            if (playMode==MusicPlayerService.REPEAT_NORMAL){
                playMode = MusicPlayerService.REPEAT_SINGLE;
            }else if (playMode == MusicPlayerService.REPEAT_SINGLE){
                playMode = MusicPlayerService.REPEAT_ALL;
            }else if (playMode == MusicPlayerService.REPEAT_ALL){
                playMode = MusicPlayerService.REPEAT_NORMAL;
            }else{
                playMode = MusicPlayerService.REPEAT_NORMAL;
            }
            //保存最新的状态
            service.setPlayMode(playMode);
            //设置图片
            showPlayMode(); 
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 展示播放模式，改变图片弹出吐司
     */
    private void showPlayMode() {
        try {
            int playMode = service.getPlayMode();
            if (playMode==MusicPlayerService.REPEAT_NORMAL){
               btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_playmode_nomal_selector);
                Toast.makeText(AudioPlayerActivity.this,"顺序播放", Toast.LENGTH_SHORT).show();
            }else if (playMode == MusicPlayerService.REPEAT_SINGLE){
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(AudioPlayerActivity.this,"单曲循环", Toast.LENGTH_SHORT).show();
            }else if (playMode == MusicPlayerService.REPEAT_ALL){
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(AudioPlayerActivity.this,"全部循环", Toast.LENGTH_SHORT).show();
            }else{
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_playmode_nomal_selector);
                Toast.makeText(AudioPlayerActivity.this,"顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验播放模式
     */
    private void checkPlayMode(){
        try {
            int playMode = service.getPlayMode();
            if (playMode==MusicPlayerService.REPEAT_NORMAL){
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_playmode_nomal_selector);
            }else if (playMode == MusicPlayerService.REPEAT_SINGLE){
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            }else if (playMode == MusicPlayerService.REPEAT_ALL){
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            }else{
                btnAudioPlayMode.setBackgroundResource(R.drawable.btn_audio_playmode_nomal_selector);
            }

            //校验播放和暂停的按钮
            if (service.isPlaying()){
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            }else{
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
//        //取消注册广播
//        if (receiver!=null){
//            unregisterReceiver(receiver);
//            receiver=null;//便于垃圾回收器去优先回收
//        }

        //2.EventBus取消注册
        EventBus.getDefault().unregister(this);
        //解绑服务
        if (con!=null){
            unbindService(con);
            con = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVisualizer!=null){
            mVisualizer.release();
        }
    }
}
