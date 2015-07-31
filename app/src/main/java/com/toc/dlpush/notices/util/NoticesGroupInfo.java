package com.toc.dlpush.notices.util;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 袁飞 on 2015/5/22.
 * DLpush
 */
public class NoticesGroupInfo implements Serializable {
    List<NotifyJsonVo>  notifylist;
    String error;
    String tip;

    public List<NotifyJsonVo> getNotifylist() {
        return notifylist;
    }

    public void setNotifylist(List<NotifyJsonVo> notifylist) {
        this.notifylist = notifylist;
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
}
