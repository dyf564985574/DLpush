package com.toc.dlpush.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.R;
import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.util.UtilTool;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 袁飞 on 2015/5/21.
 * DLpush
 */
public class NoticesAdapter extends BaseAdapter {
    private List<NotifyJsonVo> list=new ArrayList<NotifyJsonVo>();
    private Context context;
    private LayoutInflater inflater;


    public NoticesAdapter(Context context,List<NotifyJsonVo> list1){
          this.context=context;
          this.list=list1;
          this.inflater=LayoutInflater.from(context);
    }
    public void notifyDataSetChangedEx(List<NotifyJsonVo>  list01){

        if (list01 == null) {
            list01 = new ArrayList<NotifyJsonVo>();
        }
        this.list = list01;

        this.notifyDataSetChanged();
    }
    public int getCount() {
        return this.list==null? 0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.notices_item,null);
            holder.notices_item_img= (ImageView) convertView.findViewById(R.id.notices_item_img);
            holder.notices_item_title= (TextView) convertView.findViewById(R.id.notices_item_title);
            holder.notices_item_content= (TextView) convertView.findViewById(R.id.notices_item_content);
            holder.notices_background= (LinearLayout) convertView.findViewById(R.id.notices_background);
            holder.notices_item_ce= (ImageView) convertView.findViewById(R.id.notices_item_ce);
            holder.watermark = (LinearLayout) convertView.findViewById(R.id.watermark);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        //变线
        if("change".equals(list.get(position).getNotifytype())){

            if("is".equals(list.get(position).getIsread())){
                holder.notices_item_img.setImageResource(R.mipmap.changeline_yidu);
                holder.notices_item_ce.setImageResource(R.mipmap.ele_ce_yidu);
                holder.notices_item_title.setTextColor(holder.notices_item_title.getResources().getColor(R.color.notices_title_read));
                holder.notices_item_content.setTextColor(holder.notices_item_content.getResources().getColor(R.color.notices_info_read));
            }else{
                holder.notices_item_img.setImageResource(R.mipmap.changeline_weidu);
                holder.notices_item_ce.setImageResource(R.mipmap.ele_ce_weidu);
                holder.notices_item_title.setTextColor(holder.notices_item_title.getResources().getColor(R.color.notices_title));
                holder.notices_item_content.setTextColor(holder.notices_item_content.getResources().getColor(R.color.notices_info));

            }
//临时
        }else if("temp".equals(list.get(position).getNotifytype())){
            if("is".equals(list.get(position).getIsread())){
                holder.notices_item_img.setImageResource(R.mipmap.temporary_yidu);
                holder.notices_item_ce.setImageResource(R.mipmap.ele_ce_yidu);
                holder.notices_item_title.setTextColor(holder.notices_item_title.getResources().getColor(R.color.notices_title_read));
                holder.notices_item_content.setTextColor(holder.notices_item_content.getResources().getColor(R.color.notices_info_read));
            }else{
                holder.notices_item_img.setImageResource(R.mipmap.temporary_weidu);
                holder.notices_item_ce.setImageResource(R.mipmap.ele_ce_weidu);
                holder.notices_item_title.setTextColor(holder.notices_item_title.getResources().getColor(R.color.notices_title));
                holder.notices_item_content.setTextColor(holder.notices_item_content.getResources().getColor(R.color.notices_info));

            }
//计划
        }else{
            if("is".equals(list.get(position).getIsread())){
                holder.notices_item_img.setImageResource(R.mipmap.calendar_yidu);
                holder.notices_item_ce.setImageResource(R.mipmap.ele_ce_yidu);
                holder.notices_item_title.setTextColor(holder.notices_item_title.getResources().getColor(R.color.notices_title_read));
                holder.notices_item_content.setTextColor(holder.notices_item_content.getResources().getColor(R.color.notices_info_read));
            }else{
                holder.notices_item_img.setImageResource(R.mipmap.calendar_weidu);
                holder.notices_item_ce.setImageResource(R.mipmap.ele_ce_weidu);
                holder.notices_item_title.setTextColor(holder.notices_item_title.getResources().getColor(R.color.notices_title));
                holder.notices_item_content.setTextColor(holder.notices_item_content.getResources().getColor(R.color.notices_info));

            }
        }
            holder.notices_item_title.setText(list.get(position).getTitle());
            holder.notices_item_content.setText(list.get(position).getInfo());
//        holder.notices_item_title.setText(list.get(position).getTitle());
        return convertView;
    }
    class ViewHolder {
        ImageView notices_item_img;
        TextView notices_item_title;//题目
        TextView notices_item_content;//内容
        LinearLayout notices_background;
        ImageView notices_item_ce;//箭头
        LinearLayout watermark;//水印
    }
}

