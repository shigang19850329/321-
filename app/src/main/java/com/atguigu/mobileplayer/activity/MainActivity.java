package com.atguigu.mobileplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.atguigu.mobileplayer.R;

public class MainActivity extends Activity {
    private FrameLayout f1_main_content;
    private RadioGroup rg_bottom_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//没写这句什么也不显示
        f1_main_content = (FrameLayout) findViewById(R.id.f1_main_content);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
        rg_bottom_tag.check(R.id.rb_video);//设置默认选中
    }
}
