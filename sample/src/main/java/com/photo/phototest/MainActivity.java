package com.photo.phototest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.photopicker.Photo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private LinearLayout mCoantainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mCoantainer = (LinearLayout) findViewById(R.id.container);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Photo.createListOptin(new ImageLoader())
                        .setMaxSelectorCount(9)
                        .needCamera(true)
                        .needCrop(true)
                        .setCameraUri(FileUtil.getProviderUri(getBaseContext(),FileUtil.getProviderFile(getBaseContext())))
                        .setAutoCropEnable(true)
                        .start(MainActivity.this,REQUEST_CODE);


            }
        });


    }

    private static final String TAG = "MainActivity";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: data="+data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            ArrayList<String> paths = Photo.ListOption.getData(data);
            Log.d(TAG, "onActivityResult: paths="+paths.toString());
            mCoantainer.removeAllViews();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (String path : paths) {
                ImageView imageView = new ImageView(this);
                layoutParams.setMargins(0,10,0,0);
                imageView.setLayoutParams(layoutParams);
                Glide.with(imageView.getContext()).load(path).into(imageView);
                mCoantainer.addView(imageView);
            }
        }
    }
}
