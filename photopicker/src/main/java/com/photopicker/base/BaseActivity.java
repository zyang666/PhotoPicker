package com.photopicker.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.photopicker.R;
import com.photopicker.widget.LoadingDialog;
import com.photopicker.widget.SystemBarTintManager;

/**
 * Created by zy on 2017/8/6.
 *
 */

public class BaseActivity extends AppCompatActivity{

    private LoadingDialog mNoCancelLoading;
    public void showNoCancelLoading() {
        if(mNoCancelLoading == null) {
            mNoCancelLoading = new LoadingDialog(this);
            mNoCancelLoading.setCancelable(false);
            mNoCancelLoading.setCanceledOnTouchOutside(false);
            mNoCancelLoading.setBackPressedClickListener(new LoadingDialog.BackPressedClickListener() {
                @Override
                public void onBackPressed() {
                    finish();
                    mNoCancelLoading.dismiss();
                }
            });
        }
        mNoCancelLoading.show();
    }

    public void hideNoCancelLoading(){
        if(mNoCancelLoading != null && mNoCancelLoading.isShowing()){
            mNoCancelLoading.dismiss();
        }
    }
}
