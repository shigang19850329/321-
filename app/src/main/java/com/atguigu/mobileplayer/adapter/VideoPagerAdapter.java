package com.atguigu.mobileplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.domain.MediaItem;
import com.atguigu.mobileplayer.utils.Utils;

import java.util.ArrayList;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class VideoPagerAdapter extends BaseAdapter {
    private final boolean isVideo;
    private Context context;
    private final ArrayList<MediaItem> mediaItems;
    //utils必须new 出来
    private Utils utils;

    public VideoPagerAdapter( Context context, ArrayList<MediaItem> mediaItems,boolean isVideo) {
        this.isVideo = isVideo;
        this.context = context;
        this.mediaItems = mediaItems;
        this.utils = new Utils();
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView= View.inflate(context, R.layout.item_video_pager,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

           convertView.setTag(viewHolder);

        }else{
           viewHolder = (ViewHolder) convertView.getTag();
        }
        //根据position得到列表中对应位置的数据
         MediaItem mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());
        //需要转换大小，转换成MB,Formatter是android.text.format.Formatter类里的;
        viewHolder.tv_size.setText(Formatter.formatFileSize(context,mediaItem.getSize()));
        //时间也需要转换，用Util这个工具
        viewHolder.tv_time.setText(utils.stringForTime((int)mediaItem.getDuration()));

        if (!isVideo){
            //音频
            viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }
        return convertView;
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }

}
