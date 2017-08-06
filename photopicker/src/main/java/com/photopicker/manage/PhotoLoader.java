package com.photopicker.manage;

import android.content.Context;
import android.graphics.Bitmap;

import com.photopicker.bean.Folder;
import com.photopicker.bean.Images;
import com.photopicker.bean.ThumbnailImg;
import com.photopicker.util.PhotoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zy on 2017/8/6.
 * 图片加载类
 */

public class PhotoLoader {

    public List<Folder> loadAllImages(Context context) {
        if (context == null) return null;
        List<Images> imageList = PhotoUtil.getAllImges(context);
        Map<Integer, ThumbnailImg> thumbnailsSet = PhotoUtil.getAllThumbnailsSet(context);

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
                tempFolder.put(folder.getFolderName(), folder);
            }
            folder.addImg(images);
        }

        List<Folder> folderList = new ArrayList<>();
        Folder folderAll = new Folder();
        folderAll.setFolderName("所有图片");
        folderAll.setImges(imageList);
        if (imageList.size() > 0) {
            folderAll.setFirstImg(imageList.get(0));
        }
        folderList.add(folderAll);

        for (String key : tempFolder.keySet()) {
            folderList.add(tempFolder.get(key));
        }
        return folderList;
    }

    public Bitmap loadThumbnail(String path) {
        return PhotoUtil.decodeFile(path, 150, 150);
    }
}
