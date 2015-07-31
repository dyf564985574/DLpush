package com.toc.dlpush.util;

import android.content.ContentValues;
import android.content.Context;

import com.toc.dlpush.dao.BaseDao;
import com.toc.dlpush.tips.util.Comment;

import java.util.List;

/**
 * Created by yuanfei on 2015/6/24.
 */
public class ProposeDao extends BaseDao<Comment> {
    public ProposeDao(Context context) {
        super(context);
    }
    public void AddList(List<Comment> list){
        for (int i=0;i<list.size();i++) {
            Comment jsonVo=new Comment();
            jsonVo=list.get(i);
            ContentValues values = new ContentValues();
            values.put("id", jsonVo.getId());
            values.put("type", jsonVo.getType());
            values.put("content", jsonVo.getContent());
            values.put("uid", jsonVo.getUid());
            values.put("status", jsonVo.getStatus());
            values.put("companyname", jsonVo.getCompanyname());
            values.put("creatdate", jsonVo.getCreatdate());
            insert("propose", values);
        }
    }
}
