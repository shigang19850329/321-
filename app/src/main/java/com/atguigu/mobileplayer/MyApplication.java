package com.atguigu.mobileplayer;

import android.app.Application;

import org.xutils.x;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        //是否输出DEBUG日志，开启debug会影响性能
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
