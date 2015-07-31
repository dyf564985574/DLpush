package com.toc.dlpush.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.toc.dlpush.ManagerActivity;
import com.toc.dlpush.R;
import com.toc.dlpush.caring.util.Caring;
import com.toc.dlpush.setting.util.Userlink;
import com.toc.dlpush.util.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/8.
 */
public class SearchAdapter extends BaseAdapter {
    Context context;
    List<User> userList =new ArrayList<User>();
    private LayoutInflater inflater;

    public SearchAdapter(Context context,List<User> userList){
        this.context=context;
        this.userList=userList;
        this.inflater=LayoutInflater.from(context);
    }
    public void notifyDataSetChangedEx(List<User>  list01){

        if (list01 == null) {
            list01 = new ArrayList<User>();
        }
        this.userList = list01;

        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return this.userList==null? 0:userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.manager_user_item,null);
            holder.manager_item_title= (TextView) convertView.findViewById(R.id.manager_item_title);
            holder.manager_item_content= (TextView) convertView.findViewById(R.id.manager_item_content);
            holder.manager_message= (ImageView) convertView.findViewById(R.id.manager_message);
            holder.manager_phone= (ImageView) convertView.findViewById(R.id.manager_phone);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        User user=new User();
        user=userList.get(position);
        holder.manager_item_title.setText(user.getCompanyname());
        holder.manager_item_content.setText(user.getCompanyaddress());
        holder.manager_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri smsToUri = Uri.parse("smsto:" + userList.get(position).getPhone());

                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);


                context.startActivity(intent);
            }
        });
        holder.manager_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userList.get(position).getPhone()));
                context.startActivity(intent);
            }
        });
        return convertView;
    }
    class ViewHolder {
        TextView manager_item_title;//公司名称
        TextView manager_item_content;//公司地址
        ImageView manager_message;//发短信
        ImageView manager_phone;//发短信
    }
}
