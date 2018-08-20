package com.atguigu.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * 基类，公共类
 * VideoPager
 * AudioPager
 * NetVideoPager
 * NetAudioPager
 */
public abstract class BasePager {
    /**
     * 上下文
     *
     * @param context
     */
    public final Context context;
    private View rootView;

    public BasePager(Context context) {
        this.context = context;
        rootView = initView();
    }

    /**
     * 强制由孩子实现，抽象方法
     *
     * @return
     */
    public abstract View initView();

    /**
     * 当主页面需要初始化数据，联网请求数据，
     * 或者绑定数据的时候要重写该方法
     */
    public void initData() {

    }
}
