package com.photopicker.base;

import android.support.v7.app.AppCompatActivity;

import com.photopicker.widget.LoadingDialog;

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
