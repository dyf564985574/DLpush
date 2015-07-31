package com.toc.dlpush.notices.util;

import com.toc.dlpush.util.User;

import java.io.Serializable;

/**
 * Created by 袁飞 on 2015/5/22.
 * DLpush
 */
public class UserJsonVo implements Serializable {


    User user;
    String error;
    String tip;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "UserJsonVo{" +
                "user=" + user +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
