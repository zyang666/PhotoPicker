package com.photopicker.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by zy on 2017/8/17.
 *
 */

public class PreviewPhotoViewPager extends ViewPager{

    private PreviewPhotoAdapter mPreviewPhotoAdapter;
    private ItemOnClickListener mItemOnClickListener;

    public PreviewPhotoViewPager(Context context) {
        this(context,null);
    }

    public PreviewPhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void setData(List<Images> datas){
        if(mPreviewPhotoAdapter == null) {
            mPreviewPhotoAdapter = new PreviewPhotoAdapter(datas);
            setAdapter(mPreviewPhotoAdapter);
        }else {
            mPreviewPhotoAdapter.setData(datas);
        }
    }

    public void setPaths(List<String> paths){
        if(mPreviewPhotoAdapter == null) {
            mPreviewPhotoAdapter = new PreviewPhotoAdapter(null);
            setAdapter(mPreviewPhotoAdapter);
            mPreviewPhotoAdapter.setPaths(paths);
        }else {
            mPreviewPhotoAdapter.setPaths(paths);
        }
    }

    public Images getItem(int position){
        if(mPreviewPhotoAdapter != null) {
            return mPreviewPhotoAdapter.getItem(position);
        }
        return null;
    }

    public int getMaxCount(){
        if(mPreviewPhotoAdapter != null) {
            return mPreviewPhotoAdapter.getCount();
        }
        return 0;
    }

    public void notifyDataSetChanged(){
        if(mPreviewPhotoAdapter != null){
            mPreviewPhotoAdapter.notifyDataSetChanged();
        }
    }

    public List<Images> getCurrentImages(){
        if(mPreviewPhotoAdapter != null){
            return mPreviewPhotoAdapter.getImages();
        }
        return null;
    }

    /**
     * Created by zy on 2017/6/17.
     * 查看大图适配器
     */

    class PreviewPhotoAdapter extends PagerAdapter {
        private List<Images> images = new ArrayList<>();
        private List<String> paths;

        public PreviewPhotoAdapter(List<Images> images){
            if(images == null) return;
            this.images.addAll(images);
        }

        public void setData(List<Images> images){
            if(images != null) {
                this.images.addAll(images);
            }
            if(paths != null){
                paths.clear();
            }
            notifyDataSetChanged();
        }

        public void setPaths(List<String> paths) {
            this.paths = paths;
            notifyDataSetChanged();
        }

        public List<Images> getImages() {
            return images;
        }

        public Images getItem(int position){
            if(images != null && images.size() > position) {
                return images.get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            if(paths != null && !paths.isEmpty()){
                return paths.size();
            }else {
                return images == null ? 0 : images.size();
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            PhotoView imageView = new PhotoView(container.getContext());
            String path = "";
            if(paths != null && !paths.isEmpty()){
                path = paths.get(position);
            }else {
                path = images.get(position).getImgPath();
            }
            PhotoManager.get().loadImage(imageView,path);
            imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    if(mItemOnClickListener != null){
                        mItemOnClickListener.onClick(position);
                    }
                }
            });
            container.addView(imageView);
            return imageView;
        }
    }

    public void setItemOnClickListener(ItemOnClickListener listener) {
        mItemOnClickListener = listener;
    }

    public interface ItemOnClickListener {
        void onClick(int position);
    }
}
