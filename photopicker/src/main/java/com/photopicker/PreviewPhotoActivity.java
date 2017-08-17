package com.photopicker;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;
import com.photopicker.util.PhotoUtil;
import com.photopicker.widget.PreviewPhotoViewPager;

import java.util.ArrayList;

/**
 * Created by zhangyang on 2017/8/4.
 *
 * 预览大图
 */

public class PreviewPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mSelectIcon;
    private RelativeLayout mBottomBar;
    private FrameLayout mCancel;
    private ImageView mIvDelete;
    private TextView mTitle;
    private TextView mCompleted;
    private Toolbar mToolBar;
    private PreviewPhotoViewPager mPreviewViewPager;
    private int mMaxSelectorCount;
    private TextView mTvEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        initView();

        initListener();

        updateSelectedCount();
    }

    private void initView() {
        mPreviewViewPager = (PreviewPhotoViewPager) findViewById(R.id.view_pager);
        mSelectIcon = (TextView) findViewById(R.id.select_icon);
        mTvEdit = (TextView) findViewById(R.id.tv_edit);
        mTitle = (TextView) findViewById(R.id.title);
        mCompleted = (TextView) findViewById(R.id.completed);
        mBottomBar = (RelativeLayout) findViewById(R.id.bottom_bar);
        mCancel = (FrameLayout) findViewById(R.id.cancel);
        mIvDelete = (ImageView) findViewById(R.id.iv_delete);
        mToolBar = (Toolbar) findViewById(R.id.tool_bar);

        processIntent();
        updateTitle();
        checkSelectedStatus();
    }

    private void processIntent() {
        Intent intent = getIntent();
        if(intent != null){
            mMaxSelectorCount = intent.getIntExtra(Photo.PreviewOptin.EXTRA_MAX_SELECTOR_COUNT, 1);
            boolean canCrop = intent.getBooleanExtra(Photo.PreviewOptin.EXTRA_CAN_CROP, false);
            boolean canDelete = intent.getBooleanExtra(Photo.PreviewOptin.EXTRA_CAN_DELETE, false);
            int mode = intent.getIntExtra(Photo.PreviewOptin.EXTRA_MODE, Photo.MODE_CHECK);
            int currentPos = intent.getIntExtra(Photo.PreviewOptin.EXTRA_CURRENT_POSITOIN, 0);
            ArrayList<String> paths = intent.getStringArrayListExtra(Photo.PreviewOptin.EXTRA_PATHS);

            mTvEdit.setVisibility(canCrop ? View.VISIBLE : View.GONE);
            mIvDelete.setVisibility(canDelete ? View.VISIBLE : View.GONE);
            if(mode == Photo.MODE_OPTION) {
                String folderName = intent.getStringExtra(Photo.PreviewOptin.EXTRA_FOLDER_NAME);
                mPreviewViewPager.setData(PhotoManager.get().getImgFromFolder(folderName));
                mIvDelete.setVisibility(View.GONE);
                mCompleted.setVisibility(View.VISIBLE);
            }else {
                mPreviewViewPager.setPaths(paths);
                mBottomBar.setVisibility(View.GONE);
            }
            mPreviewViewPager.setCurrentItem(currentPos);
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
                Images item = mPreviewViewPager.getItem(position);
                if (item != null) {
                    mSelectIcon.setSelected(item.isSelector());
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
    }

    private void updateTitle(){
        //postion是从0开始的，这里加个1
        mTitle.setText((mPreviewViewPager.getCurrentItem() + 1) + "/" +mPreviewViewPager.getMaxCount());
    }

    private void updateSelectedCount(){
        int selectorCount = PhotoManager.get().getSelectedCount();
        if(selectorCount > 0){
            mCompleted.setEnabled(true);
        }else {
            mCompleted.setEnabled(false);
        }
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
//            setResult1(RESULT_OK);
            finish();

        }else  if(id == R.id.completed){//完成/上传
//            completed();

        }else if(id == R.id.iv_delete){//删除
//            delete();

        }else if(id == R.id.select_icon){//选择
            select();
            updateSelectedCount();

        }else if(id == R.id.tv_edit){//编辑

        }
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
            String path = item.getImgPath();
            if (mSelectIcon.isSelected()) {//选择
                PhotoManager.get().selector(path);
            } else {//取消选择
                PhotoManager.get().cancelSelector(path);
            }
        }
    }
}
