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
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.UtilTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanfei on 2015/6/16.
 */
public class ProposeAdapter extends BaseAdapter {
    List<Comment> subminsList=new ArrayList<Comment>();
    Context context;
    private LayoutInflater inflater;
    Comment comment;


    public ProposeAdapter (Context context,List<Comment> subminsList){
        this.subminsList=subminsList;
        this.context=context;
        this.inflater=LayoutInflater.from(context);
    }
    public void notifyDataSetChangedEx(List<Comment>  list01){
        Log.i("position",list01+"");
        if (list01 == null) {
            list01 = new ArrayList<Comment>();
        }
        this.subminsList = list01;

        this.notifyDataSetChanged();
    }





    public int getCount() {
        return this.subminsList==null? 0:subminsList.size();
    }

    public Object getItem(int position) {
        return subminsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("position",position+"");
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.propose_item,null);
            holder.propose_item_content= (TextView) convertView.findViewById(R.id.propose_item_content);
            holder.propose_item_title= (TextView) convertView.findViewById(R.id.propose_item_title);
            holder.propose_color= (RelativeLayout) convertView.findViewById(R.id.propose_color);
            holder.propose_deal= (TextView) convertView.findViewById(R.id.propose_deal);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        //获取相对于位置的对象
        comment=subminsList.get(position);
        if(UtilTool.Notnull(comment.getContent()).length()<=15){
            holder.propose_item_title.setText(comment.getContent());
        }else{
            holder.propose_item_title.setText(comment.getContent().substring(0,14)+"...");
        }
            holder.propose_item_content.setText(comment.getCreatdate());
        if("0".equals(comment.getStatus())){
            Drawable drawable= context.getResources().getDrawable(R.drawable.processed);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.propose_color.setBackground(drawable);
            holder.propose_deal.setText("已处理");
        }else{
            Drawable drawable= context.getResources().getDrawable(R.drawable.untreated);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.propose_color.setBackground(drawable);
            holder.propose_deal.setText("未处理");
        }

        return convertView;
    }
    class  ViewHolder{
        TextView propose_item_title;
        TextView propose_item_content;
        RelativeLayout propose_color;
        TextView propose_deal;
    }
}
