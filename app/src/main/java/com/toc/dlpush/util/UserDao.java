package com.toc.dlpush.util;

import android.content.ContentValues;
import android.content.Context;

import com.toc.dlpush.caring.util.Caring;
import com.toc.dlpush.dao.BaseDao;

import java.util.List;

/**
 * Created by yuanfei on 2015/7/9.
 */
public class UserDao extends BaseDao<User> {
    public UserDao(Context context) {
        super(context);
    }
    public void AddList(List<User> list){
        for (int i=0;i<list.size();i++) {
            User jsonVo=new User();
            jsonVo=list.get(i);
            ContentValues values = new ContentValues();
            values.put("id", jsonVo.getId());
            values.put("ammeterid", jsonVo.getAmmeterid());
            values.put("linkman", jsonVo.getLinkman());
            values.put("passwd", jsonVo.getPasswd());
            values.put("roleid", jsonVo.getRoleid());
            values.put("companyname", jsonVo.getCompanyname());
            values.put("companyaddress", jsonVo.getCompanyaddress());
            values.put("phone", jsonVo.getPhone());
            values.put("isapp", jsonVo.getIsapp());
            values.put("line", jsonVo.getLine());
            values.put("companytype", jsonVo.getCompanytype());
            values.put("companyclassify", jsonVo.getCompanyclassify());
            values.put("managerid", jsonVo.getManagerid());
            insert("user", values);
        }
    }
}
