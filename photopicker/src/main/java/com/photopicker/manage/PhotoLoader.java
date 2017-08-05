package com.photopicker.manage;

import android.content.Context;
import android.util.Log;

import com.photopicker.bean.Folder;
import com.photopicker.bean.Images;
import com.photopicker.bean.ThumbnailImg;
import com.photopicker.util.ImgUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyang on 2017/8/2.
 * 图片加载类
 */

public class PhotoLoader {

    public List<Folder> loadAllImages(Context context) {
        if(context == null) return null;

        List<Images> imageList = ImgUtil.getAllImges(context);
        Map<Integer, ThumbnailImg> thumbnailsSet = ImgUtil.getAllThumbnailsSet(context);
        if (imageList == null || thumbnailsSet == null) {
            return null;
        }

        //将获得的图片按文件夹分类出来
        Map<String, Folder> tempFolder = new HashMap<>();
        int size = imageList.size();
        for (int i = 0; i < size; i++) {
            Images images = imageList.get(i);
            ThumbnailImg thumbnailImg = thumbnailsSet.get(images.getId());
            if (thumbnailImg != null) {
                images.setThumbnail(thumbnailImg.getPath());
            }

            Folder folder = tempFolder.get(images.getFolderName());
            if (folder == null) {
                folder = new Folder();
                folder.setFolderName(images.getFolderName());
                folder.setFirstImg(images);
                tempFolder.put(folder.getFolderName(),folder);
            }
            folder.addImg(images);
        }

        List<Folder> folderList = new ArrayList<>();
        Folder folderAll = new Folder();
        folderAll.setFolderName("所有图片");
        folderAll.setImges(imageList);
        if(imageList.size() > 0){
            folderAll.setFirstImg(imageList.get(0));
        }
        folderList.add(folderAll);

        for (String key : tempFolder.keySet()) {
            folderList.add(tempFolder.get(key));
        }
        return folderList;
    }

}
