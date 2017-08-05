package com.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;


/**
 * Created by zhangyang on 2017/8/4.
 *
 */

public class Photo {

    public static CropOption createCropOption(@NonNull Uri inputUri, @NonNull Uri outputUri){
        return new CropOption(inputUri,outputUri);
    }

    public static ListOption createListOptin(){
        return new ListOption();
    }

    public static BigImgOptin createBigImgOptin(){
        return new BigImgOptin();
    }


    public static class ListOption{


    }

    public static class BigImgOptin{


    }



    /**
     * 裁剪操作类，更强大的裁剪请查看{@link com.yalantis.ucrop.UCrop},{@link com.yalantis.ucrop.UCropActivity}
     */
    public static class CropOption{

        public static final String EXTRA_INPUT_URI = "extra_input_uri";
        public static final String EXTRA_OUTPUT_URI = "extra_output_uri";
        public static final String EXTRA_COMPRESSION_FORMAT_NAME = "extra_compression_format_name";
        public static final String EXTRA_COMPRESSION_QUALITY = "extra_compression_quality";
        public static final String EXTRA_ASPECT_RATIO_X = "extra_aspect_ratio_x";
        public static final String EXTRA_ASPECT_RATIO_Y = "extra_aspect_ratio_y";
        public static final String EXTRA_ROTATE_ENABLED = "extra_rotate_enabled";
        public static final String EXTRA_SCALE_ENABLED = "extra_scale_enabled";
        public static final String EXTRA_SHOW_CROP_GRID = "extra_show_crop_grid";
        public static final String EXTRA_FREESTYLE_ENABLED = "extra_freestyle_enabled";
        public static final String EXTRA_CIRCLE_DIMMED_LAYER = "extra_circlr_dimmed_layer";
        public static final String EXTRA_CROP_FRAME_COLOR = "extra_dimmed_color";


        private Intent mIntent;

        public CropOption(@NonNull Uri inputUri, @NonNull Uri outputUri){
            mIntent = new Intent();
            mIntent.putExtra(EXTRA_INPUT_URI,inputUri);
            mIntent.putExtra(EXTRA_OUTPUT_URI,outputUri);
        }

        /**
         * 设置裁剪框的颜色
         * @param color
         * @return
         */
        public CropOption setCropFrameColor(@ColorInt int color){
            mIntent.putExtra(EXTRA_CROP_FRAME_COLOR,color);
            return this;
        }

        /**
         * 显示圆形裁剪框
         * @param circleDimmedLayer
         * @return
         */
        public CropOption setCircleDimmedLayer(boolean circleDimmedLayer){
            mIntent.putExtra(EXTRA_CIRCLE_DIMMED_LAYER,circleDimmedLayer);
            return this;
        }

        /**
         * 是否可自由调整裁剪框大小
         * @param freestyleCropEnabled
         * @return
         */
        public CropOption setFreestyleCropEnabled(boolean freestyleCropEnabled){
            mIntent.putExtra(EXTRA_FREESTYLE_ENABLED,freestyleCropEnabled);
            return this;
        }

        /**
         * 是否显示裁剪框的网格
         * @param isShow
         * @return
         */
        public CropOption setShowCropGrid(boolean isShow){
            mIntent.putExtra(EXTRA_SHOW_CROP_GRID,isShow);
            return this;
        }

        /**
         * 是否支持旋转
         * @param enabled
         * @return
         */
        public CropOption setRotateEnabled(boolean enabled){
            mIntent.putExtra(EXTRA_ROTATE_ENABLED,enabled);
            return this;
        }

        /**
         * 是否支持缩放
         * @param enabled
         * @return
         */
        public CropOption setScaleEnabled(boolean enabled){
            mIntent.putExtra(EXTRA_SCALE_ENABLED,enabled);
            return this;
        }

        /**
         * 设置裁剪之后的图片的格式
         * @param format
         * @return
         */
        public CropOption setCompressionFormat(@NonNull Bitmap.CompressFormat format) {
            mIntent.putExtra(EXTRA_COMPRESSION_FORMAT_NAME, format.name());
            return this;
        }

        /**
         * 设置裁剪之后的图片的质量
         * @param compressQuality
         * @return
         */
        public CropOption setCompressionQuality(@IntRange(from = 0) int compressQuality) {
            mIntent.putExtra(EXTRA_COMPRESSION_QUALITY, compressQuality);
            return this;
        }

        /**
         * 设置裁剪框的宽高比例
         * @param x
         * @param y
         * @return
         */
        public CropOption setAspectRatio(float x, float y) {
            mIntent.putExtra(EXTRA_ASPECT_RATIO_X, x);
            mIntent.putExtra(EXTRA_ASPECT_RATIO_Y, y);
            return this;
        }

        /**
         * 获取裁剪成功的结果
         * @param intent
         * @return
         */
        public static Uri getResult(Intent intent){
            if(intent == null) return null;
            return intent.getParcelableExtra(CropActivity.EXTRA_RESULT_URI);
        }

        /**
         * 获取裁剪失败的结果
         * @param intent
         * @return
         */
        public static Throwable getError(Intent intent){
            return (Throwable) intent.getSerializableExtra(CropActivity.EXTRA_ERROR);
        }


        public void start(Activity activity, int requestCode){
            activity.startActivityForResult(getIntent(activity),requestCode);
        }

        public void start(Fragment fragment, int requestCode){
            fragment.startActivityForResult(getIntent(fragment.getContext()),requestCode);
        }

        public void start(Context context){
            context.startActivity(getIntent(context));
        }

        private Intent getIntent(Context context) {
            mIntent.setClass(context,CropActivity.class);
            return mIntent;
        }
    }
}
