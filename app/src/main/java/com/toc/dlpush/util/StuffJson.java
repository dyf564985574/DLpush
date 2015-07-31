package com.toc.dlpush.util;

/**
 * Created by yuanfei on 2015/7/25.
 * error，tip，allnum（总客户数），online（在线人数），offline（离线人数）,islogin（登陆过人数），nologin（没登陆过人数）
 */
public class StuffJson {
    String error;
    String tip;
    String allnum;
    String online;
    String offline;
    String islogin;
    String nologin;

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

    public String getAllnum() {
        return allnum;
    }

    public void setAllnum(String allnum) {
        this.allnum = allnum;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getOffline() {
        return offline;
    }

    public void setOffline(String offline) {
        this.offline = offline;
    }

    public String getIslogin() {
        return islogin;
    }

    public void setIslogin(String islogin) {
        this.islogin = islogin;
    }

    public String getNologin() {
        return nologin;
    }

    public void setNologin(String nologin) {
        this.nologin = nologin;
    }

    @Override
    public String toString() {
        return "StuffUtil{" +
                "error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                ", allnum='" + allnum + '\'' +
                ", online='" + online + '\'' +
                ", offline='" + offline + '\'' +
                ", islogin='" + islogin + '\'' +
                ", nologin='" + nologin + '\'' +
                '}';
    }
}
