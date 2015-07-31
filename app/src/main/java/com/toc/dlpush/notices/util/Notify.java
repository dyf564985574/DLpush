package com.toc.dlpush.notices.util;

import java.io.Serializable;

/**
 * Created by 袁飞 on 2015/5/24.
 * DLpush
 */
public class Notify implements Serializable {
    String id;
    String title;//标题
    String content;//内容
    String stoptime;//停电时间
    String uid;//用户的ID
    String info;//摘要
    String sendtime;
    String readnum;//已读人数
    String countnum;//收到消息总人数
    String notifystatus;//通知审核类型：wait待审核，yes通过，no驳回
    String notifytype;//计划停电任务通知  or  来电通知 ( stop  or  come ）
    String nid;//对来电任务来说对应的计划停电任务的ID

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStoptime() {
        return stoptime;
    }

    public void setStoptime(String stoptime) {
        this.stoptime = stoptime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
    }

    public String getReadnum() {
        return readnum;
    }

    public void setReadnum(String readnum) {
        this.readnum = readnum;
    }

    public String getCountnum() {
        return countnum;
    }

    public void setCountnum(String countnum) {
        this.countnum = countnum;
    }

    public String getNotifystatus() {
        return notifystatus;
    }

    public void setNotifystatus(String notifystatus) {
        this.notifystatus = notifystatus;
    }

    public String getNotifytype() {
        return notifytype;
    }

    public void setNotifytype(String notifytype) {
        this.notifytype = notifytype;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    @Override
    public String toString() {
        return "Notify{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", stoptime='" + stoptime + '\'' +
                ", uid='" + uid + '\'' +
                ", info='" + info + '\'' +
                ", sendtime='" + sendtime + '\'' +
                ", readnum='" + readnum + '\'' +
                ", countnum='" + countnum + '\'' +
                ", notifystatus='" + notifystatus + '\'' +
                ", notifytype='" + notifytype + '\'' +
                ", nid='" + nid + '\'' +
                '}';
    }
}
