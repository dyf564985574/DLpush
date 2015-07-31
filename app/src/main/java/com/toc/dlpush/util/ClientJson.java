package com.toc.dlpush.util;

import java.util.List;

/**
 * Created by yuanfei on 2015/7/9.
 */
public class ClientJson {
    List<User> manageruserlist;
    String error;
    String tip;

    public List<User> getManageruserlist() {
        return manageruserlist;
    }

    public void setManageruserlist(List<User> manageruserlist) {
        this.manageruserlist = manageruserlist;
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
        return "ClientJson{" +
                "manageruserlist=" + manageruserlist +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
