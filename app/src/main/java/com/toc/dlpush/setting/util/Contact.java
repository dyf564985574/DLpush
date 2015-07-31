package com.toc.dlpush.setting.util;

import java.util.List;

/**
 * Created by 袁飞 on 2015/5/23.
 * DLpush
 */
public class Contact {
    List<Userlink> linklist;
    String error;
    String tip;

    public List<Userlink> getLinklist() {
        return linklist;
    }

    public void setLinklist(List<Userlink> linklist) {
        this.linklist = linklist;
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
        return "Contact{" +
                "linklist=" + linklist +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
