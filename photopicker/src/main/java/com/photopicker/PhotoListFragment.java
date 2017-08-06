package com.photopicker;

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.photopicker.base.BasePhotoListFragment;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;
import com.photopicker.util.PermissionsUtil;
import com.photopicker.util.PhotoUtil;
import com.photopicker.widget.PhotoListView;

/**
 * Created by zy on 2017/8/6.
 *  展示相册列表
 */

public class PhotoListFragment extends BasePhotoListFragment{

    @Override
    protected void init() {

    }

    @Override
    protected View getToolbar() {
        return null;
    }

    @Override
    protected boolean showBottomLayout() {
        return false;
    }

    @Override
    protected View getBottomView() {

        return null;
    }


    @Override
    public boolean showCamera() {
        return false;
    }

    @Override
    public Drawable getCameraIcon() {
        return null;
    }

    @Override
    public PhotoListView.PhotoListViewHolder getPhotoViewHolder() {
        View view = View.inflate(getContext(), R.layout.item_photo_list, null);
        return new ViewHolder(view);
    }


    class ViewHolder extends PhotoListView.PhotoListViewHolder{
        private  ImageView mItemIcon;
        private  ImageView mSelectorIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            mSelectorIcon = (ImageView) itemView.findViewById(R.id.selector_icon);
            mSelectorIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public void bind(Images images, int position) {
            PhotoManager.get().loadThumbnail(mItemIcon,images.getImgPath());
        }
    }

}
