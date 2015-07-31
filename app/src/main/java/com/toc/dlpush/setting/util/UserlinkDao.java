package com.toc.dlpush.setting.util;

import android.content.Context;
import android.database.Cursor;

import com.toc.dlpush.dao.BaseDao;
import com.toc.dlpush.notices.util.UserJsonVo;

import java.util.ArrayList;

/**
 * Created by 袁飞 on 2015/5/23.
 * DLpush
 */
public class UserlinkDao extends BaseDao<Userlink> {

    public UserlinkDao(Context context) {
        super(context);
    }
    public ArrayList<Userlink> getData(){
        Cursor mCursor = query("user_link", new String[] { "*" }, "", null);
        ArrayList<Userlink> notices = null;
        if (mCursor != null) {
            notices = new ArrayList<Userlink>();
            while (mCursor.moveToNext()) {
                Userlink no = new Userlink();
                String id = mCursor.getString(mCursor.getColumnIndex("id"));
                String linkphone = mCursor.getString(mCursor.getColumnIndex("linkphone"));
                String uid = mCursor.getString(mCursor.getColumnIndex("uid"));
                String linkname = mCursor.getString(mCursor.getColumnIndex("linkname"));
                // 按照这个方法，把各个字段取出，然后set到no
                no.setId(id);
                no.setLinkphone(linkphone);
                no.setUid(uid);
                no.setLinkname(linkname);
                notices.add(no);
            }
        }
        return notices;
    }

}
