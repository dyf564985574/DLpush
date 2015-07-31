package com.toc.dlpush.caring.util;

import java.io.Serializable;

/**
 * Created by 袁飞 on 2015/5/25.
 * DLpush
 */
public class Caring implements Serializable {
    String id;//主键
    String title;//标题
    String content;//贴士新闻内容
    String info;//摘要
    String creatdate;
    String uid;
    String newsstatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewsstatus() {
        return newsstatus;
    }

    public void setNewsstatus(String newsstatus) {
        this.newsstatus = newsstatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreatdate() {
        return creatdate;
    }

    public void setCreatdate(String creatdate) {
        this.creatdate = creatdate;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Caring{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", info='" + info + '\'' +
                ", creatdate='" + creatdate + '\'' +
                ", uid='" + uid + '\'' +
                ", newsstatus='" + newsstatus + '\'' +
                '}';
    }
}
