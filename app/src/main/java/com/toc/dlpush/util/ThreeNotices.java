package com.toc.dlpush.util;

import java.util.List;

/**
 * Created by yuanfei on 2015/7/23.
 */
public class ThreeNotices {
    List<AdminNoticesUtil> templist;
    List<AdminNoticesUtil> changelist;
    List<AdminNoticesUtil> stoplist;
    String error;
    String tip;

    public List<AdminNoticesUtil> getTemplist() {
        return templist;
    }

    public void setTemplist(List<AdminNoticesUtil> templist) {
        this.templist = templist;
    }

    public List<AdminNoticesUtil> getChangelist() {
        return changelist;
    }

    public void setChangelist(List<AdminNoticesUtil> changelist) {
        this.changelist = changelist;
    }

    public List<AdminNoticesUtil> getStoplist() {
        return stoplist;
    }

    public void setStoplist(List<AdminNoticesUtil> stoplist) {
        this.stoplist = stoplist;
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
        return "ThreeNotices{" +
                "templist=" + templist +
                ", changelist=" + changelist +
                ", stoplist=" + stoplist +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
