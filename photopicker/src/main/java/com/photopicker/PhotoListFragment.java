package com.photopicker;

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.photopicker.base.BasePhotoListFragment;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;
import com.photopicker.util.PermissionsUtil;
import com.photopicker.util.PhotoUtil;
import com.photopicker.widget.PhotoListView;

import java.util.List;

/**
 * Created by zy on 2017/8/6.
 *  展示相册列表
 */

public class PhotoListFragment extends BasePhotoListFragment implements View.OnClickListener {
    private static final String TAG = "PhotoListFragment";

    private ImageView mArrows;
    private TextView mTitle;
    private TextView mCompleted;

    private int mMaxSelectorCount;
    private Uri mCameraUri;
    private boolean mNeedCrop;
    private boolean mNeedCamera;
    private boolean mShowBottomLayout;

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if(bundle!= null){
            mMaxSelectorCount = bundle.getInt(Photo.ListOption.EXTRA_MAX_SELECTOR_COUNT, 0);
            mNeedCrop = bundle.getBoolean(Photo.ListOption.EXTRA_NEED_CROP, false);
            mNeedCamera = bundle.getBoolean(Photo.ListOption.EXTRA_NEED_CAMERA, true);
            mShowBottomLayout = bundle.getBoolean(Photo.ListOption.EXTRA_SHOW_BOTTOM_LAYOUT, true);
            mCameraUri = bundle.getParcelable(Photo.ListOption.EXTRA_CAMERA_URI);
            int gridNumColumns = bundle.getInt(Photo.ListOption.EXTRA_GRID_NUM_COLUMNS);
            if(gridNumColumns > 1){
                mPhotoListView.setNumColumns(gridNumColumns);
            }
        }

        updateCountText();
    }

    @Override
    protected View getToolbar() {
        View toolbar = View.inflate(getContext(), R.layout.layout_tool_bar, null);
        View cancel = toolbar.findViewById(R.id.cancel);
        mArrows = (ImageView) toolbar.findViewById(R.id.arrows);
        mTitle = (TextView) toolbar.findViewById(R.id.title);
        mCompleted = (TextView) toolbar.findViewById(R.id.completed);
        cancel.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mCompleted.setOnClickListener(this);
        return toolbar;
    }


    @Override
    protected boolean showBottomLayout() {
        return mShowBottomLayout;
    }

    @Override
    protected View getBottomView() {
        View bottomView = View.inflate(getContext(), R.layout.layout_photo_list_bottom, null);
        TextView tvBottomEdit =  (TextView) bottomView.findViewById(R.id.tv_bottom_edit);
        TextView tvBottomPreview = (TextView) bottomView.findViewById(R.id.tv_bottom_preview);
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
        /*if(mPhotoListView.getItemViewType(position) == PhotoListAdapter.CAMERA_TYPE){
            //点击的是相机item，打开相机
            boolean pass = PermissionsUtil.checkPermissions(getContext(), Manifest.permission.CAMERA, REQUEST_CODE_CAMERA_PERMISSIONS);
            if(pass) {
                startCamera();
            }

        }else {
            if (mMaxSlectorCount == 1 && mNeedCrop) {//进入裁剪的前提条件满足，进入裁剪，否则进入查看大图页面
                Images iamg = mPhotoListAdapter.getItem(position - 1);
                if (iamg != null) {
                    String path = iamg.get_data();
                    // 去裁剪
                    startCropActivity(path);
                } else {
                    Toast.makeText(mContext, "没找到对应的图片", Toast.LENGTH_SHORT).show();
                }
            } else {
                //查看大图
                Photo.createBigImgOption()
                        .setPosition(position - 1)
                        .setBucketName(mBucketName)
                        .setMaxSelectorCount(mMaxSlectorCount)
                        .setSelectorList(mSelectorImages)
                        .setMode(Photo.MODE_SELECTOR)
                        .start(mContext,REQUEST_CODE_BIG_IMGE);

            }
        }*/
    }

    /**
     * 更新右上角的数量显示
     */
    public void updateCountText(){
        int selectorCount = PhotoManager.get().getSelectorCount();
        if(selectorCount > 0){
            mCompleted.setEnabled(true);
        }else {
            mCompleted.setEnabled(false);
        }
        if(mMaxSelectorCount > 1) {
            mCompleted.setText("完成(" + selectorCount + "/" + mMaxSelectorCount + ")");
        }else {
            mCompleted.setText("完成");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.cancel){//取消
            getActivity().finish();

        }else if(id == R.id.title){

        }else if(id == R.id.completed){//完成
            showNoCancelLoading();
            PhotoManager.get().getResult(
                    getContext(),
                    getContext().getCacheDir().getAbsolutePath(),
                    new PhotoManager.ResultCallBack() {
                        @Override
                        public void callback(List<String> result) {
                            hideNoCancelLoading();
                            if(result == null) return;
                            Log.d(TAG, "callback: result="+result.toString());
                            getActivity().finish();
                        }
                    });
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
        }

        @Override
        public void bind(Images images, int position) {
            if(images == null) return;
            PhotoManager.get().loadThumbnail(mItemIcon, images.getImgPath());
            if (images.isSelector()) {
                mCoverView.setVisibility(View.VISIBLE);
            } else {
                mCoverView.setVisibility(View.GONE);
            }

            final String path = images.getImgPath();
            mSelectorIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectorIcon.setSelected(!mSelectorIcon.isSelected());
                    if (mSelectorIcon.isSelected()) {
                        mCoverView.setVisibility(View.VISIBLE);
                        PhotoManager.get().selector(path);
                    } else {
                        mCoverView.setVisibility(View.GONE);
                        PhotoManager.get().cancelSelector(path);
                    }
                    updateCountText();
                }
            });
        }
    }

}
