package com.toc.dlpush.dao;

import android.content.ContentValues;
import android.content.Context;

import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.util.AdminNoticesUtil;

import java.util.List;

/**
 * Created by yuanfei on 2015/7/29.
 */
public class ChangeNumDao extends BaseDao<AdminNoticesUtil> {
    public ChangeNumDao(Context context) {
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
            insert("changenotices", values);
        }
    }
}
