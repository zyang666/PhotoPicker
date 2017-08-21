package com.photopicker;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.photopicker.base.BaseActivity;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.UCropView;

/**
 * Created by zhangyang on 2017/8/4.
 * 裁剪界面
 */

public class CropActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_ERROR = "extra_error";
    public static final String EXTRA_RESULT_URI = "extra_result_uri";

    public static final int RESULT_ERROR = 10;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    public static final int DEFAULT_COMPRESS_QUALITY = 100;

    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;

    private UCropView mUcropView;
    private View mRotate;
    private View mCrop;
    private GestureCropImageView mCropImageView;
    private OverlayView mOverlayView;
    private Toolbar mToolBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);


        initView();
        processIntent();
        initData();
    }

    private void processIntent() {
        Intent intent = getIntent();
        if(intent != null){
            String compressionFormatName = intent.getStringExtra(Photo.CropOption.EXTRA_COMPRESSION_FORMAT_NAME);
            Bitmap.CompressFormat compressFormat = null;
            if (!TextUtils.isEmpty(compressionFormatName)) {
                compressFormat = Bitmap.CompressFormat.valueOf(compressionFormatName);
            }
            mCompressFormat = (compressFormat == null) ? DEFAULT_COMPRESS_FORMAT : compressFormat;
            mCompressQuality = intent.getIntExtra(Photo.CropOption.EXTRA_COMPRESSION_QUALITY, DEFAULT_COMPRESS_QUALITY);

            initCropImageView(intent);
            initOverlayView(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCropImageView != null) {
            mCropImageView.cancelAllAnimations();
        }
    }

    private void initView() {
        mRotate = findViewById(R.id.rotate);
        mCrop = findViewById(R.id.crop);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.tool_bar_cancel).setOnClickListener(this);
        mRotate.setOnClickListener(this);
        mCrop.setOnClickListener(this);

        mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mUcropView = (UCropView) findViewById(R.id.ucropView);
        mCropImageView = mUcropView.getCropImageView();
        mOverlayView = mUcropView.getOverlayView();
    }

    private void initOverlayView(Intent intent) {
        mOverlayView.setShowCropGrid(intent.getBooleanExtra(Photo.CropOption.EXTRA_SHOW_CROP_GRID,false));
        mOverlayView.setFreestyleCropEnabled(intent.getBooleanExtra(Photo.CropOption.EXTRA_FREESTYLE_ENABLED,false));
        mOverlayView.setCircleDimmedLayer(intent.getBooleanExtra(Photo.CropOption.EXTRA_CIRCLE_DIMMED_LAYER,false));
        mOverlayView.setCropFrameColor(intent.getIntExtra(Photo.CropOption.EXTRA_CROP_FRAME_COLOR,Color.WHITE));
    }

    private void initCropImageView(Intent intent) {
        mCropImageView.setMaxBitmapSize(CropImageView.DEFAULT_MAX_BITMAP_SIZE);
        mCropImageView.setMaxScaleMultiplier(CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER);
        mCropImageView.setImageToWrapCropBoundsAnimDuration(CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION);

        mCropImageView.setRotateEnabled(intent.getBooleanExtra(Photo.CropOption.EXTRA_ROTATE_ENABLED, false));
        mCropImageView.setScaleEnabled(intent.getBooleanExtra(Photo.CropOption.EXTRA_SCALE_ENABLED,true));
        float aspectRatioX = intent.getFloatExtra(Photo.CropOption.EXTRA_ASPECT_RATIO_X, 0);
        float aspectRatioY = intent.getFloatExtra(Photo.CropOption.EXTRA_ASPECT_RATIO_Y, 0);
        if (aspectRatioX > 0 && aspectRatioY > 0) {
            mCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
        }else {
            mCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        }
    }

    private void initData() {
        Uri inputUri = getIntent().getParcelableExtra(Photo.CropOption.EXTRA_INPUT_URI);
        Uri outputUri = getIntent().getParcelableExtra(Photo.CropOption.EXTRA_OUTPUT_URI);

        if (inputUri != null && outputUri != null) {
            try {
                mCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                setResultError(e);
                finish();
            }
        } else {
            setResultError(new NullPointerException(getString(com.yalantis.ucrop.R.string.ucrop_error_input_data_is_absent)));
            finish();
        }
    }

    protected void setResultError(Throwable throwable) {
        setResult(RESULT_ERROR, new Intent().putExtra(EXTRA_ERROR, throwable));
    }


    @Override
    protected void onResume() {
        super.onResume();
        mToolBar.measure(0,0);
        hideOrShowToolBar(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideOrShowToolBar(200);
            }
        },300);
    }

    /**
     * 动画隐藏或显示标题栏
     */
    private void hideOrShowToolBar(long duration){
        mToolBar.clearAnimation();
        float translationY = mToolBar.getTranslationY();
        float start = translationY;
        float end = translationY < 0 ? 0 :  -mToolBar.getMeasuredHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mToolBar, "translationY", start,end);
        animator.setDuration(duration);
        animator.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.cancel || id == R.id.tool_bar_cancel){
            finish();

        }else if(id == R.id.rotate){
            mCropImageView.postRotate(90);
            mCropImageView.setImageToWrapCropBounds();

        }else if(id == R.id.crop){
            showNoCancelLoading();
            mCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, new BitmapCropCallback() {
                @Override
                public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                    hideNoCancelLoading();
                    setResult(RESULT_OK,new Intent().putExtra(EXTRA_RESULT_URI,resultUri));
                    finish();
                }

                @Override
                public void onCropFailure(@NonNull Throwable t) {
                    hideNoCancelLoading();
                    setResultError(t);
                    finish();
                }
            });
        }
    }
}
