package com.atguigu.mobileplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.mobileplayer.R;
import com.atguigu.mobileplayer.domain.SearchBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes NetVideoPager
 */
public class SearchAdapter extends BaseAdapter {
    private Context context;
    private final List<SearchBean.ItemData> mediaItems;


    public SearchAdapter(Context context, List<SearchBean.ItemData> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
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
            convertView= View.inflate(context, R.layout.item_net_video_pager,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);

           convertView.setTag(viewHolder);

        }else{
           viewHolder = (ViewHolder) convertView.getTag();
        }
        //根据position得到列表中对应位置的数据
         SearchBean.ItemData mediaItem = mediaItems.get(position);
        viewHolder.tv_name.setText(mediaItem.getItemTitle());
        viewHolder.tv_desc.setText(mediaItem.getKeywords());//得到描述信息
        //使用xUtils3请求图片
        //x.image().bind(viewHolder.iv_icon,mediaItem.getImageUrl());
        //使用Glide加载图片
       Glide.with(context)
                .load(mediaItem.getItemImage().getImgUrl1())
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .placeholder(R.drawable.video_default)//加载占位图
                .error(R.drawable.video_default)//出错的时候显示的图片
                .into(viewHolder.iv_icon);
        //使用Picasso请求图片
        /*Picasso.with(context)
                .load(mediaItem.getImageUrl())
                .placeholder(R.drawable.video_default)//加载占位图
                .error(R.drawable.video_default)//出错的时候显示的图片
                .into(viewHolder.iv_icon);*/

        return convertView;
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }

}
