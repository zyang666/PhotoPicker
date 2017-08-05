package com.photopicker.bean;

/**
 * Created by zhangyang on 2017/8/2.
 * 缩略图
 */

public class ThumbnailImg {

    //缩率图的id
    private int id;
    //缩略图路径
    private String path;
    //缩略图所对应图片的 id
    private int imageId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    @Override
    public String toString() {
        return "ThumbnailImg{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", imageId=" + imageId +
                '}';
    }
}
