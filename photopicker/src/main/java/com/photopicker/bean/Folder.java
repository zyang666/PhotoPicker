package com.photopicker.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyang on 2017/8/2.
 * 图片所属的文件夹
 */

public class Folder {

    //文件夹名字
    private String folderName;
    //文件夹中第一张图片
    private Images firstImg;
    //文件夹内的图片
    private List<Images> mImges;

    public void addImg(Images image){
        if(mImges == null){
            mImges = new ArrayList<>();
        }
        mImges.add(image);
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Images getFirstImg() {
        return firstImg;
    }

    public void setFirstImg(Images firstImg) {
        this.firstImg = firstImg;
    }

    public List<Images> getImges() {
        return mImges;
    }

    public void setImges(List<Images> imges) {
        this.mImges = imges;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "folderName='" + folderName + '\'' +
                ", firstImg=" + firstImg +
                ", mImges=" + mImges +
                '}';
    }
}