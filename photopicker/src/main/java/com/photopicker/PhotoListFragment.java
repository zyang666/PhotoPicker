package com.photopicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.photopicker.base.BasePhotoListFragment;
import com.photopicker.bean.Folder;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;
import com.photopicker.util.FileUtil;
import com.photopicker.util.PermissionsUtil;
import com.photopicker.util.PhotoUtil;
import com.photopicker.widget.FolderPopupWindow;
import com.photopicker.widget.PhotoListView;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2017/8/6.
 *  展示相册列表
 */

public class PhotoListFragment extends BasePhotoListFragment implements View.OnClickListener {
    private static final String TAG = "PhotoListFragment";


    private static final int REQUEST_CODE_CAMERA_PERMISSIONS = 0;

    private static final int REQUEST_CODE_CROP_IMAGE = 0x12;
    private static final int REQUSET_CAMERA = 0x13;
    private static final int REQUEST_CODE_PREVIEW = 0x14;

    private ImageView mArrows;
    private TextView mTitle;
    private TextView mCompleted;

    private int mMaxSelectorCount;
    private Uri mCameraUri;
    private boolean mNeedCrop;
    private boolean mNeedCamera;
    private boolean mShowBottomLayout;
    private LinearLayout mTitleContainer;
    private View mToolbar;
    private FolderPopupWindow mPopupWindow;
    private TextView mTvBottomPreview;
    private Bundle mCropOptionBundle;
    private String mFolderName = "所有图片";
    private boolean mAvatarMode;
    private boolean isFrist = true;
    private boolean mAutoCropEnable;
    private String mCameraSavePath;

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if(bundle!= null){
            mAutoCropEnable = bundle.getBoolean(Photo.ListOption.EXTRA_AUTO_CROP_ENABLE, false);
            mAvatarMode = bundle.getBoolean(Photo.ListOption.EXTRA_AVATAR_MODE, false);
            mNeedCamera = bundle.getBoolean(Photo.ListOption.EXTRA_NEED_CAMERA, false);
            mNeedCrop = bundle.getBoolean(Photo.ListOption.EXTRA_NEED_CROP, true);
            mShowBottomLayout = bundle.getBoolean(Photo.ListOption.EXTRA_SHOW_BOTTOM_LAYOUT, true);
//            mCameraUri = bundle.getParcelable(Photo.ListOption.EXTRA_CAMERA_URI);
            mCropOptionBundle = bundle.getBundle(Photo.ListOption.EXTRA_CROP_OPTION_BUNDLE);
            mMaxSelectorCount = bundle.getInt(Photo.ListOption.EXTRA_MAX_SELECTOR_COUNT, 1);
            int gridNumColumns = bundle.getInt(Photo.ListOption.EXTRA_GRID_NUM_COLUMNS);
            if(gridNumColumns > 1){
                mPhotoListView.setNumColumns(gridNumColumns);
            }

            if(mAvatarMode){//选择头像模式，强制将最大数量设置为1
                mMaxSelectorCount = 1;

            }
        }

        mPopupWindow = new FolderPopupWindow(getActivity());

        initListener();
    }

    private void initListener() {
        //popupWindow  dismiss
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mOverrideView.setVisibility(View.GONE);
                if(!mAvatarMode) {
                    mCompleted.setVisibility(View.VISIBLE);
                }
                startArrowsAnimation(180, 0);
            }
        });

        //选择文件夹
        mPopupWindow.setOnBucketClickListener(new FolderPopupWindow.OnBucketClickListener() {
            @Override
            public void bucketClick(final String bucketName) {
                mFolderName = bucketName;
                loadImgFromFolderName(bucketName,true);
                mTitle.setText(bucketName);
                updateCountText();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isFrist){
            loadImgFromFolderName(mFolderName,false);
            updateCountText();
        }
        isFrist = false;
    }

    @Override
    protected View getToolbar() {
        mToolbar = View.inflate(getContext(), R.layout.layout_tool_bar, null);
        mArrows = (ImageView) mToolbar.findViewById(R.id.arrows);
        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitleContainer = (LinearLayout) mToolbar.findViewById(R.id.title_container);
        mCompleted = (TextView) mToolbar.findViewById(R.id.completed);
        View cancel = mToolbar.findViewById(R.id.cancel);

        cancel.setOnClickListener(this);
        mTitleContainer.setOnClickListener(this);
        mCompleted.setOnClickListener(this);
        if(mAvatarMode){
            mCompleted.setVisibility(View.GONE);
        }
        return mToolbar;
    }

    @Override
    protected void firstLoadDataSuccess(List<Folder> data) {
        mPopupWindow.setData(data);
        if(data != null && data.size() > 0 && data.get(0) != null){
            mFolderName = data.get(0).getFolderName();
        }
    }

    @Override
    protected boolean showBottomLayout() {
        return mShowBottomLayout && !mAvatarMode;
    }

    @Override
    protected View getBottomView() {
        View bottomView = View.inflate(getContext(), R.layout.layout_photo_list_bottom, null);
        mTvBottomPreview = (TextView) bottomView.findViewById(R.id.tv_bottom_preview);
        mTvBottomPreview.setOnClickListener(this);
        return bottomView;
    }

    @Override
    public boolean showCamera() {
        return mNeedCamera;
    }

    @Override
    public Drawable getCameraIcon() {
        return PhotoUtil.getDrawableFromResId(getContext(),R.drawable.photo_ic_camera);
    }

    @Override
    public PhotoListView.PhotoListViewHolder getPhotoViewHolder() {
        View view = View.inflate(getContext(), R.layout.item_photo_list, null);
        return new ViewHolder(view);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mPhotoListView.getItemViewType(position) == PhotoListView.CAMERA && checkCameraPermission()){
            //点击的是相机item，打开相机
            startCamera();
        }else {
            //是选择头像模式，并且需要裁剪，则进入裁剪页面
            if (mAvatarMode && mNeedCrop) {
                Images iamg = mPhotoListView.getImages(position);
                if (iamg != null) {
                    String path = iamg.getImgPath();
                    Uri inputUri = Uri.fromFile(new File(path));
                    startCropActivity(inputUri);
                } else {
                    PhotoUtil.showShortToast(getContext(),"没找到对应的图片");
                }

            } else {
                startPreviewPhotoActivity(Photo.MODE_OPTION,mPhotoListView.getRealPos(position));
            }
        }
    }

    private boolean checkCameraPermission(){
        return PermissionsUtil.checkPermissions(getActivity(), Manifest.permission.CAMERA, REQUEST_CODE_CAMERA_PERMISSIONS);
    }

    /**
     * 启动预览大图界面
     */
    private void startPreviewPhotoActivity(int mode,int position){
        Photo.PreviewOptin previewOptin = Photo.createPreviewOptin(null);
        previewOptin.setMode(mode)
                .canCrop(mNeedCrop)
                .setMaxSelectorCount(mMaxSelectorCount)
                .setFolderName(mFolderName)
                .setCurrentPosition(position);
        previewOptin.getPreviewBundle().putBoolean(Photo.ListOption.EXTRA_AUTO_CROP_ENABLE,mAutoCropEnable);
        dealBundle(previewOptin);
        previewOptin.start(this, REQUEST_CODE_PREVIEW);
    }

    /**
     * 启动裁剪界面
     * @param inputUri
     */
    private void startCropActivity(Uri inputUri){
        Uri outputUri = Uri.fromFile(FileUtil.getCropFile(getContext(),".jpg"));
        Photo.CropOption cropOption = Photo.createCropOption(inputUri, outputUri)
                .setAspectRatio(1, 1);
        if (mCropOptionBundle != null) {
            cropOption.getBundle().putAll(mCropOptionBundle);
        }
        cropOption.start(this, REQUEST_CODE_CROP_IMAGE);
    }

    /**
     * 打开相机
     */
    private void startCamera() {
        File providerFile = FileUtil.getProviderFile(getContext());
        mCameraSavePath = providerFile.getAbsolutePath();
        Log.d(TAG, "startCamera: cameraSavePath="+ mCameraSavePath);
        mCameraUri = FileUtil.getProviderUri(getContext(), providerFile);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);//将拍拍取的照片保存到指定URI,外部传入
        startActivityForResult(intent, REQUSET_CAMERA);
    }

    /**
     * 处理外部传进来的bundle
     * @param previewOptin
     */
    private void dealBundle(Photo.PreviewOptin previewOptin){
        if(mCropOptionBundle != null){
            Bundle previewBundle = previewOptin.getPreviewBundle();
            Bundle cropBundle = previewOptin.getPreviewBundle().getBundle(Photo.PreviewOptin.EXTRA_CROP_OPTION_BUNDLE);
            if(cropBundle != null){
                cropBundle.putAll(mCropOptionBundle);
                previewBundle.putBundle(Photo.PreviewOptin.EXTRA_CROP_OPTION_BUNDLE,cropBundle);
            }else {
                previewBundle.putBundle(Photo.PreviewOptin.EXTRA_CROP_OPTION_BUNDLE, mCropOptionBundle);
            }
        }
    }

    /**
     * 更新右上角的数量显示和右下角的预览数量
     */
    public void updateCountText(){
        int selectorCount = PhotoManager.get().getSelectedCount();
        if(selectorCount > 0){
            mCompleted.setEnabled(true);
            mTvBottomPreview.setEnabled(true);
        }else {
            mCompleted.setEnabled(false);
            mTvBottomPreview.setEnabled(false);
        }
        if(mMaxSelectorCount > 1 && selectorCount > 0) {
            mCompleted.setText("完成(" + selectorCount + "/" + mMaxSelectorCount + ")");
            mTvBottomPreview.setText("预览(" + selectorCount + ")");
        }else {
            mCompleted.setText("完成");
            mTvBottomPreview.setText("预览");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.cancel){//取消
            if(mPopupWindow.isShowing()){
                mPopupWindow.dismiss();
            }else{
                getActivity().finish();
            }

        }else if(id == R.id.title_container){//点击title
            if(mPopupWindow != null) {
                mPopupWindow.showPopupWindow(mToolbar);
                mOverrideView.setVisibility(View.VISIBLE);
            }
            //popupWindow弹出的时候隐藏右上角的上传
            mCompleted.setVisibility(View.GONE);
            startArrowsAnimation(0, 180);

        }else if(id == R.id.completed){//完成
            completed();
        }else if(id == R.id.tv_bottom_preview){//预览
            startPreviewPhotoActivity(Photo.MODE_OPTION & Photo.MODE_PREVIEW,0);
        }
    }

    /**
     * 完成
     */
    private void completed(){
        showNoCancelLoading();
        if(!mAutoCropEnable){
            ArrayList<String> result = PhotoManager.get().getResult();
            Log.d(TAG, "completed: result="+result.toString());
            setPhotoResult(result);
            return;
        }
        PhotoManager.get().getCropAllResult(
                getContext(),
                getContext().getCacheDir().getAbsolutePath(),
                new PhotoManager.ResultCallBack() {
                    @Override
                    public void callback(ArrayList<String> result) {
                        hideNoCancelLoading();
                        setPhotoResult(result);
                        Log.d(TAG, "callback: result="+result);
                    }
                });

    }

    private void setPhotoResult(ArrayList<String> result){
        hideNoCancelLoading();
        Intent intent = new Intent();
        intent.putStringArrayListExtra(Photo.ListOption.EXTRA_SELECTOR_LIST_RESULT,result);
        getActivity().setResult(Activity.RESULT_OK,intent);
        getActivity().finish();
    }

    private void startArrowsAnimation(float fromDegrees, float toDegrees) {
        mArrows.clearAnimation();
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setDuration(0);
        rotateAnimation.setFillAfter(true);
        mArrows.startAnimation(rotateAnimation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSIONS) {//申请相机权限回调
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                startCamera();
            } else {
                PhotoUtil.showShortToast(getContext(),"没有相机权限，无法进入相机拍照！");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PREVIEW) {//查看大图回来
            if (resultCode == Activity.RESULT_OK) { //选择完成
                ArrayList<String> result = Photo.PreviewOptin.getData(data);
                setPhotoResult(result);
            }

        }else if(requestCode == REQUEST_CODE_CROP_IMAGE){//裁剪回来
            if (resultCode == Activity.RESULT_OK) {
                Uri cropResultUri = Photo.CropOption.getResult(data);
                Log.d(TAG, "onActivityResult: cropResultUri="+cropResultUri);
                Log.d(TAG, "onActivityResult: cropResultUri.getPath="+cropResultUri.getPath());
                ArrayList<String> cropResult = new ArrayList<>();
                cropResult.add(cropResultUri.getPath());
                setPhotoResult(cropResult);
            }

        }else if (requestCode == REQUSET_CAMERA) {//照相机回来
            if (resultCode == Activity.RESULT_OK) {
                if(TextUtils.isEmpty(mCameraSavePath)) return;
                File file = new File(mCameraSavePath);
                if(!file.exists()){
                    return;
                }
                if(mNeedCrop || mAvatarMode){
                    startCropActivity(Uri.fromFile(file));
                }else {
                    ArrayList<String> result = new ArrayList<>();
                    result.add(file.getAbsolutePath());
                    Log.d(TAG, "onActivityResult: path="+file.getAbsolutePath());
                    setPhotoResult(result);
                }
            }
        }

    }

    //--------------------------------- view holder ------------------------------------------------

    class ViewHolder extends PhotoListView.PhotoListViewHolder{
        private  View mCoverView;
        private  ImageView mItemIcon;
        private  ImageView mSelectorIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            mSelectorIcon = (ImageView) itemView.findViewById(R.id.selector_icon);
            mCoverView = itemView.findViewById(R.id.cover_view);
            if(mAvatarMode){
                mSelectorIcon.setVisibility(View.GONE);
            }
        }

        @Override
        public void bind(final Images images, int position) {
            if(images == null) return;
            PhotoManager.get().loadImage(mItemIcon, images.getImgPath());
            if (images.isSelector()) {
                mCoverView.setVisibility(View.VISIBLE);
                mSelectorIcon.setSelected(true);
            } else {
                mCoverView.setVisibility(View.GONE);
                mSelectorIcon.setSelected(false);
            }

            mSelectorIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mSelectorIcon.isSelected() && PhotoManager.get().getSelectedCount() >= mMaxSelectorCount){
                        PhotoUtil.showShortToast(getContext(),"最多只能选择"+mMaxSelectorCount+"张");
                        return;
                    }
                    mSelectorIcon.setSelected(!mSelectorIcon.isSelected());
                    if (mSelectorIcon.isSelected()) {
                        images.setSelector(true);
                        mCoverView.setVisibility(View.VISIBLE);
                        PhotoManager.get().selector(images);
                    } else {
                        images.setSelector(false);
                        mCoverView.setVisibility(View.GONE);
                        PhotoManager.get().cancelSelector(images);
                    }
                    updateCountText();
                }
            });
        }
    }

}
