package com.toc.dlpush.tips.util;

/**
 * Created by 袁飞 on 2015/5/27.
 * DLpush
 */
public class submins {
    String id;
    String uid;
    String content;//反馈内容
    String creatdate;
    String type;//类型，意见或者反馈，（comment  or  feedback）

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

    @Override
    public String toString() {
        return "submins{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", content='" + content + '\'' +
                ", creatdate='" + creatdate + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
