package com.photopicker.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.photopicker.bean.Images;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2017/8/6.
 *
 */

public class PhotoListView extends GridView {

    private PhotoListAdapter mPhotoListAdapter;
    private Option mOption;

    public PhotoListView(Context context) {
        this(context,null);
    }

    public PhotoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPhotoListAdapter = new PhotoListAdapter(new ArrayList<Images>());
        setAdapter(mPhotoListAdapter);
    }

    public void setOption(@NonNull Option option){
        mOption = option;
    }

    public void setData(List<Images> data){
        mPhotoListAdapter.setDatas(data);
        mPhotoListAdapter.notifyDataSetChanged();
    }

    class PhotoListAdapter extends BaseAdapter{

        private static final int CAMERA = 1;

        List<Images> datas;

        PhotoListAdapter(List<Images> datas){
            this.datas = datas;
        }
        public void setDatas(List<Images> datas){
            this.datas = datas;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0 && mOption != null && mOption.showCamera()){
                return CAMERA;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            if(mOption != null && mOption.showCamera()){
               return super.getViewTypeCount() + 1;
            }
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
            PhotoListViewHolder holder = null;
            if(convertView == null){
                int itemViewType = getItemViewType(position);
                if(itemViewType == CAMERA){
                    ImageView imageView = new ImageView(getContext());
                    holder = new CameraViewHolder(imageView);
                }else {
                    if(mOption != null) {
                        holder = mOption.getPhotoViewHolder();
                    }else {
                        holder = new EmptyViewHolder(new TextView(getContext()));
                    }
                }
                convertView = holder.itemView;
                convertView.setTag(holder);
            }else {
                holder = (PhotoListViewHolder) convertView.getTag();
            }
            holder.bind(getItem(position),position);
            return convertView;
        }
    }


    public abstract static class PhotoListViewHolder{
        public View itemView;

        public PhotoListViewHolder(View itemView){
            this.itemView = itemView;
        }

        public abstract void bind(Images images,int position);
    }
    class CameraViewHolder extends PhotoListViewHolder{
        CameraViewHolder(ImageView itemView) {
            super(itemView);
        }

        @Override
        public void bind(Images images, int position) {
            if(mOption != null){
                if(itemView instanceof ImageView){
                    ((ImageView) itemView).setImageDrawable(mOption.getCameraIcon());
                }
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


    public interface Option{
        boolean showCamera();
        Drawable getCameraIcon();
        PhotoListViewHolder getPhotoViewHolder();
    }
}
