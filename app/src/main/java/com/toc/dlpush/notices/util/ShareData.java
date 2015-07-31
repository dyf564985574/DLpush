package com.toc.dlpush.notices.util;

/**
 * Created by 袁飞 on 2015/5/27.
 * DLpush
 */
public class ShareData {
    String error;
    String tip;
    String sharenum;//分享次数

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

    public String getSharenum() {
        return sharenum;
    }

    public void setSharenum(String sharenum) {
        this.sharenum = sharenum;
    }

    @Override
    public String toString() {
        return "share{" +
                "error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                ", sharenum='" + sharenum + '\'' +
                '}';
    }
}
