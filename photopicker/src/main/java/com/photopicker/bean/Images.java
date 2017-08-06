package com.photopicker.bean;

/**
 * Created by zy on 2017/8/6.
 * 图片
 */

public class Images {

    private boolean isSelector;
    //图片在数据库中的主键id
    private int id;
    //图片路径
    private String imgPath;
    //图片的缩略图路径
    private String thumbnail;
    //图片所属文件夹的名称
    private String folderName;

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Images{" +
                "id=" + id +
                ", imgPath='" + imgPath + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", folderName='" + folderName + '\'' +
                '}';
    }
}
