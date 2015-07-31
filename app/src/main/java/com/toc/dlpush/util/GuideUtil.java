package com.toc.dlpush.util;

/**
 * Created by yuanfei on 2015/7/7.
 */
public class GuideUtil {
    int image;
    String text;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "GuideUtil{" +
                "imageview=" + image +
                ", text='" + text + '\'' +
                '}';
    }
}
