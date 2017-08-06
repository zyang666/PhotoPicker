package com.photopicker.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.photopicker.Photo;
import com.photopicker.R;
import com.photopicker.bean.Folder;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;
import com.photopicker.util.PhotoUtil;
import com.photopicker.widget.PhotoListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2017/8/6.
 *
 */

public abstract class BasePhotoListFragment extends Fragment implements PhotoListView.Option {
    private static final String TAG = "BasePhotoListFragment";

    protected FrameLayout mToolbarContainer;
    protected FrameLayout mBottomContainer;
    protected PhotoListView mPhotoListView;
    protected List<Images> mImges;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbarContainer = (FrameLayout) getView().findViewById(R.id.toolbar_container);
        mBottomContainer = (FrameLayout) getView().findViewById(R.id.bottom_container);
        mPhotoListView = (PhotoListView) getView().findViewById(R.id.photo_list_view);
        mToolbarContainer.addView(getToolbar());
        if(showBottomLayout()) {
            mBottomContainer.addView(getBottomView());
        }

        processIntent();
        init();
    }

    protected void processIntent() {


    }

    private void init() {
        mPhotoListView.setOption(this);
        PhotoManager.get().loadAllImgs(getContext(), new PhotoManager.LoadAllImgCallBack() {
            @Override
            public void success(List<Folder> folders) {
                if(folders != null && folders.size() > 0) {
                    Folder folder = folders.get(0);
                    mImges = folder.getImges();
                    mPhotoListView.setData(mImges);
                }else {
                    Log.e(TAG, "success: 加载手机图片为空");
                }
            }

            @Override
            public void fail() {
                Log.e(TAG, "fail: 加载手机图片失败");
            }
        });
    }

    protected View getBottomView(){
        return null;
    }

    protected abstract View getToolbar();

    /**
     * 是否显示底部布局
     * @return
     */
    protected abstract boolean showBottomLayout();

}
