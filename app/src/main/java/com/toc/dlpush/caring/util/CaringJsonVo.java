package com.toc.dlpush.caring.util;

/**
 * Created by 袁飞 on 2015/5/27.
 * DLpush
 */
public class CaringJsonVo {
    Caring news;
    String error;
    String tip;

    public Caring getNews() {
        return news;
    }

    public void setNews(Caring news) {
        this.news = news;
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
        return "CaringJsonVo{" +
                "news=" + news +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
