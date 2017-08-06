package com.photopicker.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import com.photopicker.R;

public class LoadingDialog extends AppCompatDialog {

    private BackPressedClickListener mBackPressedClickListener;

    public LoadingDialog(Context context) {
        this(context, R.style.LoadingDialog);
    }

    private LoadingDialog(Context context, int theme) {
        super(context, theme);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(R.layout.dialog_loading);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mBackPressedClickListener != null){
            mBackPressedClickListener.onBackPressed();
        }
    }

    public void setBackPressedClickListener(BackPressedClickListener listener){
        mBackPressedClickListener = listener;
    }
    public interface BackPressedClickListener{
        void onBackPressed();
    }
}
