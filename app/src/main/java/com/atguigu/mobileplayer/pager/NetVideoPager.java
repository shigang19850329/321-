package com.atguigu.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.activity.SystemVideoPlayer;
import com.atguigu.mobileplayer.adapter.NetVideoPagerAdapter;
import com.atguigu.mobileplayer.base.BasePager;
import com.atguigu.mobileplayer.domain.MediaItem;
import com.atguigu.mobileplayer.utils.CacheUtils;
import com.atguigu.mobileplayer.utils.Constants;
import com.atguigu.mobileplayer.utils.LogUtil;
import com.atguigu.mobileplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;

import static com.atguigu.mobileplayer.R.id.list_view;
import static com.atguigu.mobileplayer.R.id.pb_loading;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class NetVideoPager extends BasePager {
    //这个注解的意思是把这个类和这个框架关联起来，直接用
    @ViewInject(list_view)
    private XListView mListView;

    @ViewInject(R.id.tv_no_net)
    private TextView mTv_no_net;

    @ViewInject(pb_loading)
    private ProgressBar mProgressBar;
    /**
     * 装数据集合
     */
    private ArrayList<MediaItem> mediaItems;

    private NetVideoPagerAdapter adapter;

    /**
     * 是否已经加载更多了
     */
    private boolean isLoadMore = false;

    public NetVideoPager(Context context) {
        super(context);
    }

    /**
     * 初始化当前页面的控件，由父类调用
     * @return
     */
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.net_video_pager,null);

        //第一个参数是：NetVideoPager.this,第二个参数：布局 inject 注入
        x.view().inject(this,view);

        //设置item的点击事件
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new MYIXListViewListener());
        return view;
    }
    class MYIXListViewListener implements XListView.IXListViewListener{
        @Override
        public void onRefresh() {
         getDataFromNet();
        }

        @Override
        public void onLoadMore() {
         //加载更多，原理上数据应该更多
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功=="+result);
                isLoadMore = true;
                processData(result);//解析数据，主线程
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败=="+ex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled=="+cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
                isLoadMore = false;
            }
        });
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime("更新时间:"+getSystemTime());
    }
    /**
     * 得到系统时间
     *
     * @return
     */
    public String getSystemTime() {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //3.传递数据列表，对象，序列化
            Intent intent = new Intent(context, SystemVideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position-1);
            context.startActivity(intent);
        }
    }
    @Override
    public void initData(){
        super.initData();
        LogUtil.e("网络视频数据被初始化了");
        String saveJson = CacheUtils.getString(context,Constants.NET_URL);
        if (!TextUtils.isEmpty(saveJson)){
            processData(saveJson);
        }
        getDataFromNet();

    }

    private void getDataFromNet() {
        //联网请求
        //视频内容
        RequestParams params = new RequestParams(Constants.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功=="+result);
                //缓存数据
                CacheUtils.putString(context,Constants.NET_URL,result);
                //解析数据，主线程
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
               LogUtil.e("联网失败=="+ex.getMessage());
                //如果出错了，就把textview显示出来，progressBar消失
                mTv_no_net.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled=="+cex.getMessage());
            }

            @Override
            public void onFinished() {
              LogUtil.e("onFinished==");
            }
        });
    }

    /**
     * 加工数据
     * @param json
     */
    private void processData(String json) {
        if (!isLoadMore){
            mediaItems = parseJson(json);
            showData();
        }else{
            isLoadMore = false;
            //加载更多，要把得到更多的数据，添加到原来的集合中
            mediaItems.addAll(parseJson(json));
            //刷新适配器
            adapter.notifyDataSetChanged();
            //一定要调用这个方法
            onLoad();
        }
    }

    private void showData() {
        //设置适配器
        if (mediaItems!=null&&mediaItems.size()>0){
            //文本隐藏
            adapter = new NetVideoPagerAdapter(context,mediaItems);
            mListView.setAdapter(adapter);
            onLoad();
            mTv_no_net.setVisibility(View.GONE);
        }else{
            //没有数据，文本显示，
            mTv_no_net.setVisibility(View.VISIBLE);
        }
        //把progressBar隐藏
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 解析json数据
     * 1.用系统接口解析json数据
     * 2.使用第三方解析工具（Gson,fastjson）
     *
     * @param json
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        //处理异常，为空会报错。
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            //jsonObject.getJSONObject("trailers");
            //不用getJSONObject方法因为如果这个字段为空，会崩溃
            if (jsonArray!=null&&jsonArray.length()>0){
               for (int i = 0; i < jsonArray.length(); i++) {
                   //返回的是一个json对象
                   JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                   if (jsonObjectItem!=null){
                       MediaItem mediaItem = new MediaItem();

                       String movieName = jsonObjectItem.optString("movieName");//name
                       mediaItem.setName(movieName);

                       String videoTitle = jsonObjectItem.optString("videoTitle");//desc
                       mediaItem.setDesc(videoTitle);

                       String imageUrl = jsonObjectItem.optString("coverImg");//imageUrl
                       mediaItem.setImageUrl(imageUrl);

                       String hightUrl = jsonObjectItem.optString("hightUrl");//data
                       mediaItem.setData(hightUrl);

                       //把数据添加到集合中，适配器要求的是集合，返回要求的也是集合
                       mediaItems.add(mediaItem);//放在前面也可以。
                   }
               }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }
}
