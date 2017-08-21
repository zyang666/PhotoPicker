package com.photopicker.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.photopicker.R;
import com.photopicker.bean.Folder;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2017/6/18.
 *
 * 展示文件夹列表
 */

public class FolderPopupWindow extends PopupWindow{

    private Activity mActivity;
    private ListView mListView;
    private List<Folder> mDatas;
    private BucketListAdapter mBucketListAdapter;
    private OnBucketClickListener mOnBucketClickListener;
    private int mCurrentPosition;
    private Folder mCurrentSelect;

    public FolderPopupWindow(Activity context){
        mActivity = context;
        View inflate = View.inflate(context, R.layout.popup_window_view, null);
        setContentView(inflate);

        Point point = new Point();
        context.getWindowManager().getDefaultDisplay().getSize(point);
        int w = point.x;
        int h = point.y;
        setWidth(w);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        setOutsideTouchable(true);
        update();
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        initView(inflate);
        initListener();
    }

    public void setData(List<Folder> list){
        mDatas = list;
        //默认选中第一条
        if(list != null && list.size() > 0) {
            list.get(0).setSelector(true);
            mCurrentSelect = list.get(0);
        }
        mBucketListAdapter.setData(list);
    }

    public void showPopupWindow(View parent){
        showAsDropDown(parent);
    }

    private void initView(View inflate) {
        mListView = (ListView) inflate.findViewById(R.id.popup_list_view);

        mDatas = new ArrayList<>();
        mBucketListAdapter = new BucketListAdapter(mActivity, mDatas);
        mListView.setAdapter(mBucketListAdapter);
    }

    /**
     * 更新选择图片的显示
     * @param position
     */
    private void updaeSelectorUI(int position) {
        if(mCurrentSelect != null){//取消之前选中的文件夹
            mCurrentSelect.setSelector(false);
        }
        Folder item = mBucketListAdapter.getItem(position);
        item.setSelector(true);
        //记录当前选择的文件夹
        mCurrentSelect = item;
    }


    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果点击的条目是上一次点击的老条目，则不刷新数据，中断回调
                if(mCurrentPosition == position){
                    dismiss();
                    return;
                }

                updaeSelectorUI(position);

                mCurrentPosition = position;
                if(mOnBucketClickListener != null && mDatas.get(position) != null){
                    mOnBucketClickListener.bucketClick(mDatas.get(position).getFolderName());
                }
                dismiss();
            }
        });
    }

    /**---------------------------------------------------------------------------------------------
     * 文件夹列表的适配器
     */
    public class BucketListAdapter extends BaseAdapter {

        private Context mContext;
        private List<Folder> mImages;

        public BucketListAdapter(Context context, List<Folder> images){
            mContext = context;
            mImages = images;
        }

        public void setData(List<Folder> images){
            mImages = images;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImages == null ? 0 : mImages.size();
        }

        @Override
        public Folder getItem(int position) {
            if(mImages != null && mImages.size() > position){
                return mImages.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bucket, null);
                holder.photo = (ImageView) convertView.findViewById(R.id.bucket_image);
                holder.root_view = convertView.findViewById(R.id.root_view);
                holder.title = (TextView) convertView.findViewById(R.id.bucket_title);
                holder.photo_count = (TextView) convertView.findViewById(R.id.photo_count);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            Folder bucket = getItem(position);
            if(bucket != null){
                if(bucket.isSelector()){
                    holder.root_view.setBackgroundColor(Color.parseColor("#ECECEC"));
                }else {
                    holder.root_view.setBackgroundColor(Color.WHITE);
                }
                holder.title.setText(bucket.getFolderName());
                holder.photo_count.setText(bucket.getImageCount()+"");

                Images images = bucket.getFirstImg();
                if(images != null) {
                    PhotoManager.get().loadImage(holder.photo, images.getImgPath());
                }

            }
            return convertView;
        }

         class ViewHolder{
            ImageView photo,selectIcon;
            TextView title,photo_count;
             View root_view;
        }
    }


    public void setOnBucketClickListener(OnBucketClickListener listener){
        mOnBucketClickListener = listener;
    }
    public interface OnBucketClickListener{
        void bucketClick(String bucketName);
    }
}
