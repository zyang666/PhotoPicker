package com.photopicker;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.photopicker.base.BaseActivity;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;
import com.photopicker.util.FileUtil;
import com.photopicker.util.PhotoUtil;
import com.photopicker.util.StatusBarUtil;
import com.photopicker.widget.HorizontalRecyclerView;
import com.photopicker.widget.PreviewPhotoViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyang on 2017/8/4.
 *
 * 预览大图
 */

public class PreviewPhotoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PreviewPhotoActivity";

    private static final int REQUEST_CODE_CROP_IMAGE = 0x01;

    private TextView mSelectIcon;
    private LinearLayout mBottomBar;
    private View mCancel;
    private ImageView mIvDelete;
    private TextView mTitle;
    private TextView mCompleted;
    private View mToolBar;
    private PreviewPhotoViewPager mPreviewViewPager;
    private int mMaxSelectorCount;
    private TextView mTvEdit;
    private boolean mAutoCropEnable;
    private Bundle mCropOptionBundle;
    private String mCropSrcPath;
    private HorizontalRecyclerView mHorizontalView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        StatusBarUtil.setTransparentForImageViewInFragment(this,null);


        initView();

        initListener();

        updateSelectedCount();
    }

    private void initView() {
        mPreviewViewPager = (PreviewPhotoViewPager) findViewById(R.id.view_pager);
        mHorizontalView = (HorizontalRecyclerView) findViewById(R.id.horizontal_view);
        mSelectIcon = (TextView) findViewById(R.id.select_icon);
        mTvEdit = (TextView) findViewById(R.id.tv_edit);
        mTitle = (TextView) findViewById(R.id.title);
        mCompleted = (TextView) findViewById(R.id.completed);
        mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        mCancel = findViewById(R.id.cancel);
        mIvDelete = (ImageView) findViewById(R.id.iv_delete);
        mToolBar = findViewById(R.id.tool_bar);
        mHorizontalView.setData(PhotoManager.get().getSelectedImgs());

        processIntent();
        updateTitle();
        checkSelectedStatus();
    }

    private void processIntent() {
        Intent intent = getIntent();
        if(intent != null){
            mAutoCropEnable = intent.getBooleanExtra(Photo.ListOption.EXTRA_AUTO_CROP_ENABLE, false);
            mMaxSelectorCount = intent.getIntExtra(Photo.PreviewOptin.EXTRA_MAX_SELECTOR_COUNT, 1);
            mCropOptionBundle = intent.getBundleExtra(Photo.PreviewOptin.EXTRA_CROP_OPTION_BUNDLE);
            int mode = intent.getIntExtra(Photo.PreviewOptin.EXTRA_MODE, Photo.MODE_PREVIEW);
            int currentPos = intent.getIntExtra(Photo.PreviewOptin.EXTRA_CURRENT_POSITOIN, 0);
            boolean canCrop = intent.getBooleanExtra(Photo.PreviewOptin.EXTRA_CAN_CROP, false);
            boolean canDelete = intent.getBooleanExtra(Photo.PreviewOptin.EXTRA_CAN_DELETE, false);
            ArrayList<String> paths = intent.getStringArrayListExtra(Photo.PreviewOptin.EXTRA_PATHS);

            mTvEdit.setVisibility(canCrop ? View.VISIBLE : View.GONE);
            mIvDelete.setVisibility(canDelete ? View.VISIBLE : View.GONE);
            if(mode == Photo.MODE_OPTION) {
                String folderName = intent.getStringExtra(Photo.PreviewOptin.EXTRA_FOLDER_NAME);
                mPreviewViewPager.setData(PhotoManager.get().getImgFromFolder(folderName));
                mIvDelete.setVisibility(View.GONE);
                mCompleted.setVisibility(View.VISIBLE);

            }else if(mode == (Photo.MODE_OPTION & Photo.MODE_PREVIEW)){
                mPreviewViewPager.setData(PhotoManager.get().getSelectedImgs());
                mIvDelete.setVisibility(View.GONE);
                mCompleted.setVisibility(View.VISIBLE);

            }else {
                mPreviewViewPager.setPaths(paths);
                mBottomBar.setVisibility(View.GONE);
            }
            if(mMaxSelectorCount <= 1){
                mSelectIcon.setVisibility(View.GONE);
            }

            //当不需要裁剪（不显示“编辑”）和不显示“选择”按钮时，隐藏底部布局
            if(mSelectIcon.getVisibility() == View.GONE && mTvEdit.getVisibility() == View.GONE){
                mBottomBar.setVisibility(View.GONE);
            }

            mPreviewViewPager.setCurrentItem(currentPos);
            Images item = mPreviewViewPager.getItem(currentPos);
            if(item != null){
                mHorizontalView.setCurrentImage(item);
            }
        }
    }

    /**
     * 检查当前页选择状态
     */
    private void checkSelectedStatus(){
        Images item = mPreviewViewPager.getItem(mPreviewViewPager.getCurrentItem());
        if (item != null) {
            mSelectIcon.setSelected(item.isSelector());
        }
    }

    private void initListener() {
        mCancel.setOnClickListener(this);
        mCompleted.setOnClickListener(this);
        mIvDelete.setOnClickListener(this);
        mSelectIcon.setOnClickListener(this);
        mTvEdit.setOnClickListener(this);

        //viewPager选择监听，用于记录当前显示的position，并更新选择按钮
        mPreviewViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: positiong="+position);
                Images item = mPreviewViewPager.getItem(position);
                if (item != null) {
                    mSelectIcon.setSelected(item.isSelector());
                    mHorizontalView.setCurrentImage(item);
                }
                updateTitle();
            }
        });

        mPreviewViewPager.setItemOnClickListener(new PreviewPhotoViewPager.ItemOnClickListener() {
            @Override
            public void onClick(int position) {
                hideOrShowToolBar(150);
                hideOrShowBottomBar(150);
            }
        });

        mHorizontalView.setOnItemClickListener(new HorizontalRecyclerView.OnItemClickListener() {
            @Override
            public void click(Images image) {
                List<Images> currentImages = mPreviewViewPager.getCurrentImages();
                if(currentImages != null && currentImages.contains(image)){
                    int pos = currentImages.indexOf(image);
                    mPreviewViewPager.setCurrentItem(pos,false);
                }
            }
        });
    }

    private void updateTitle(){
        //position是从0开始的，这里加个1
        mTitle.setText((mPreviewViewPager.getCurrentItem() + 1) + "/" +mPreviewViewPager.getMaxCount());
    }

    private void updateSelectedCount(){
        int selectorCount = PhotoManager.get().getSelectedCount();
        if(mMaxSelectorCount > 1 && selectorCount > 0) {
            mCompleted.setText("完成(" + selectorCount + "/" + mMaxSelectorCount + ")");
        }else {
            mCompleted.setText("完成");
        }
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

    /**
     * 动画隐藏或显示底部选择条
     */
    private void hideOrShowBottomBar(long duration){
        mBottomBar.clearAnimation();
        float translationY = mBottomBar.getTranslationY();
        float start = translationY;
        float end = translationY > 0 ? 0 : mBottomBar.getMeasuredHeight();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mBottomBar, "translationY", start,end);
        animator.setDuration(duration);
        animator.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mBottomBar.measure(0,0);
        mToolBar.measure(0,0);
        hideOrShowToolBar(0);
        hideOrShowBottomBar(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideOrShowToolBar(200);
                hideOrShowBottomBar(200);
            }
        },300);
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.cancel){//返回
            finish();

        }else  if(id == R.id.completed){//完成/上传
            completed();

        }else if(id == R.id.iv_delete){//删除
//            delete();

        }else if(id == R.id.select_icon){//选择
            select();
            updateSelectedCount();

        }else if(id == R.id.tv_edit){//编辑
            Images images = mPreviewViewPager.getItem(mPreviewViewPager.getCurrentItem());
            mCropSrcPath = images.getImgPath();
            startCropActivity(Uri.fromFile(new File(mCropSrcPath)));
        }
    }

    /**
     * 启动裁剪界面
     * @param inputUri
     */
    private void startCropActivity(Uri inputUri){
        Uri outputUri = Uri.fromFile(FileUtil.getCropFile(this,".jpg"));
        Photo.CropOption cropOption = Photo.createCropOption(inputUri, outputUri)
                .setAspectRatio(1, 1);
        if (mCropOptionBundle != null) {
            cropOption.getBundle().putAll(mCropOptionBundle);
        }
        cropOption.start(this, REQUEST_CODE_CROP_IMAGE);
    }

    /**
     * 完成
     */
    private void completed(){
        showNoCancelLoading();
        //如果一张都没有选中，点完成就默认选中当前页显示的图片
        if(PhotoManager.get().getResult().isEmpty()){
            ArrayList<String> result = new ArrayList<>();
            Images item = mPreviewViewPager.getItem(mPreviewViewPager.getCurrentItem());
            if(item != null){
                String inputPath = item.getImgPath();
                Map<String, String> cropPaths = PhotoManager.get().getCropPaths();
                String cropPath = cropPaths.get(inputPath);

                if(mAutoCropEnable){//需要自动裁剪
                    if(!TextUtils.isEmpty(cropPath)){//当前图片已经裁剪，就不需要重复裁剪了
                        result.add(cropPath);
                    }else {
                        String outPath = getCacheDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
                        PhotoUtil.autoCrop(this, inputPath, outPath);
                        File file = new File(outPath);
                        if (file.exists()) {
                            result.add(outPath);
                        } else {
                            Log.e(TAG, "onClick: 裁剪失败，inputPath=" + inputPath);
                            result.add(inputPath);
                        }
                    }
                }else {
                    result.add(inputPath);
                }
            }
            Log.d(TAG, "completed: result=" + result.toString());
            setPhotoResult(result);

        }else {

            if (!mAutoCropEnable) {
                ArrayList<String> result = PhotoManager.get().getResult();
                Log.d(TAG, "completed: result=" + result.toString());
                setPhotoResult(result);
                return;
            }

            PhotoManager.get().getCropAllResult(
                    getBaseContext(),
                    getBaseContext().getCacheDir().getAbsolutePath(),
                    new PhotoManager.ResultCallBack() {
                        @Override
                        public void callback(ArrayList<String> result) {
                            hideNoCancelLoading();
                            setPhotoResult(result);
                            Log.d(TAG, "callback: result=" + result);
                        }
                    });

        }
    }

    private void setPhotoResult(ArrayList<String> result){
        hideNoCancelLoading();
        Intent intent = new Intent();
        intent.putStringArrayListExtra(Photo.PreviewOptin.EXTRA_PREVIEW_RESULT,result);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    /**
     * 选择/取消选择
     */
    private void select() {
        if(PhotoManager.get().getSelectedCount() == mMaxSelectorCount && !mSelectIcon.isSelected()){
            PhotoUtil.showShortToast(this,"最多只能选择"+mMaxSelectorCount+"张");
            return;
        }

        mSelectIcon.setSelected(!mSelectIcon.isSelected());
        Images item = mPreviewViewPager.getItem(mPreviewViewPager.getCurrentItem());
        if (item != null) {
            item.setSelector(mSelectIcon.isSelected());
            if (mSelectIcon.isSelected()) {//选择
                PhotoManager.get().selector(item);
                mHorizontalView.add(item);
            } else {//取消选择
                PhotoManager.get().cancelSelector(item);
                mHorizontalView.delete(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CROP_IMAGE){//裁剪回来
            if (resultCode == Activity.RESULT_OK) {
                Uri cropResultUri = Photo.CropOption.getResult(data);
                Log.d(TAG, "onActivityResult: cropResultUri="+cropResultUri);
                PhotoManager.get().saveCropImg(mCropSrcPath,cropResultUri.getPath());
                mPreviewViewPager.notifyDataSetChanged();
                mHorizontalView.notifyDataSetChanged();
            }
        }
    }
}
