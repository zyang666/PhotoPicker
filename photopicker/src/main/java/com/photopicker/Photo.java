package com.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.ArrayList;


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

    public static BigImgOptin createBigImgOptin(@NonNull ArrayList<String> imgPath){
        return new BigImgOptin(imgPath);
    }

    @IntDef({CROP_MODE_SQUARE,CROP_MODE_CIRCLE})
    public @interface CropMode{}

    /**
     * 方形裁剪
     */
    public static final int CROP_MODE_SQUARE = 10;

    /**
     * 圆形裁剪
     */
    public static final int CROP_MODE_CIRCLE = 12;

    public static class ListOption{

        public static final String EXTRA_CROP_OPTION_BUNDLE = "extra_crop_option_bundle";
        public static final String EXTRA_BIG_IMG_BUNDLE = "extra_big_img_bundle";
        public static final String EXTRA_MAX_SELECTOR_COUNT = "extra_max_selector_count";
        public static final String EXTRA_NEED_CROP = "extra_need_crop";
        public static final String EXTRA_NEED_CAMERA = "extra_need_camera";
        public static final String EXTRA_CAMERA_URI = "extra_camera_uei";

        private Intent mListIntent;
        private Bundle mListBundle;

        public ListOption(){
            mListIntent = new Intent();
            mListBundle = new Bundle();
        }

        /**
         * 是否需要显示照相机,如果需要照相机的item，请务必调用{@link #setCameraUri(Uri)}设置照相机拍完照保存的相片路径
         * @return
         */
        public ListOption needCamera(boolean needCamera){
            mListBundle.putBoolean(EXTRA_NEED_CAMERA,needCamera);
            return this;
        }

        /**
         * 设置相机拍照保存相片的uri
         * @param saveUri
         * @return
         */
        public ListOption setCameraUri(@NonNull Uri saveUri){
            mListBundle.putParcelable(EXTRA_CAMERA_URI,saveUri);
            return this;
        }

        /**
         * 是否需要裁剪
         * @return
         */
        public ListOption needCrop(boolean needCrop){
            mListBundle.putBoolean(EXTRA_NEED_CROP,needCrop);
            return this;
        }

        /**
         * 设置裁剪的模式 {@link #CROP_MODE_CIRCLE},{@link #CROP_MODE_SQUARE}
         * @param cropMode
         * @return
         */
        public ListOption setCropMode(@CropMode int cropMode){
            Bundle cropBundle = mListBundle.getParcelable(EXTRA_CROP_OPTION_BUNDLE);
            if(cropBundle == null){
                cropBundle = new Bundle();
            }
            mListBundle.putParcelable(EXTRA_CROP_OPTION_BUNDLE,cropBundle);
            return this;
        }

        /**
         * 设置最大选择数量
         * @param maxSelectorCount
         * @return
         */
        public ListOption setMaxSelectorCount(int maxSelectorCount){
            mListBundle.putInt(EXTRA_MAX_SELECTOR_COUNT,maxSelectorCount);
            return this;
        }

        /**
         * 设置一个大图的option
         * @param bigImgOption
         * @return
         */
        public ListOption withBigImgOption(@NonNull BigImgOptin bigImgOption){
            Bundle bigImgBundle = bigImgOption.getBigImgBundle();
            Bundle bundle = mListBundle.getParcelable(EXTRA_BIG_IMG_BUNDLE);
            if(bundle == null) {
                mListBundle.putParcelable(EXTRA_BIG_IMG_BUNDLE, bigImgBundle);
            }else {
                bundle.putAll(bigImgBundle);
                mListBundle.putParcelable(EXTRA_BIG_IMG_BUNDLE, bundle);
            }
            return this;
        }

        /**
         * 设置一个裁剪的option
         * @param cropOption
         * @return
         */
        public ListOption withCropOption(@NonNull CropOption cropOption){
            Bundle bundle = cropOption.getBundle();
            Bundle cropBundle = mListBundle.getParcelable(EXTRA_CROP_OPTION_BUNDLE);
            if(cropBundle == null) {
                mListBundle.putParcelable(EXTRA_CROP_OPTION_BUNDLE, bundle);
            }else {
                cropBundle.putAll(bundle);
                mListBundle.putParcelable(EXTRA_CROP_OPTION_BUNDLE, cropBundle);
            }
            return this;
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
            boolean needCamera = mListBundle.getBoolean(EXTRA_NEED_CAMERA);
            if(needCamera){
                Uri cameraUri =mListBundle.getParcelable(EXTRA_CAMERA_URI);
                if(cameraUri == null){
                    throw new NullPointerException("camera_uri不能为空，请调用setCameraUri(Uri saveUri)设置相机uri");
                }
            }
            mListIntent.setClass(context,PhotoListActivity.class);
            mListIntent.putExtras(mListBundle);
            return mListIntent;
        }
    }


    @IntDef({MODE_OPTION,MODE_CHECK})
    public @interface Mode{
    }
    /**
     * 操作模式，可启用大图的裁剪、选择功能
     * */
    public static final int MODE_OPTION = 1;

    /**查
     * 看模式，可启用大图的删除功能
     * */
    public static final int MODE_CHECK = 2;

    public static class BigImgOptin{

        public static final String EXTRA_CROP_OPTION_BUNDLE = "extra_crop_option_bundle";
        public static final String EXTRA_BIG_IMG_PATHS = "extra_big_img_paths";
        public static final String EXTRA_MODE = "extra_mode";
        public static final String EXTRA_CAN_DELETE = "extra_can_delete";
        public static final String EXTRA_NEED_CROP = "extra_need_crop";
        public static final String EXTRA_MAX_SELECTOR_COUNT = "extra_max_selector_count";


        private Bundle mBigImgBundle;
        private Intent mBigImgIntent;

        public BigImgOptin(@NonNull ArrayList<String> imgPath){
            mBigImgIntent = new Intent();
            mBigImgBundle = new Bundle();
            mBigImgBundle.putStringArrayList(EXTRA_BIG_IMG_PATHS,imgPath);
        }

        public Bundle getBigImgBundle(){
            return mBigImgBundle;
        }

        /**
         * 设置最大选择数量，只要模式为{@link #MODE_OPTION}才有效
         */
        public BigImgOptin setMaxSelectorCount(int maxSelectorCount){
            mBigImgBundle.putInt(EXTRA_MAX_SELECTOR_COUNT,maxSelectorCount);
            return this;
        }


        /**
         * 是否可编辑(裁剪)，只要模式为{@link #MODE_OPTION}才有效
         */
        public BigImgOptin canCrop(boolean needCrop){
            mBigImgBundle.putBoolean(EXTRA_NEED_CROP,needCrop);
            return this;
        }

        /**
         * 设置裁剪的模式 {@link #CROP_MODE_CIRCLE},{@link #CROP_MODE_SQUARE}
         * @param cropMode
         * @return
         */
        public BigImgOptin setCropMode(@CropMode int cropMode){
            Bundle cropBundle = mBigImgBundle.getParcelable(EXTRA_CROP_OPTION_BUNDLE);
            if(cropBundle == null){
                cropBundle = new Bundle();
            }
            mBigImgBundle.putParcelable(EXTRA_CROP_OPTION_BUNDLE,cropBundle);
            return this;
        }

        /**
         * 是否可删除，只要模式为{@link #MODE_CHECK}才有效
         */
        public BigImgOptin canDelete(boolean canDelete){
            mBigImgBundle.putBoolean(EXTRA_CAN_DELETE,canDelete);
            return this;
        }

        /**
         * 设置查看大图的模式
         * @param mode
         * @return
         */
        public BigImgOptin setMode(@Mode int mode){
            mBigImgBundle.putInt(EXTRA_MODE,mode);
            return this;
        }

        /**
         * 设置一个裁剪的option
         * @param cropOption
         * @return
         */
        public BigImgOptin withCropOption(@NonNull CropOption cropOption){
            Bundle bundle = cropOption.getBundle();
            Bundle cropBundle = mBigImgBundle.getParcelable(EXTRA_CROP_OPTION_BUNDLE);
            if(cropBundle == null) {
                mBigImgBundle.putParcelable(EXTRA_CROP_OPTION_BUNDLE, bundle);
            }else {
                cropBundle.putAll(bundle);
                mBigImgBundle.putParcelable(EXTRA_CROP_OPTION_BUNDLE,cropBundle);
            }
            return this;
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
            mBigImgIntent.setClass(context,ShowImageActivity.class);
            mBigImgIntent.putExtras(mBigImgBundle);
            return mBigImgIntent;
        }
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


        private Intent mCropIntent;
        private Bundle mCropBundle;

        public CropOption(@NonNull Uri inputUri, @NonNull Uri outputUri){
            mCropIntent = new Intent();
            mCropBundle = new Bundle();
            mCropBundle.putParcelable(EXTRA_INPUT_URI,inputUri);
            mCropBundle.putParcelable(EXTRA_OUTPUT_URI,outputUri);
        }

        public Bundle getBundle(){
            return mCropBundle;
        }

        /**
         * 设置裁剪框的颜色
         * @param color
         * @return
         */
        public CropOption setCropFrameColor(@ColorInt int color){
            mCropBundle.putInt(EXTRA_CROP_FRAME_COLOR,color);
            return this;
        }

        /**
         * 显示圆形裁剪框
         * @param circleDimmedLayer
         * @return
         */
        public CropOption setCircleDimmedLayer(boolean circleDimmedLayer){
            mCropBundle.putBoolean(EXTRA_CIRCLE_DIMMED_LAYER,circleDimmedLayer);
            return this;
        }

        /**
         * 是否可自由调整裁剪框大小
         * @param freestyleCropEnabled
         * @return
         */
        public CropOption setFreestyleCropEnabled(boolean freestyleCropEnabled){
            mCropBundle.putBoolean(EXTRA_FREESTYLE_ENABLED,freestyleCropEnabled);
            return this;
        }

        /**
         * 是否显示裁剪框的网格
         * @param isShow
         * @return
         */
        public CropOption setShowCropGrid(boolean isShow){
            mCropBundle.putBoolean(EXTRA_SHOW_CROP_GRID,isShow);
            return this;
        }

        /**
         * 是否支持旋转
         * @param enabled
         * @return
         */
        public CropOption setRotateEnabled(boolean enabled){
            mCropBundle.putBoolean(EXTRA_ROTATE_ENABLED,enabled);
            return this;
        }

        /**
         * 是否支持缩放
         * @param enabled
         * @return
         */
        public CropOption setScaleEnabled(boolean enabled){
            mCropBundle.putBoolean(EXTRA_SCALE_ENABLED,enabled);
            return this;
        }

        /**
         * 设置裁剪之后的图片的格式
         * @param format
         * @return
         */
        public CropOption setCompressionFormat(@NonNull Bitmap.CompressFormat format) {
            mCropBundle.putString(EXTRA_COMPRESSION_FORMAT_NAME, format.name());
            return this;
        }

        /**
         * 设置裁剪之后的图片的质量
         * @param compressQuality
         * @return
         */
        public CropOption setCompressionQuality(@IntRange(from = 0) int compressQuality) {
            mCropBundle.putInt(EXTRA_COMPRESSION_QUALITY, compressQuality);
            return this;
        }

        /**
         * 设置裁剪框的宽高比例
         * @param x
         * @param y
         * @return
         */
        public CropOption setAspectRatio(float x, float y) {
            mCropBundle.putFloat(EXTRA_ASPECT_RATIO_X, x);
            mCropBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y);
            return this;
        }

        /**
         * 获取裁剪成功的结果
         * @param intent
         * @return
         */
        public static Uri getResult(@NonNull Intent intent){
            return intent.getParcelableExtra(CropActivity.EXTRA_RESULT_URI);
        }

        /**
         * 获取裁剪失败的结果
         * @param intent
         * @return
         */
        public static Throwable getError(@NonNull Intent intent){
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
            mCropIntent.setClass(context,CropActivity.class);
            mCropIntent.putExtras(mCropBundle);
            return mCropIntent;
        }
    }
}
