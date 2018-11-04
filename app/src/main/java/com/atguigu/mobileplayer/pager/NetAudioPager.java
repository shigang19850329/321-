package com.atguigu.mobileplayer.pager;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.adapter.NetAudioPagerAdapter;
import com.atguigu.mobileplayer.base.BasePager;
import com.atguigu.mobileplayer.domain.NetAudioPagerData;
import com.atguigu.mobileplayer.utils.CacheUtils;
import com.atguigu.mobileplayer.utils.Constants;
import com.atguigu.mobileplayer.utils.LogUtil;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class NetAudioPager extends BasePager {
    @ViewInject(R.id.list_view)//可以用xutils这样实例化
    private ListView list_view;

    @ViewInject(R.id.tv_no_net)
    private TextView tv_no_net;

    @ViewInject(R.id.pb_loading)
    private ProgressBar pb_loading;

    private List<NetAudioPagerData.ListBean> datas;

    private NetAudioPagerAdapter adapter;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_audio_pager,null);
        //让这个类和这个布局关联起来，可以实例化里面的控件
        x.view().inject(NetAudioPager.this,view);
        return view;
    }
    @Override
    public void initData(){
        super.initData();
        LogUtil.e("网络音乐数据被初始化了");
        String saveJson = CacheUtils.getString(context,Constants.ALL_RES_URL);
        if (!TextUtils.isEmpty(saveJson)){
            //解析数据
            processData(saveJson);
        }
        //联网
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功=="+result);
                //保存数据
                CacheUtils.putString(context,Constants.ALL_RES_URL,result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
               LogUtil.e("请求数据失败=="+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled== "+cex.getMessage());
            }

            @Override
            public void onFinished() {
               LogUtil.e("onFinished==");
            }
        });
    }

    /**
     * 解析json数据和显示数据
     * 解析数据：1.GsonFormat生成bean对象;2.用gson解析数据生成一个bean
     * @param json
     */
    private void processData(String json) {
        NetAudioPagerData data = parsedJson(json);
        datas = data.getList();

        if (datas!=null&&datas.size()>0){
            //有数据
            tv_no_net.setVisibility(View.GONE);
            //设置适配器
            adapter = new NetAudioPagerAdapter(context,datas);
            list_view.setAdapter(adapter);
        }else{
            tv_no_net.setText("没有对应的数据...");
            //没有数据
            tv_no_net.setVisibility(View.VISIBLE);
        }

        pb_loading.setVisibility(View.GONE);
    }

    /**
     * Gson解析数据
     * @param json
     * @return
     */
    private NetAudioPagerData parsedJson(String json) {
        return new Gson().fromJson(json,NetAudioPagerData.class);
    }
}
