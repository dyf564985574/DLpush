package com.toc.dlpush.util;

import android.content.ContentValues;
import android.content.Context;

import com.toc.dlpush.dao.BaseDao;
import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.setting.util.Userlink;

import java.util.List;

/**
 * Created by yuanfei on 2015/6/24.
 */
public class NoticesDao extends BaseDao<NotifyJsonVo> {
    public NoticesDao(Context context) {
        super(context);
    }
    //向数据库添加联系人
    public  void Add_contact(List<NotifyJsonVo> notifyJsonVos){
        for (int i=0;i<notifyJsonVos.size();i++) {
            NotifyJsonVo jsonVo=new NotifyJsonVo();
            jsonVo=notifyJsonVos.get(i);
            ContentValues values = new ContentValues();
            values.put("id", jsonVo.getId());
            values.put("title", jsonVo.getTitle());
            values.put("content", jsonVo.getContent());
            values.put("stoptime", jsonVo.getStoptime());
            values.put("uid", jsonVo.getUid());
            values.put("info", jsonVo.getInfo());
            values.put("sendtime", jsonVo.getSendtime());
            values.put("readnum", jsonVo.getReadnum());
            values.put("countnum", jsonVo.getCountnum());
            values.put("notifystatus", jsonVo.getNotifystatus());
            values.put("notifytype", jsonVo.getNotifytype());
            values.put("nid", jsonVo.getNid());
            values.put("isread", jsonVo.getIsread());
            insert("notices", values);
        }
    }
}
