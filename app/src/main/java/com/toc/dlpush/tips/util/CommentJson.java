package com.toc.dlpush.tips.util;

import java.util.List;

/**
 * Created by 袁飞 on 2015/6/1.
 * DLpush
 */
public class CommentJson {
    List<Comment> commentList;
    String error;
    String tip;

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
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
        return "CommentJson{" +
                "commentList=" + commentList +
                ", error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
