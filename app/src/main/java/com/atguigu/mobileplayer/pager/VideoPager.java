package com.atguigu.mobileplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.activity.SystemVideoPlayer;
import com.atguigu.mobileplayer.adapter.VideoPagerAdapter;
import com.atguigu.mobileplayer.base.BasePager;
import com.atguigu.mobileplayer.domain.MediaItem;
import com.atguigu.mobileplayer.utils.LogUtil;
import com.atguigu.mobileplayer.utils.Utils;

import java.util.ArrayList;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class VideoPager extends BasePager {
    private ListView list_view;
    private TextView tv_no_media;
    private ProgressBar pb_loading;
    private VideoPagerAdapter videoPageradapter;
    private Utils utils;
    //存放MediaItems的集合
    private ArrayList<MediaItem> mediaItems;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems!=null&&mediaItems.size()>0){
                //有数据，设置适配器，把文本隐藏
                videoPageradapter = new VideoPagerAdapter(context,mediaItems,true);
                list_view.setAdapter(videoPageradapter);
                //文本隐藏
                tv_no_media.setVisibility(View.GONE);
            }else{
                //没有数据，文本显示，
                tv_no_media.setVisibility(View.VISIBLE);
            }
         //把progressBar隐藏
            pb_loading.setVisibility(View.GONE);
        }
    };
    public VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        list_view = (ListView) view.findViewById(R.id.list_view);
        tv_no_media = (TextView) view.findViewById(R.id.tv_no_media);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        //设置ListView的item点击事件
        list_view.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }
 class MyOnItemClickListener implements AdapterView.OnItemClickListener{
     //第一个参数是ListView，第二个参数是视图，第三个是下标，
     @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         MediaItem mediaItem = mediaItems.get(position);
        // Toast.makeText(context,"mediaItem=="+mediaItem.toString(), Toast.LENGTH_SHORT).show();

         //1.调起系统所有的播放器,隐式意图
//         Intent intent = new Intent();
//         //Video/*表示所有格式的视频,调取所有播放视频的播放器
//         intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//         context.startActivity(intent);
         //2.调用自己写的播放器,显示意图
         Intent intent = new Intent(context,SystemVideoPlayer.class);
         intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
         context.startActivity(intent);
     }
 }
    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频数据被初始化了");
        //加载本地视频数据
        getDataFromLocal();
    }

    /**
     * 从本地的SDcard得到数据
     * 1.遍历SDcard，后缀名，太慢，性能不好，耗时过长，一般不用。
     * 2.内容提供者里面获取视频。
     * 有一个媒体扫描器，开机以后或者SD卡插入以后，开始扫描，它会将视频信息放入到
     * ContentProvider中，
     * 3.知识点，如果是6.0的安卓系统，需要加上动态权限。
     */
    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<MediaItem>();
                //根据上下文，得到内容解析者
                ContentResolver resolver = context.getContentResolver();
                //得到一个外部内容的URI
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {//视频文件在SDcard中的名称
                        MediaStore.Video.Media.DISPLAY_NAME,
                        //视频的总时长
                        MediaStore.Video.Media.DURATION,
                        //视频的大小
                        MediaStore.Video.Media.SIZE,
                        //视频在SD卡的绝对地址
                        MediaStore.Video.Media.DATA,
                        //如果是歌曲的话就是演唱者，视频可能没有这个属性。
                        MediaStore.Video.Media.ARTIST
                };
                //第二个参数是一个集合，有我们关心的字段,
                //query方法返回一个Cursor
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {//循环在这里控制
                        MediaItem mediaItem = new MediaItem();
                        //mediaItems.add(mediaItem);写在上面也一样。
                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);//视频的总时长
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);//视频的大小
                        mediaItem.setSize(size);
                        String data = cursor.getString(3);//视频的播放地址
                        mediaItem.setData(data);
                        String artist = cursor.getString(4);//艺术家，歌曲演唱者
                        mediaItem.setArtist(artist);
                        mediaItems.add(mediaItem);
                    }
                    cursor.close();
                    //在这里发消息有风险，如果cursor为null，进不来。
                }
                //在这里发消息 handler
                handler.sendEmptyMessage(10);
            }
        }.start();
    }
    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

}
