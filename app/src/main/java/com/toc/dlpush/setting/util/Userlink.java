package com.toc.dlpush.setting.util;

/**
 * Created by 袁飞 on 2015/5/23.
 * DLpush
 */
public class Userlink {
    String id;
    String uid;
    String linkphone;
    String linkname;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLinkphone() {
        return linkphone;
    }

    public void setLinkphone(String linkphone) {
        this.linkphone = linkphone;
    }

    public String getLinkname() {
        return linkname;
    }

    public void setLinkname(String linkname) {
        this.linkname = linkname;
    }

    @Override
    public String toString() {
        return "Userlink{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", linkphone='" + linkphone + '\'' +
                ", linkname='" + linkname + '\'' +
                '}';
    }
}
