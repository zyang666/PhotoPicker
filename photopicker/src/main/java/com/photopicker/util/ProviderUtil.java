package com.photopicker.util;

import android.content.Context;

/**
 * Created by zy on 2017/8/21.
 */

public class ProviderUtil {

    public static String getFileProviderName(Context context){
        return context.getPackageName()+".provider";
    }
}
