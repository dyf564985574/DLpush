package com.toc.dlpush.util;

import com.toc.dlpush.caring.util.Caring;

import java.util.List;

/**
 * Created by yuanfei on 2015/7/24.
 */
public class AdminNewsUtil {
    private List<AdminNoticesUtil> daynumlist;
    private List<Caring> newslist;
    private String tip;
    private String error;
    private String today;

    public List<AdminNoticesUtil> getDaynumlist() {
        return daynumlist;
    }

    public void setDaynumlist(List<AdminNoticesUtil> daynumlist) {
        this.daynumlist = daynumlist;
    }

    public List<Caring> getNewslist() {
        return newslist;
    }

    public void setNewslist(List<Caring> newslist) {
        this.newslist = newslist;
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

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    @Override
    public String toString() {
        return "AdminNewsUtil{" +
                "daynumlist=" + daynumlist +
                ", newslist=" + newslist +
                ", tip='" + tip + '\'' +
                ", error='" + error + '\'' +
                ", today='" + today + '\'' +
                '}';
    }
}
