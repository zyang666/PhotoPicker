package com.photopicker.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.photopicker.R;
import com.photopicker.bean.Folder;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;
import com.photopicker.util.PermissionsUtil;
import com.photopicker.widget.FolderPopupWindow;
import com.photopicker.widget.LoadingDialog;
import com.photopicker.widget.PhotoListView;

import java.util.List;

/**
 * Created by zy on 2017/8/6.
 *
 */

public abstract class BasePhotoListFragment extends Fragment implements PhotoListView.Option, AdapterView.OnItemClickListener {
    private static final String TAG = "BasePhotoListFragment";

    private static final int REQUEST_CODE_CAMERA_PERMISSIONS = 0;
    private static final int REQUEST_CODE_READ_PERMISSIONS = 1;

    private boolean isFirstLoad = true;

    protected FrameLayout mToolbarContainer;
    protected FrameLayout mBottomContainer;
    protected PhotoListView mPhotoListView;
    protected List<Images> mImges;
    protected FrameLayout mOverrideView;
    private FolderPopupWindow mPopupWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbarContainer = (FrameLayout) getView().findViewById(R.id.toolbar_container);
        mBottomContainer = (FrameLayout) getView().findViewById(R.id.bottom_container);
        mPhotoListView = (PhotoListView) getView().findViewById(R.id.photo_list_view);
        mOverrideView = (FrameLayout) getView().findViewById(R.id.override_view);
        mPhotoListView.setOnItemClickListener(this);
        mPhotoListView.setOption(this);

        init();

        mToolbarContainer.addView(getToolbar());
        if(showBottomLayout()) {
            mBottomContainer.addView(getBottomView());
        }else {
            mBottomContainer.setVisibility(View.GONE);
        }
        boolean pass = PermissionsUtil.checkPermissions(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, 10);
        if(pass){
            loadData(false);
        }
    }

    protected void loadData(boolean reload){
        loadImgFromFolderName(null,reload);


        /*PhotoManager.get().loadAllImgs(getContext(),reload, new PhotoManager.LoadAllImgCallBack() {
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
        });*/
    }

    protected void loadImgFromFolderName(final String folderName, boolean reload){
        if(reload){//重新加载，则清掉之前已经选择的图片
            PhotoManager.get().getCropPaths().clear();
            PhotoManager.get().getSelectedImgs().clear();
        }

        PhotoManager.get().loadAllImgs(getContext(), reload, new PhotoManager.LoadAllImgCallBack() {
            @Override
            public void success(List<Folder> folders) {
                if(folders != null && folders.size() > 0) {
                    if(TextUtils.isEmpty(folderName)) {
                        Folder folder = folders.get(0);
                        mImges = folder.getImges();
                    }else {
                        mImges = PhotoManager.get().getImgFromFolder(folderName);
                    }
                    mPhotoListView.setData(mImges);

                    if(isFirstLoad){
                        firstLoadDataSuccess(folders);
                    }
                    isFirstLoad = false;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSIONS) {//申请相机权限回调
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
//                startCamera();
            } else {
                // Permission Denied
                Toast.makeText(getContext(),"没有相机权限，无法进入相机拍照！",Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_CODE_READ_PERMISSIONS){//申请读写权限回调
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadData(false);
            } else {
                Toast.makeText(getContext(),"没有权限，无法获取图片内容！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private LoadingDialog mNoCancelLoading;
    public void showNoCancelLoading() {
        if(mNoCancelLoading == null) {
            mNoCancelLoading = new LoadingDialog(getContext());
            mNoCancelLoading.setCancelable(false);
            mNoCancelLoading.setCanceledOnTouchOutside(false);
            mNoCancelLoading.setBackPressedClickListener(new LoadingDialog.BackPressedClickListener() {
                @Override
                public void onBackPressed() {
                    getActivity().finish();
                    mNoCancelLoading.dismiss();
                }
            });
        }
        mNoCancelLoading.show();
    }

    public void hideNoCancelLoading(){
        if(mNoCancelLoading != null && mNoCancelLoading.isShowing()){
            mNoCancelLoading.dismiss();
        }
    }

    protected void init(){
        //nothing
    }

    protected View getBottomView(){
        return null;
    }

    protected abstract View getToolbar();

    protected abstract void firstLoadDataSuccess(List<Folder> data);

    /**
     * 是否显示底部布局
     * @return
     */
    protected abstract boolean showBottomLayout();
}
