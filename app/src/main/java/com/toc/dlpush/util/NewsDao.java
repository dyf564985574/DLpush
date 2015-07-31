package com.toc.dlpush.util;

import android.content.ContentValues;
import android.content.Context;

import com.toc.dlpush.caring.util.Caring;
import com.toc.dlpush.dao.BaseDao;
import com.toc.dlpush.notices.util.NotifyJsonVo;

import java.util.List;

/**
 * Created by yuanfei on 2015/6/24.
 */
public class NewsDao extends BaseDao<Caring> {
    public NewsDao(Context context) {
        super(context);
    }
    public void AddList(List<Caring> list){
        for (int i=0;i<list.size();i++) {
            Caring jsonVo=new Caring();
            jsonVo=list.get(i);
            ContentValues values = new ContentValues();
            values.put("id", jsonVo.getId());
            values.put("title", jsonVo.getTitle());
            values.put("content", jsonVo.getContent());
            values.put("uid", jsonVo.getUid());
            values.put("info", jsonVo.getInfo());
            values.put("newsstatus", jsonVo.getNewsstatus());
            values.put("creatdate", jsonVo.getCreatdate());
            insert("news", values);
        }
    }
}
