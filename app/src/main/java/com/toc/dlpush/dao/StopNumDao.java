package com.toc.dlpush.dao;

import android.content.ContentValues;
import android.content.Context;

import com.toc.dlpush.util.AdminNoticesUtil;

import java.util.List;

/**
 * Created by yuanfei on 2015/7/29.
 */
public class StopNumDao extends BaseDao<AdminNoticesUtil> {
    public StopNumDao(Context context) {
        super(context);
    }
    //向数据库添加联系人
    public  void Add_contact(List<AdminNoticesUtil> notifyJsonVos){
        for (int i=0;i<notifyJsonVos.size();i++) {
            AdminNoticesUtil jsonVo=new AdminNoticesUtil();
            jsonVo=notifyJsonVos.get(i);
            ContentValues values = new ContentValues();
            values.put("day", jsonVo.getDay());
            values.put("num", jsonVo.getNum());
            insert("stopnotices", values);
        }
    }
}
