package com.toc.dlpush.setting.util;

/**
 * Created by Yangweisi on 2015/6/6.
 */
public class Updatas {
    String error;
    String tip;
    String version;
    String path;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Updatas{" +
                "error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                ", version='" + version + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
