package com.toc.dlpush.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.toc.dlpush.R;
import com.toc.dlpush.caring.util.Caring;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by 袁飞 on 2015/5/25.
 * DLpush
 */
public class CaringAdapter extends BaseAdapter {
    List<Caring> carings;
    private Context context;
    private LayoutInflater inflater;
    private boolean flag = true;

    public CaringAdapter(Context context,List<Caring> list1){
        this.context=context;
        this.carings=list1;
        this.inflater=LayoutInflater.from(context);

    }
    public CaringAdapter(Context context,List<Caring> list1,boolean flag){
        this.context=context;
        this.carings=list1;
        this.inflater=LayoutInflater.from(context);
        this.flag = flag;

    }
    public void notifyDataSetChangedEx(List<Caring>  list01){

        if (list01 == null) {
            list01 = new ArrayList<Caring>();
        }
        this.carings = list01;

        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return this.carings==null? 0:carings.size();
    }

    @Override
    public Object getItem(int position) {
        return carings.get(position);
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
            convertView=inflater.inflate(R.layout.caring_item,null);
            holder.caring_time= (TextView) convertView.findViewById(R.id.caring_time);
            holder.caring_item_title= (TextView) convertView.findViewById(R.id.caring_item_title);
            holder.caring_item_content= (TextView) convertView.findViewById(R.id.caring_item_content);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        Caring caring=carings.get(position);
        Log.i("carings", carings.get(position).getCreatdate());
            Date nowDate = Calendar.getInstance().getTime();
            holder.caring_item_title.setText(caring.getTitle());
            holder.caring_item_content.setText(caring.getInfo());
        if(flag) {
            holder.caring_time.setText(twoDateDistance(caring, GetDate(caring.getCreatdate()), nowDate));
        }else{
            if(caring.getCreatdate().length()>10) {
                holder.caring_time.setText(caring.getCreatdate().substring(0, 10));
            }
        }
        return convertView;
    }
    class ViewHolder {
        TextView caring_time;
        TextView caring_item_title;//题目
        TextView caring_item_content;//内容
    }
    public  String twoDateDistance(Caring caring, Date startDate,Date endDate){

        if(startDate == null ||endDate == null){
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();

        if (timeLong/(1000*60*60*24)==0){
            timeLong = timeLong/60/60/1000;

            return caring.getCreatdate().substring(10,16);
        }
        else if (timeLong/(1000*60*60*24) == 1){
            return  "昨天";
        }
        else {
            return "以前";
        }
    }
    public  Date GetDate(String index){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        Date date= null;
        try {
            date = sdf.parse(index);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
