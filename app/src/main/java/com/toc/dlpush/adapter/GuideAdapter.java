package com.toc.dlpush.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toc.dlpush.R;
import com.toc.dlpush.util.GuideUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/7.
 */
public class GuideAdapter extends BaseAdapter {
    private List<GuideUtil> guideUtils=new ArrayList<GuideUtil>();
    private  Context context;
    private LayoutInflater inflater;

    public  GuideAdapter(Context context,List<GuideUtil> guideUtils){
        this.context=context;
        this.guideUtils=guideUtils;
        this.inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return this.guideUtils==null? 0:guideUtils.size();
    }

    @Override
    public Object getItem(int position) {
        return guideUtils.get(position);
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
            convertView=inflater.inflate(R.layout.guide_main,null);
            holder.img= (ImageView) convertView.findViewById(R.id.img);
            holder.text= (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
       holder.img.setImageResource(guideUtils.get(position).getImage());
       holder.text.setText(guideUtils.get(position).getText());
        return convertView;
    }
    class ViewHolder {
        ImageView img;
        TextView text;//题目
    }
}
