package com.toc.dlpush.notices.util;

/**
 * Created by 袁飞 on 2015/5/24.
 * DLpush
 */
public class NotifyGroup {
    Notify notify;
    String error;
    String tip;

    public Notify getNotify() {
        return notify;
    }

    public void setNotify(Notify notify) {
        this.notify = notify;
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
        return "NotifyGroup{" +
                "notify=" + notify +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
