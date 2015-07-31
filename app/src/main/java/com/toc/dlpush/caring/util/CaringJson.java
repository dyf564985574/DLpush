package com.toc.dlpush.caring.util;

import java.util.List;

/**
 * Created by 袁飞 on 2015/5/27.
 * DLpush
 */
public class CaringJson {
    List<Caring> newslist;
    String error;
    String tip;

    public List<Caring> getNewslist() {
        return newslist;
    }

    public void setNewslist(List<Caring> newslist) {
        this.newslist = newslist;
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
        return "CaringJson{" +
                "newslist=" + newslist +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
