package com.photopicker.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

/**
 * Created by zy on 2017/8/6.
 *
 */

public class PermissionsUtil {

    public static boolean checkPermissions(Activity context, String permissions,int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(context, permissions)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{permissions}, requestCode);
        }else {
            return true;
        }
        return false;
    }

}
