package com.photo.phototest;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zhangyang on 2017/6/30.
 */

public class FileUtil {

    public static final String APP_FILE_PROVIDER_AUTHORITY = "com.photo.phototest.fileprovider";

    /**
     * 获取一个适配7.0的共享文件，主要用于跨应用共享
     * @param context
     * @return
     */
    public static File getProviderFile(Context context){
        File file1 = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //存在SD卡
            file1 = new File(Environment.getExternalStorageDirectory() + "/Pictures/");
        } else {
            file1 = new File(context.getCacheDir()+"/Pictures/");
        }

        if(!file1.getParentFile().exists()){
            file1.mkdirs();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);//获取当前时间，进一步转化为字符串
        Date date = new Date();
        String str = format.format(date);
        String fileName = str + ".jpg";
        File file = new File(file1, fileName);
        return file;
    }


    /**
     * 获取一个适配7.0的共享文件的Uri
     * @param context
     * @param file
     * @return
     */
    public static Uri getProviderUri(Context context,File file){
        return FileProvider.getUriForFile(context,APP_FILE_PROVIDER_AUTHORITY,file);
    }
}
