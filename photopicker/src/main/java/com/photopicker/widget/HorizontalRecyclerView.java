package com.photopicker.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.photopicker.R;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2017/8/18.
 *
 */

public class HorizontalRecyclerView extends RecyclerView{

    private InteriorAdapter mInteriorAdapter;

    private String mCurrentPath;
    private OnItemClickListener mItemClickListener;

    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getContext().getResources().getDisplayMetrics());
    }

    public HorizontalRecyclerView(Context context) {
        this(context,null);
    }

    public HorizontalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        setLayoutManager(new LinearLayoutManager(getContext(),HORIZONTAL,false));
    }


    public void setData(List<Images> data){
        if(mInteriorAdapter == null) {
            mInteriorAdapter = new InteriorAdapter(data);
            setAdapter(mInteriorAdapter);
        }else {
            mInteriorAdapter.setData(data);
        }
    }

    public void delete(Images images){
        if(images != null) {
            mCurrentPath = images.getImgPath();
        }
        if(mInteriorAdapter != null && mInteriorAdapter.getData() != null){
            List<Images> data = mInteriorAdapter.getData();
            if(data.contains(images)) {
                data.remove(images);
                mInteriorAdapter.notifyDataSetChanged();
            }
        }
    }

    public void add(Images images){
        if(images != null && mInteriorAdapter != null && mInteriorAdapter.getData() != null){
            List<Images> data = mInteriorAdapter.getData();
            if(!data.contains(images)) {
                data.add(images);
                mInteriorAdapter.notifyDataSetChanged();
                int currentPos = data.indexOf(images);
                if(currentPos > 0) {
                    smoothScrollToPosition(currentPos);
                }
            }
        }
    }

    public void notifyDataSetChanged(){
        if(mInteriorAdapter != null){
            mInteriorAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置当前选中的图片路径
     * @param currentImage
     */
    public void setCurrentImage(Images currentImage){
        if(currentImage != null) {
            mCurrentPath = currentImage.getImgPath();
        }
        if(mInteriorAdapter != null && mInteriorAdapter.getData() != null){
            List<Images> data = mInteriorAdapter.getData();
            if(data.contains(currentImage)) {
                int currentPos = data.indexOf(currentImage);
                smoothScrollToPosition(currentPos);
            }
            mInteriorAdapter.notifyDataSetChanged();
        }
    }


    class InteriorAdapter extends Adapter<InteriorViewHolder>{

        List<Images> data = new ArrayList<>();

        InteriorAdapter(List<Images> data){
            if(data == null) return;
            this.data.addAll(data);
        }

        void setData(List<Images> data){
            if(data == null) return;
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        List<Images> getData(){
            return data;
        }

        Images getPath(int position){
            if(data != null && data.size() > position){
                return data.get(position);
            }
            return null;
        }

        @Override
        public InteriorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_view, parent, false);
            return new InteriorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(InteriorViewHolder holder, int position) {
            if(position == getItemCount() - 1){
                holder.itemView.setPadding(dp2px(20),dp2px(13),dp2px(20),dp2px(13));
            }else {
                holder.itemView.setPadding(dp2px(20),dp2px(13),0,dp2px(13));
            }
            final Images image = getPath(position);
            if(image != null) {
                if (mCurrentPath.equals(image.getImgPath())) {
                    holder.selectframe.setSelected(true);
                } else {
                    holder.selectframe.setSelected(false);
                }
                PhotoManager.get().loadImage(holder.imgView, image.getImgPath());
            }

            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener != null){
                        mItemClickListener.click(image);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }
    }


    class InteriorViewHolder extends ViewHolder{

        ImageView imgView;
        FrameLayout selectframe;

        InteriorViewHolder(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.image);
            selectframe = (FrameLayout) itemView.findViewById(R.id.select_frame);
        }
    }

    public void setOnItemClickListener(OnItemClickListener l){
        mItemClickListener = l;
    }

    public interface OnItemClickListener{
        void click(Images iamge);
    }
}
