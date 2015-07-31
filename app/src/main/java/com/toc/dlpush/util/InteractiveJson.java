package com.toc.dlpush.util;

import com.toc.dlpush.tips.util.Comment;

import java.util.List;

/**
 * Created by yuanfei on 2015/7/25.
 * error，tip，tsl（投诉率0~1），ycl（已处理数），wcl（未处理数）,jsonvolist(可用客
 */
public class InteractiveJson {
    String error;
    String tip;
    String tsl;
    String ycl;
    String wcl;
    List<Comment> jsonvolist;

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

    public String getTsl() {
        return tsl;
    }

    public void setTsl(String tsl) {
        this.tsl = tsl;
    }

    public String getYcl() {
        return ycl;
    }

    public void setYcl(String ycl) {
        this.ycl = ycl;
    }

    public String getWcl() {
        return wcl;
    }

    public void setWcl(String wcl) {
        this.wcl = wcl;
    }

    public List<Comment> getJsonvolist() {
        return jsonvolist;
    }

    public void setJsonvolist(List<Comment> jsonvolist) {
        this.jsonvolist = jsonvolist;
    }

    @Override
    public String toString() {
        return "InteractiveJson{" +
                "error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                ", tsl='" + tsl + '\'' +
                ", ycl='" + ycl + '\'' +
                ", wcl='" + wcl + '\'' +
                ", jsonvolist=" + jsonvolist +
                '}';
    }
}
