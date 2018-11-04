package com.atguigu.mobileplayer.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.activity.SearchActivity;

/**
 * 自定义标题栏
 */
public class TitleBar extends LinearLayout implements View.OnClickListener{
    //用View有个好处是不管子布局是什么类型都会兼容
    private View tv_search;
    private View rl_game;
    private View iv_record;
    private Context context;
    //在代码中实例化该类的时候，使用这个方法。
    public TitleBar(Context context) {
        this(context,null);
    }
  //当在布局文件中使用这个类的时候，Android系统通过这个构造方法实例化该类
    public TitleBar(Context context, @Nullable AttributeSet attrs) {
       this(context, attrs,0);//传一个样式，默认传一个0进去。
    }
 //当需要设置样式的时候来可以使用该方法。
    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }
//布局文件加载完了的时候回调这个方法
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子的实例
        tv_search = getChildAt(1);
        rl_game = getChildAt(2);
        iv_record = getChildAt(3);

        //设置点击事件
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //必须是v.getId()不然点击不起作用
        switch (v.getId()){
            case R.id.tv_search:
                //Toast.makeText(context,"搜索", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,SearchActivity.class);
                context.startActivity(intent);
                break;
            case R.id.rl_game:
                Toast.makeText(context,"游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record:
                Toast.makeText(context,"播放历史", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
