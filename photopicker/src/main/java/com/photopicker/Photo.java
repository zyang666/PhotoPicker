package com.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.photopicker.manage.PhotoLoader;
import com.photopicker.manage.PhotoManager;

import java.util.ArrayList;


/**
 * Created by zy on 2017/8/4.
 *
 */

public class Photo {

    public static CropOption createCropOption(@NonNull Uri inputUri, @NonNull Uri outputUri){
        return new CropOption(inputUri,outputUri);
    }

    public static ListOption createListOptin(PhotoLoader photoLoader){
        if(photoLoader != null){
            PhotoManager.get().setPhotoLoader(photoLoader);
        }
        return new ListOption();
    }

    public static PreviewOptin createPreviewOptin(PhotoLoader photoLoader){
        if(photoLoader != null){
            PhotoManager.get().setPhotoLoader(photoLoader);
        }
        return new PreviewOptin();
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
        public static final String EXTRA_MAX_SELECTOR_COUNT = "extra_max_selector_count";
        public static final String EXTRA_NEED_CROP = "extra_need_crop";
        public static final String EXTRA_NEED_CAMERA = "extra_need_camera";
        public static final String EXTRA_SHOW_BOTTOM_LAYOUT = "extra_show_bottom_layout";
        public static final String EXTRA_CAMERA_URI = "extra_camera_uri";
        public static final String EXTRA_GRID_NUM_COLUMNS = "extra_grid_num_columns";
        public static final String EXTRA_AVATAR_MODE = "extra_avatar_mode";
        public static final String EXTRA_SELECTOR_LIST_RESULT = "extra_selector_list_result";
        public static final String EXTRA_AUTO_CROP_ENABLE = "extra_auto_crop_enable";

        private Intent mListIntent;
        private Bundle mListBundle;

        public ListOption(){
            mListIntent = new Intent();
            mListBundle = new Bundle();
        }

        /**
         * 设置相册列表的列数
         * @param numColumns
         * @return
         */
        public ListOption setNumColumns(int numColumns){
            mListBundle.putInt(EXTRA_GRID_NUM_COLUMNS,numColumns);
            return this;
        }

        /**
         * 设置为头像选取模式，设置true将强制将最大选择数量至为1,不显示底部预览布局
         * @return
         */
        public ListOption setAvatarMode(boolean avatarMode){
            mListBundle.putBoolean(EXTRA_AVATAR_MODE,avatarMode);
            return this;
        }

        /**
         * 设置自动裁剪功能
         * @param autoCropEnable 设置为true，则会在返回的结果中全部进行自动裁剪,，按照1:1的比例，居中裁剪方形图片
         * @return
         */
        public ListOption setAutoCropEnable(boolean autoCropEnable){
            mListBundle.putBoolean(EXTRA_AUTO_CROP_ENABLE,autoCropEnable);
            return this;
        }

        /**
         * 是否需要显示底部布局
         * @return
         */
        public ListOption showBottomLayout(boolean showBottomLayout){
            mListBundle.putBoolean(EXTRA_SHOW_BOTTOM_LAYOUT,showBottomLayout);
            return this;
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
            if(cropMode == Photo.CROP_MODE_CIRCLE){
                cropBundle.putBoolean(Photo.CropOption.EXTRA_CIRCLE_DIMMED_LAYER,true);
            }else {
                cropBundle.putBoolean(Photo.CropOption.EXTRA_CIRCLE_DIMMED_LAYER,false);
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

        /**
         * 获取图片选择列表返回的图片数据
         * @param intent
         * @return
         */
        public static ArrayList<String> getData(Intent intent){
            ArrayList<String> data = new ArrayList<>();
            if(intent != null){
                data = intent.getStringArrayListExtra(EXTRA_SELECTOR_LIST_RESULT);
            }
            return data;
        }
    }


    @IntDef({MODE_OPTION, MODE_PREVIEW})
    public @interface Mode{
    }
    /**
     * 操作模式，可启用大图的裁剪、选择功能
     * */
    public static final int MODE_OPTION = 1;

    /**
     * 预览模式，可启用大图的删除功能
     * */
    public static final int MODE_PREVIEW = 2;

    public static class PreviewOptin {

        public static final String EXTRA_CROP_OPTION_BUNDLE = "extra_crop_option_bundle";
        public static final String EXTRA_MODE = "extra_mode";
        public static final String EXTRA_CAN_DELETE = "extra_can_delete";
        public static final String EXTRA_CAN_CROP = "extra_need_crop";
        public static final String EXTRA_MAX_SELECTOR_COUNT = "extra_max_selector_count";
        public static final String EXTRA_CURRENT_POSITOIN = "extra_current_position";
        public static final String EXTRA_FOLDER_NAME = "extra_folder_name";
        public static final String EXTRA_PATHS = "extra_paths";
        public static final String EXTRA_PREVIEW_RESULT = "extra_preview_result";


        private Bundle mPreviewBundle;
        private Intent mBigImgIntent;

        public PreviewOptin(){
            mBigImgIntent = new Intent();
            mPreviewBundle = new Bundle();
        }

        public Bundle getPreviewBundle(){
            return mPreviewBundle;
        }

        /**
         * 设置需要预览的图片集
         * @param imgPaths
         * @return
         */
        public PreviewOptin setPaths(ArrayList<String> imgPaths){
            mPreviewBundle.putStringArrayList(EXTRA_PATHS,imgPaths);
            return this;
        }

        /**
         * 设置当前显示的position
         * @param position
         * @return
         */
        public PreviewOptin setCurrentPosition(int position){
            mPreviewBundle.putInt(EXTRA_CURRENT_POSITOIN,position);
            return this;
        }

        /**
         * 设置最大选择数量，只要模式为{@link #MODE_OPTION}才有效
         */
        public PreviewOptin setMaxSelectorCount(int maxSelectorCount){
            mPreviewBundle.putInt(EXTRA_MAX_SELECTOR_COUNT,maxSelectorCount);
            return this;
        }

        /**
         * 是否可编辑(裁剪)，只要模式为{@link #MODE_OPTION}才有效
         */
        public PreviewOptin canCrop(boolean canCrop){
            mPreviewBundle.putBoolean(EXTRA_CAN_CROP,canCrop);
            return this;
        }

        /**
         * 设置裁剪的模式 {@link #CROP_MODE_CIRCLE},{@link #CROP_MODE_SQUARE}
         * @param cropMode
         * @return
         */
        public PreviewOptin setCropMode(@CropMode int cropMode){
            Bundle cropBundle = mPreviewBundle.getParcelable(EXTRA_CROP_OPTION_BUNDLE);
            if(cropBundle == null){
                cropBundle = new Bundle();
            }
            if(cropMode == Photo.CROP_MODE_CIRCLE){
                cropBundle.putBoolean(Photo.CropOption.EXTRA_CIRCLE_DIMMED_LAYER,true);
            }else {
                cropBundle.putBoolean(Photo.CropOption.EXTRA_CIRCLE_DIMMED_LAYER,false);
            }
            mPreviewBundle.putParcelable(EXTRA_CROP_OPTION_BUNDLE,cropBundle);
            return this;
        }

        /**
         * 是否可删除，只要模式为{@link #MODE_PREVIEW}才有效
         */
        public PreviewOptin canDelete(boolean canDelete){
            mPreviewBundle.putBoolean(EXTRA_CAN_DELETE,canDelete);
            return this;
        }

        /**
         * 设置查看大图的模式
         * @param mode
         * @return
         */
        public PreviewOptin setMode(@Mode int mode){
            mPreviewBundle.putInt(EXTRA_MODE,mode);
            return this;
        }

        /**
         * 设置预览某一文件夹下所有图片
         * @param name
         * @return
         */
        public PreviewOptin setFolderName(String name){
            mPreviewBundle.putString(EXTRA_FOLDER_NAME,name);
            return this;
        }

        /**
         * 设置一个裁剪的option
         * @param cropOption
         * @return
         */
        public PreviewOptin withCropOption(@NonNull CropOption cropOption){
            Bundle bundle = cropOption.getBundle();
            Bundle cropBundle = mPreviewBundle.getParcelable(EXTRA_CROP_OPTION_BUNDLE);
            if(cropBundle == null) {
                mPreviewBundle.putParcelable(EXTRA_CROP_OPTION_BUNDLE, bundle);
            }else {
                cropBundle.putAll(bundle);
                mPreviewBundle.putParcelable(EXTRA_CROP_OPTION_BUNDLE,cropBundle);
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
            mBigImgIntent.setClass(context,PreviewPhotoActivity.class);
            mBigImgIntent.putExtras(mPreviewBundle);
            return mBigImgIntent;
        }

        public static ArrayList<String> getData(Intent intent){
            ArrayList<String> data = new ArrayList<>();
            if(intent != null){
                data = intent.getStringArrayListExtra(EXTRA_PREVIEW_RESULT);
            }
            return data;
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
