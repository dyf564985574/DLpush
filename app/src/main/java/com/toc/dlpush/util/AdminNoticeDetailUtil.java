package com.toc.dlpush.util;

import com.toc.dlpush.notices.util.NotifyJsonVo;

import java.util.List;

/**
 * Created by yuanfei on 2015/7/24.
 */
public class AdminNoticeDetailUtil {
    private  List<AdminNoticesUtil> daynumlist;
    private List<NotifyJsonVo>  notifylist;
    private String error;
    private String tip;
    private String today;

    public List<AdminNoticesUtil> getDaynumlist() {
        return daynumlist;
    }

    public void setDaynumlist(List<AdminNoticesUtil> daynumlist) {
        this.daynumlist = daynumlist;
    }

    public List<NotifyJsonVo> getNotifylist() {
        return notifylist;
    }

    public void setNotifylist(List<NotifyJsonVo> notifylist) {
        this.notifylist = notifylist;
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

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    @Override
    public String toString() {
        return "AdminNoticeDetailUtil{" +
                "daynumlist=" + daynumlist +
                ", notifylist=" + notifylist +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                ", today='" + today + '\'' +
                '}';
    }
}
