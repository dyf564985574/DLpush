package com.toc.dlpush.util;

/**
 * Created by yuanfei on 2015/7/9.
 */
public class PhoneJson {
    String managerphone;
    String error;
    String tip;

    public String getManagerphone() {
        return managerphone;
    }

    public void setManagerphone(String managerphone) {
        this.managerphone = managerphone;
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
        return "PhoneJson{" +
                "managerphone='" + managerphone + '\'' +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
