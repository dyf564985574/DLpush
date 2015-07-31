package com.toc.dlpush.util;

import java.util.List;

/**
 * Created by yuanfei on 2015/7/17.
 */
public class UserJson {
    List<User> userlist;
    String error;
    String tip;

    public List<User> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<User> userlist) {
        this.userlist = userlist;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "UserJson{" +
                "userlist=" + userlist +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
