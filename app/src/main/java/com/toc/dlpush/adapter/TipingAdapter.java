package com.toc.dlpush.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.R;
import com.toc.dlpush.tips.util.Comment;
import com.toc.dlpush.tips.util.submins;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 袁飞 on 2015/6/1.
 * DLpush
 */
public class TipingAdapter extends BaseAdapter {
   List<Comment> subminsList=new ArrayList<Comment>();
   Context context;
   private LayoutInflater inflater;
    Comment comment;


   public TipingAdapter (Context context,List<Comment> subminsList){
       this.subminsList=subminsList;
       this.context=context;
       this.inflater=LayoutInflater.from(context);
   }
   public void notifyDataSetChangedEx(List<Comment>  list01){
       if (list01 == null) {
           list01 = new ArrayList<Comment>();
       }
       this.subminsList = list01;

       this.notifyDataSetChanged();
   }




    @Override
    public int getCount() {
        return this.subminsList == null ? 0 : subminsList.size();
    }

    @Override
    public Object getItem(int position) {
        return subminsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.tiping_item,null);
            holder.tiping_item_content= (TextView) convertView.findViewById(R.id.tiping_item_content);
            holder.tiping_item_title= (TextView) convertView.findViewById(R.id.tiping_item_title);
            holder.suggest_color= (RelativeLayout) convertView.findViewById(R.id.suggest_color);
            /*antongshu 新增删除方法和按钮 atart*/
            holder.suggest_deal= (TextView) convertView.findViewById(R.id.suggest_deal);
            holder.watermark = (LinearLayout) convertView.findViewById(R.id.watermark);
            /*antongshu 新增删除方法和按钮 end*/
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        if(subminsList.size()==0){
            Log.i("subminsList","subminsList");
            holder.watermark.setVisibility(View.VISIBLE);
        }else{
            holder.watermark.setVisibility(View.GONE);
        }
        comment=subminsList.get(position);
        if("comment".equals(comment.getType())){
            holder.tiping_item_title.setText(comment.getCompanyname()+"的意见");
        }else{
            holder.tiping_item_title.setText(comment.getCompanyname()+"的建议");
        }
            holder.tiping_item_content.setText(comment.getCreatdate());
        if("0".equals(comment.getStatus())){
            Drawable drawable= context.getResources().getDrawable(R.drawable.processed);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.suggest_color.setBackground(drawable);

            holder.suggest_deal.setText("已处理");
        }else{
            Drawable drawable= context.getResources().getDrawable(R.drawable.untreated);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.suggest_color.setBackground(drawable);
            holder.suggest_deal.setText("未处理");
        }

        return convertView;
    }
    class  ViewHolder{
        TextView tiping_item_title;
        TextView tiping_item_content;
        RelativeLayout suggest_color;
        TextView suggest_deal;
        LinearLayout watermark;//水印
    }
}
