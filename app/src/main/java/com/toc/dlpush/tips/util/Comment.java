package com.toc.dlpush.tips.util;

import java.io.Serializable;

/**
 * Created by 袁飞 on 2015/6/1.
 * DLpush
 */
public class Comment implements Serializable {
    String id;
    String uid;
    String content;//反馈内容
    String creatdate;
    String type;//类型，意见或者反馈，（comment  or  feedback）
    String companyname;//意见所属公司的名称
    String status;//0 已处理   1代表未处理
    String note;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatdate() {
        return creatdate;
    }

    public void setCreatdate(String creatdate) {
        this.creatdate = creatdate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", content='" + content + '\'' +
                ", creatdate='" + creatdate + '\'' +
                ", type='" + type + '\'' +
                ", companyname='" + companyname + '\'' +
                ", status='" + status + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
