package com.photopicker.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photopicker.bean.Images;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2017/8/6.
 *
 */

public class PhotoListView extends GridView {

    public static final int CAMERA = 1;

    private PhotoListAdapter mPhotoListAdapter;
    private Option mOption;
    private boolean mShowCamera;

    public PhotoListView(Context context) {
        this(context,null);
    }

    public PhotoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOption(@NonNull Option option){
        mOption = option;
    }

    public void setData(List<Images> data){
        if(mOption != null && !(mOption.showCamera() == mShowCamera)){
            mPhotoListAdapter = new PhotoListAdapter(data);
            setAdapter(mPhotoListAdapter);
        }else {
            if (mPhotoListAdapter == null) {
                mPhotoListAdapter = new PhotoListAdapter(data);
                setAdapter(mPhotoListAdapter);
            } else {
                mPhotoListAdapter.setDatas(data);
                mPhotoListAdapter.notifyDataSetChanged();
            }
        }
    }

    public int getItemViewType(int position){
        if(mPhotoListAdapter == null){
            return 0;
        }
        return mPhotoListAdapter.getItemViewType(position);
    }

    class PhotoListAdapter extends BaseAdapter{



        List<Images> datas;

        PhotoListAdapter(List<Images> datas){
            this.datas = datas;
        }
        public void setDatas(List<Images> datas){
            this.datas = datas;
        }

        @Override
        public int getItemViewType(int position) {
            /*if(position == 0 && mOption != null && mOption.showCamera()){
                return CAMERA;
            }*/
            if(position == 0 && mShowCamera){
                return CAMERA;
            }

            return super.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            if(mOption != null && mOption.showCamera()){
                mShowCamera = true;
               return super.getViewTypeCount() + 1;
            }
            mShowCamera = false;
            return super.getViewTypeCount();
        }

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public Images getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //若需要的话，第一个条目为照相机条目
            if(getItemViewType(position) == CAMERA){
                PhotoListViewHolder cameraHolder = null;
                if (convertView == null) {
                    ImageView imageView = new ImageView(getContext());
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    cameraHolder = new CameraViewHolder(imageView);
                    convertView = cameraHolder.itemView;
                    convertView.setTag(cameraHolder);
                }else {
                    cameraHolder = (PhotoListViewHolder) convertView.getTag();
                }
                cameraHolder.bind(null,position);
                return convertView;

            }else {

                PhotoListViewHolder holder = null;
                if(convertView == null){
                    if (mOption != null) {
                        holder = mOption.getPhotoViewHolder();
                    } else {
                        holder = new EmptyViewHolder(new TextView(getContext()));
                    }
                    convertView = holder.itemView;
                    convertView.setTag(holder);
                }else {
                    holder = (PhotoListViewHolder) convertView.getTag();
                }

                int pos = position - (getViewTypeCount() - 1);
                holder.bind(getItem(pos),pos);
                return convertView;
            }
        }
    }

    /**
     * 相机viewHolder
     */
    class CameraViewHolder extends PhotoListViewHolder{
        ImageView imageView;
        CameraViewHolder(ImageView itemView) {
            super(itemView);
            this.imageView = itemView;
        }

        @Override
        public void bind(Images images, int position) {
            if(mOption != null && imageView != null){
                imageView.setImageDrawable(mOption.getCameraIcon());
            }
        }
    }

    class EmptyViewHolder extends PhotoListViewHolder{

        EmptyViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Images images, int position) {
        }
    }


    public abstract static class PhotoListViewHolder{
        public View itemView;

        public PhotoListViewHolder(View itemView){
            SquareLayout squareLayout = new SquareLayout(itemView.getContext());
            squareLayout.addView(itemView);
            this.itemView = squareLayout;
        }

        public abstract void bind(Images images,int position);
    }

    public interface Option{
        boolean showCamera();
        Drawable getCameraIcon();
        PhotoListViewHolder getPhotoViewHolder();
    }
}
