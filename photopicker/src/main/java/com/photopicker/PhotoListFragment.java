package com.photopicker;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.photopicker.base.BasePhotoListFragment;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;
import com.photopicker.util.PhotoUtil;
import com.photopicker.widget.PhotoListView;

/**
 * Created by zy on 2017/8/6.
 *
 */

public class PhotoListFragment extends BasePhotoListFragment{

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
        return true;
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

        public ViewHolder(View itemView) {
            super(itemView);
            mItemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
        }

        @Override
        public void bind(Images images, int position) {
            boolean isBitmap = PhotoUtil.checkFileIsBitmap(images.getThumbnail());
            if(isBitmap){
                PhotoManager.get().loadThumbnail(mItemIcon,images.getThumbnail());
            }else {
                PhotoManager.get().loadThumbnail(mItemIcon,images.getImgPath());
            }
        }
    }

}
