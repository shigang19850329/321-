package com.atguigu.mobileplayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.base.BasePager;
import com.atguigu.mobileplayer.pager.AudioPager;
import com.atguigu.mobileplayer.pager.NetAudioPager;
import com.atguigu.mobileplayer.pager.NetVideoPager;
import com.atguigu.mobileplayer.pager.VideoPager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private FrameLayout f1_main_content;
    private RadioGroup rg_bottom_tag;
    /**
     * 页面的集合
     */
    private ArrayList<BasePager> basePagers;
    //设置选中的位置
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//没写这句什么也不显示
        f1_main_content = (FrameLayout) findViewById(R.id.f1_main_content);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);


        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));//添加本地视频页面0
        basePagers.add(new AudioPager(this));//添加本地音乐页面1
        basePagers.add(new NetVideoPager(this));//添加网络视频页面2
        basePagers.add(new NetAudioPager(this));//添加网络音乐页面3

        //设置RadioGroup的点击监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_video);//设置默认选中
    }
    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
          switch (checkedId){
              default:
                  position = 0;
                  break;
              case R.id.rb_audio://音频
                  position = 1;
                  break;
              case R.id.rb_net_Video://音频
                  position = 2;
                  break;
              case R.id.rb_net_audio://音频
                  position = 3;
                  break;
          }
          setFragment();
        }
    }

    /**
     * 把我们的页面添加到Fragment中
     */
    private void setFragment() {
        //1得到FragmentManager
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换
         ft.replace(R.id.f1_main_content, new ReplaceFragment(getBasePager()));
        //4.提交事务
        ft.commit();
    }

    /**
     * 根据位置得到对应的页面
     * @return
     */

    public BasePager getBasePager(){
        BasePager basePager = basePagers.get(position);
        if (basePager!=null&&!basePager.isInitData){
            basePager.initData();//以后在这里面做一些联网请求，或者绑定数据之类的。
            basePager.isInitData = true;
        }
        return basePager;
    }

    /**
     * 是否已经退出
     */
   private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (position!=0){//不是第一个界面
                position = 0;
                rg_bottom_tag.check(R.id.rb_video);//选中首页
                return true;
            }else if (!isExit){
                isExit = true;
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                },2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
