package com.atguigu.mobileplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.atguigu.mobileplayer.base.BasePager;
import com.atguigu.mobileplayer.utils.LogUtil;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class NetAudioPager extends BasePager {
    private TextView textView;
    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("网络音乐页面被初始化了");
        textView = new TextView(context);
        //设置字体大小25sp
        textView.setTextSize(25);
        //设置居中
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);//字体红色
        return null;
    }
    @Override
    public void initData(){
        super.initData();
        LogUtil.e("网络音乐数据被初始化了");
        textView.setText("网络音乐页面");
    }
}
