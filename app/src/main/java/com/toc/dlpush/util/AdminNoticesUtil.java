package com.toc.dlpush.util;

/**
 * Created by yuanfei on 2015/7/23.
 */
public class AdminNoticesUtil {
    String day;
    String num;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "AdminNoticesUtil{" +
                "day='" + day + '\'' +
                ", num='" + num + '\'' +
                '}';
    }
}
