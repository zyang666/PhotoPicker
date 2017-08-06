package com.photopicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by zhangyang on 2017/8/2.
 *
 * 展示相册列表
 */

public class PhotoListActivity extends AppCompatActivity{

    private static final String TAG = "PhotoListActivity";

    public static void start(Context context) {
        Intent intent = new Intent(context, PhotoListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_photo_list);
    }
}
