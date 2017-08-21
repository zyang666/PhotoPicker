package com.photopicker.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zy on 2017/8/21.
 */

public class FileUtil {

    public static File getCropFile(Context context,String suffix){
        File file = new File(context.getCacheDir() + "/PhotoCrop/");
        return createFile(file,"CROP_",suffix);
    }

    /**
     * 获取一个适配7.0的共享文件，主要用于跨应用共享
     * @param context
     * @return
     */
    public static File getProviderFile(Context context){
        File file1 = null;
        if (existSDCard()) {
            //存在SD卡
            file1 = new File(Environment.getExternalStorageDirectory() + "/Pictures/");
        } else {
            file1 = new File(context.getCacheDir()+"/Pictures/");
        }

        return createFile(file1,"IMG_",".jpg");
    }


    /**
     * 获取一个适配7.0的共享文件的Uri
     * @param context
     * @param file
     * @return
     */
    public static Uri getProviderUri(Context context,File file){
        return FileProvider.getUriForFile(context, ProviderUtil.getFileProviderName(context),file);
    }


    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }


    /**
     * 判断SDCard是否可用
     */
    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
