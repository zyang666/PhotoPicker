package com.photopicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.photopicker.manage.PhotoManager;

/**
 * Created by zhangyang on 2017/8/2.
 *
 * 展示相册列表
 */

public class PhotoListActivity extends AppCompatActivity{

    private PhotoListFragment mPhotoListFragment;

    public static void start(Context context) {
        Intent intent = new Intent(context, PhotoListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_photo_list);
        mPhotoListFragment = new PhotoListFragment();
        mPhotoListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_container, mPhotoListFragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoManager.release();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(mPhotoListFragment != null){
            mPhotoListFragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}
