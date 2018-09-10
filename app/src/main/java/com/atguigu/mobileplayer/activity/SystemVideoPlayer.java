package com.atguigu.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.domain.MediaItem;
import com.atguigu.mobileplayer.utils.Utils;
import com.atguigu.mobileplayer.view.VideoView;

import java.util.ArrayList;
import java.util.Date;


/**
 * 系统播放器
 */
public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private boolean isUseSystem = false;
    //视频进度的更新
    private static final int PROGRESS = 1;
    //隐藏控制面板
    private static final int HIDE_MEDIACONTROLLER = 2;
    //全屏的情况
    private static final int Full_SCREEN = 3;
    //默认屏幕大小的情况
    private static final int DEFAULT_SCREEN = 4;
    //显示网速
    private static final int SHOW_SPEED = 5;
    //屏幕的宽
    private int screenWidth = 0;
    //屏幕的高
    private int screenHeight = 0;
    //真实视频的宽
    private int videoWidth;
    //真实视频的高
    private int videoHeight;
    private VideoView videoView;
    private Uri uri;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchScreen;
    private Utils utils;
    private RelativeLayout media_controller;
    private TextView tv_net_speed;
    private LinearLayout ll_buffer;
    private TextView tv_loading_net_speed;
    private LinearLayout ll_loading;

    //是否全屏
    private boolean isFullScreen = false;
    /**
     * 传入进来的视频列表
     */
    private ArrayList<MediaItem> mediaItems;
    /*
    监听电量变化的广播，写成类的成员变量一会要注册。
     */
    private MyReceiver receiver;
    /*
    要播放的列表中的具体位置
     */
    private int position;
    /**
     * 1.定义手势识别器
     */
    private GestureDetector detector;
    //调节声音
    private AudioManager am;
    //当前音量
    private int currentVolume;
    //最大音量 0~15
    private int maxVolume;
    //是否静音
    private boolean isMute = false;
    /**
     * 是否网络
     */
    private boolean isNetUri = false;
    /**
    上一次的播放进度
     */
    private int precurrentPosition ;
    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-08-27 15:25:59 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSwitchScreen = (Button) findViewById(R.id.btn_video_switch_screen);
        videoView = (VideoView) findViewById(R.id.video_view);
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);
        tv_net_speed = (TextView) findViewById(R.id.tv_net_speed);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        tv_loading_net_speed  = (TextView) findViewById(R.id.tv_loading_net_speed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwitchScreen.setOnClickListener(this);

        //最大音量和SeekBar关联起来
        seekbarVoice.setMax(maxVolume);
        //设置当前的进度，当前音量
        seekbarVoice.setProgress(currentVolume);

        //发送消息，更新网速
        handler.sendEmptyMessage(SHOW_SPEED);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-08-27 15:25:59 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
            isMute = !isMute;//一点叫他取反
            updateVolume(currentVolume,isMute);
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
        } else if (v == btnExit) {
            // Handle clicks for btnExit
            finish();//退出
        } else if (v == btnVideoPre) {
            // Handle clicks for btnVideoPre
            playPreVideo();
        } else if (v == btnVideoStartPause) {
            startAndPause();

        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if (v == btnVideoSwitchScreen) {
            // Handle clicks for btnVideoSwitchScreen
            setFullScreenAndDefault();
        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
    }

    private void startAndPause() {
        // Handle clicks for btnVideoStartPause
        if (videoView.isPlaying()) {
            //视频在播放，设置为暂停
            videoView.pause();
            //按钮状态要设置为播放状态
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            //视频在暂停，设置为播放
            videoView.start();
            //按钮状态设置暂停
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放上一个
            position--;
            if (position >= 0) {
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放下一个
            position++;
            if (position < mediaItems.size()) {
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItem.getData());
                //设置按钮状态
                setButtonState();
            }
        } else if (uri != null) {
            //把上一个和下一个按钮变成灰色，并且不可以点击
            setButtonState();
        }

    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //它刚好是等于一个的时候
            if (mediaItems.size() == 1) {
                //两个按钮都灰色
                setEnable(false);
            } else if (mediaItems.size() == 2) {
                if (position == 0) {
                    //如果是第一个视频，上一个不可点
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == mediaItems.size() - 1) {
                    //如果是最后一个视频，下一个不可点
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);

                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                }
            } else {
                if (position == 0) {
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                } else if (position == mediaItems.size() - 1) {
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                } else {
                    //如果既不是第一个也不是最后一个，设置上一个按钮和下一个按钮为正常状态
                    setEnable(true);
                }
            }

        } else if (uri != null) {
            //两个按钮设置灰色
            setEnable(false);

        }
    }

    private void setEnable(boolean isEnable) {
        if (isEnable) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        } else {//两个按钮都设置成灰色
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //要处理多种事情，所以区分一下
            switch (msg.what) {
                case SHOW_SPEED:
                    //得到网速
                    String netSpeed = utils.getNetSpeed(SystemVideoPlayer.this);

                    //显示网速，1是加载的过程中显示，2.卡的过程中显示
                    tv_loading_net_speed.setText("玩命加载中..."+netSpeed);
                    tv_net_speed.setText("缓存中..."+netSpeed);

                     //两秒钟调用一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED,2000);
                    break;
                case PROGRESS:
                    //1.得到当前播放进度，
                    int currentPosition = videoView.getCurrentPosition();
                    //2.SeekBar.setProgressBar(当前进度);
                    seekbarVideo.setProgress(currentPosition);
                    //更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    //设置系统时间
                    tvSystemTime.setText(getSystemTime());
                    //3.缓冲进度的更新，每秒更新一次
                    if (isNetUri){
                        //只有网络的视频才有缓冲效果
                        //得到一个缓冲百分比，0~100之间的值
                        int buffer =  videoView.getBufferPercentage();
                        //首先seekBar.getMax();得到一个最大值,百分比跟这个最大值相乘
                        int totalBuffer = buffer*seekbarVideo.getMax();
                        //得除以一百
                        int secondProgress = totalBuffer/100;
                        seekbarVideo.setSecondaryProgress(secondProgress);
                    }else{
                        //本地的视频没有缓冲效果
                      seekbarVideo.setSecondaryProgress(0);
                    }
                    //监听卡
                    if (!isUseSystem) {

                        if(videoView.isPlaying()){
                            int buffer = currentPosition - precurrentPosition;
                            if (buffer < 500) {
                                //视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                //视频不卡了
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }else{
                            ll_buffer.setVisibility(View.GONE);
                        }

                    }


                    precurrentPosition = currentPosition;
                    //移除消息
                    handler.removeMessages(PROGRESS);
                    //延迟一秒再发送消息
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;
            }
        }
    };

    /**
     * 得到系统时间
     *
     * @return
     */
    private String getSystemTime() {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
        //每循环一次new一个date对象，没有类引用它过一会会释放。
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        setListener();
        getData();
        setData();
    }

    private void setData() {
        //判断列表不为空
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());//设置视频的名称
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoView.setVideoPath(mediaItem.getData());//设置播放地址

        } else if (uri != null) {
            tvName.setText(uri.toString());//设置视频的名称
            isNetUri = utils.isNetUri(uri.toString());
            videoView.setVideoURI(uri);
        } else {
            Toast.makeText(SystemVideoPlayer.this, "帅哥你没有传递数据", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void getData() {
        //得到播放地址
        uri = getIntent().getData();

        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        //第二个参数默认是0
        position = getIntent().getIntExtra("position", 0);

    }

    private void initData() {
        utils = new Utils();
        //注册电量广播
        //1.注册广播接收器
        receiver = new MyReceiver();
        //创建意图过滤器
        IntentFilter intentFilter = new IntentFilter();
        //当电量变化的时候发这个广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
        //2.实例化手势识别器，并且重写我们的单击，双击，长按
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startAndPause();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setFullScreenAndDefault();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //Toast.makeText(SystemVideoPlayer.this,"我被单击了", Toast.LENGTH_SHORT).show();
                if (isShowMediaController) {
                    //隐藏
                    hideMediaController();
                    //把消息移除
                    handler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {//显示
                    showMediaController();
                    //发消息
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
        //得到屏幕的高和宽
        //得到屏幕高和宽的新的方式
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    private void setFullScreenAndDefault() {
        if (isFullScreen) {
            //默认
            setVideoType(DEFAULT_SCREEN);
        } else {
            //全屏
            setVideoType(Full_SCREEN);
        }
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen) {
            case Full_SCREEN://全屏
                //1.设置屏幕大小，屏幕有多大就有多大。
                videoView.setVideoSize(screenWidth, screenHeight);
                //2.设置按钮的状态,默认
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN://默认
                //1.设置屏幕大小，
                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;
                //for compatibility,we adjust size based on aspect ratio;方向比例
                if (mVideoWidth * height < width * mVideoHeight) {
                    //log.i("@@@","image too wide,correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //log.i("@@@","image too tall,correcting");
                    height = width * mVideoHeight / width;
                }
                videoView.setVideoSize(width, height);
                //2.设置按钮的状态，全屏
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_full_selector);
                isFullScreen = false;

                break;
        }
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//0~100
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        //设置控制面板
        //准备好的监听,实现这个接口
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        //播放出错的监听
        videoView.setOnErrorListener(new MyOnErrorListener());
        //播放完成的监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());

        //显示控制面板
        //videoView.setMediaController(new MediaController(this));

        //设置SeekBar状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
        //设置音量变化状态监听
        seekbarVoice.setOnSeekBarChangeListener(new VolumeOnSeekBarChangeListener());
        if (isUseSystem){
            //监听视频播放卡,对版本进行判断，只有大于和等于17版本4.2.2的时候执行下列方法。
            //系统的API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MyOnInfoListener());
            }
        }
    }
    class MyOnInfoListener implements MediaPlayer.OnInfoListener{
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始卡的标志,视频卡了，拖动卡了。
                    //让这个layout显示出来
                   ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡结束了。
                    ll_buffer.setVisibility(View.GONE);//不卡了，隐藏起来
                    break;
            }
            return true;
        }
    }
    class VolumeOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress>0){
                    isMute = false;
                }else{
                    isMute = true;
                }
                //修改音量,滑动过程中让isMute变false
                updateVolume(progress,false);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //按下去移除消息
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //移开的时候再发送消息
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
        }
    }

    /**
     * 设置音量的大小
     *
     * @param progress
     */
    private void updateVolume(int progress,boolean isMute) {
        if (isMute){
            //如果静音的话，都改为0
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        }else{
            //第三个参数如果是1的话就把系统的音量调节调出来，如果是0的话就不调。
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
            currentVolume = progress;
        }

    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当手指滑动的时候会引起我们的SeekBar的进度变化，会回调这个方法
         * 自动更新的时候也会调用此方法，如果是用户引起的，这个boolean是true
         * 不是用户引起的，是false
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoView.seekTo(progress);
            }
        }

        /**
         * 当我们的手指触碰的时候，会回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        /**
         * 当我们的手指离开的时候，会回调这个方法
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
            videoView.start();//开始播放了。
            //mp.getDuration();
            //得到视频的总时长，
            int duration = videoView.getDuration();
            //和SeekBar关联起来
            seekbarVideo.setMax(duration);
            //设置播放总时间
            tvDuration.setText(utils.stringForTime(duration));

            hideMediaController();//默认是隐藏控制面板

            //发消息
            handler.sendEmptyMessage(PROGRESS);

            setVideoType(DEFAULT_SCREEN);//准备好了设置默认播放。
            //拖动完成的监听
//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//            @Override
//            public void onSeekComplete(MediaPlayer mp) {
//                Toast.makeText(SystemVideoPlayer.this,"拖动完成", Toast.LENGTH_SHORT).show();
//            }
//        });
            //把加载页面取消掉
            ll_loading.setVisibility(View.GONE);
     }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //Toast.makeText(SystemVideoPlayer.this, "播放出错了哦", Toast.LENGTH_SHORT).show();
            //如果返回false，会默认弹出一个对话框，
            //1.播放视频的格式不支持，2.播放网络视频的时候，网络中断，3.播放的视频中间有空白，不完整。
            return false;
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //Toast.makeText(SystemVideoPlayer.this,"播放完成了="+uri, Toast.LENGTH_SHORT).show();
            playNextVideo();
        }
    }

    @Override
    public void onProvideAssistData(Bundle data) {
        super.onProvideAssistData(data);
    }

    @Override
    protected void onDestroy() {
        //释放资源的时候，先释放子类，再释放父类。
        //注销receiver
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();//写在后面
    }
   private float startY ;//起始位置
    /**
     *屏幕的高
     */
    private float touchRang;
    /**
     * 当一按下时候的音量，不能用currentVolume，那个在不断地变化。
     */
    private int mVol;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                //1.按下记录值
                startY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                //返回其中较小的那个值
                touchRang = Math.min(screenHeight,screenWidth);//screenHeight
                handler.removeMessages(HIDE_MEDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //移动，记录相关数值
                float endY = event.getY();
                float distanceY = startY-endY;
                //改变声音 = （滑动屏幕的距离：总距离）*总音量
                float delta = distanceY/touchRang*maxVolume;
                    //最终声音 = 原来的+改变的声音
                int voice = (int)Math.min(Math.max(mVol+delta,0),maxVolume);
                if (delta!=0){
                    isMute = false;
                    updateVolume(voice,isMute);
                }
                //startY = event.getY();//不能加，距离不敏感了
                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                break;
        }
        //它只是对这个事件进行解析，它不拦截事件。
        return super.onTouchEvent(event);
    }

    //是否显示控制面板
    private boolean isShowMediaController = false;

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        media_controller.setVisibility(View.VISIBLE);
        isShowMediaController = true;
    }

    //隐藏控制面板
    private void hideMediaController() {
        media_controller.setVisibility(View.GONE);
        isShowMediaController = false;
    }
    /**
     *   监听物理键，实现声音的调节大小
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //处理物理按键监听
        if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVolume--;
            updateVolume(currentVolume,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;//如果写成false，系统和自定义的音量都改变
        }else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            currentVolume++;
            updateVolume(currentVolume,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
