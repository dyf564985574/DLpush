package com.toc.dlpush.setting.util;

/**
 * Created by 袁飞 on 2015/5/23.
 * DLpush
 */
public class Return {
    String error;
    String tip;

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
        return "Return{" +
                "error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
