package com.atguigu.mobileplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.atguigu.mobileplayer.domain.Lyric;
import com.atguigu.mobileplayer.utils.DensityUtil;
import com.atguigu.mobileplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * 作者： 石刚
 * QQ号 342532640
 * 作用:xxx
 */
public class ShowLyricView extends TextView{
    /**
     * 歌词列表
     */
    private ArrayList<Lyric> lyrics;
    /**
     * 画笔
     */
    private Paint paint;
    private Paint whitePaint;

    private int width;
    private int height;
    /**
     * 歌词列表中的索引
     */
    private int index;
    /**
     * 每一行的高
     */
    private float textHeight;
    /**
     * 当前播放进度
     */
    private float currentPosition;
    /**
     * 高亮显示的时间或者休眠时间
     */
    private float sleepTime;
    /**
     * 时间戳，什么时刻到高亮哪句歌词
     */
    private long timePoint;

    /**
     * 设置歌词列表
     * @param lyrics
     */
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context,null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 得到控件的宽和高
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView(Context context){
        textHeight = DensityUtil.dip2px(context,18);//对应的像素
        LogUtil.e("textHeight=="+textHeight);
        //创建画笔
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(DensityUtil.dip2px(context,16));
        paint.setAntiAlias(true);//抗锯齿
        //设置居中对齐
        paint.setTextAlign(Paint.Align.CENTER);

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(DensityUtil.dip2px(context,16));
        whitePaint.setAntiAlias(true);
        whitePaint.setTextAlign(Paint.Align.CENTER);
//
//        lyrics = new ArrayList<>();
//        //模拟假的歌词
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 1000; i++) {
//            lyric.setTimePoint(1000*i);//1秒，2秒，3秒...
//            lyric.setSleepTime(1500*i);//高亮时间
//            lyric.setContent(i+"aaaaaaaaaaaaaa"+i);
//            //把歌词添加到集合中
//            lyrics.add(lyric);
//            lyric = new Lyric();//如果不重新创建一个会总写在这一个里面
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics!=null&&lyrics.size()>0){//我这里写成了&

            //往上推移
            float push = 0;
            if (sleepTime == 0){
                push = 0;
            }else{
                //平移
                //这一句所花的时间:休眠时间=移动的距离:总距离（行高）
                //移动的距离 = （这一句所花的时间:休眠时间）*总距离
                //当前进度-时间点
                float delta = ((currentPosition-timePoint)/sleepTime)*textHeight;
                //屏幕的坐标 = 行高 + 移动的距离
                push = (float)(textHeight +delta);
            }
            canvas.translate(0,-push);
            //绘制歌词:绘制当前句
            String currentText = lyrics.get(index).getContent();
            canvas.drawText(currentText,width/2,height/2,paint);
            //绘制前面部分
            float tempY = height/2;//Y轴中间坐标
            for (int i = index-1; i >=0 ; i--) {
                //得到每一句歌词
                String preContent = lyrics.get(i).getContent();

                tempY = tempY-textHeight;
                if (tempY<0){
                    break;
                }
                canvas.drawText(preContent,width/2,tempY,whitePaint);//绘制上一句
            }
            //绘制后面部分
            tempY = height/2;//Y轴中间坐标
            for (int i = index+1; i <=lyrics.size(); i++) {
                //得到每一句歌词
                String nextContent = lyrics.get(i).getContent();

                tempY = tempY+textHeight;
                if (tempY>height){
                    break;
                }
                canvas.drawText(nextContent,width/2,tempY,whitePaint);//绘制上一句
            }

        }else{
            //没有歌词
            canvas.drawText("没有歌词",width/2,height/2,paint);
        }
    }

    /**
     * 根据当前播放的位置，找出该高亮显示哪句歌词
     * @param currentPosition
     */
    public void setShowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null||lyrics.size()==0)
            return;
        //从位置1开始
        for (int i = 1; i <lyrics.size(); i++) {
            //如果小于这个时间戳
            if (currentPosition<lyrics.get(i).getTimePoint()){
                int tempIndex = i-1;
                if (currentPosition>=lyrics.get(tempIndex).getTimePoint()){
                    //当前正在播放的那句歌词
                    index = tempIndex;
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimePoint();
                }
            }
        }
        //重新绘制
        invalidate();//在主线程中
        //子线程
        // postInvalidate();
    }
}
