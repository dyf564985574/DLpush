package com.toc.dlpush.util;

import com.toc.dlpush.tips.util.Comment;

/**
 * Created by Yangweisi on 2015/6/8.
 */
public class CommentJsons {
    String error;
    String tip;
    Comment comment;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "CommentJsons{" +
                "error='" + error + '\'' +
                ", tip='" + tip + '\'' +
                ", comment=" + comment +
                '}';
    }
}
