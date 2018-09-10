package com.atguigu.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class VideoView extends android.widget.VideoView {
    //在代码中创建的时候一般用这个方法
    public VideoView(Context context) {
       this(context,null);
    }
  //这个方法当这个类布局文件的时候，系统通过该构造方法实例化该类
    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
 //如果省略，会崩溃。当需要设置样式的时候调用它
    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
    //设置视频的宽和高,指定视频的宽和高
    public void setVideoSize(int videoWidth,int videoHeight){
        ViewGroup.LayoutParams  params = getLayoutParams();
        params.width = videoWidth;
        params.height = videoHeight;
        //requestLayout();//中心请求layout
        setLayoutParams(params);//内部也是调用了requestLayout
    }
}
