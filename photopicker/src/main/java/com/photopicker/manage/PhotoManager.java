package com.photopicker.manage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.photopicker.bean.Folder;
import com.photopicker.bean.Images;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zy on 2017/8/6.
 *
 * 图片数据管理类
 */

public class PhotoManager implements Handler.Callback {
    private static final String TAG = "PhotoManager";

    private PhotoLoader mPhotoLoader;

    private List<Folder> mFolders;
    private static ExecutorService executorService;
    private Handler mHandler;

    private PhotoManager(){
        mPhotoLoader = new PhotoLoader();
        executorService = Executors.newFixedThreadPool(3);
        mHandler = new Handler(Looper.getMainLooper(),this);
    }

    private static PhotoManager sInstance;
    public static PhotoManager get(){
        if(sInstance == null){
            synchronized (PhotoManager.class){
                if(sInstance == null){
                    sInstance = new PhotoManager();
                }
            }
        }
        return sInstance;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case 10:
                MessageBean obj = (MessageBean) msg.obj;
                if(obj.callBack != null) {
                    if (obj.folders != null){
                        mFolders = obj.folders;
                        obj.callBack.success(mFolders);
                    }else {
                        obj.callBack.fail();
                    }
                }
                return true;
        }

        return false;
    }

    private void addTask(Runnable runnable){
        executorService.submit(runnable);
    }

    /**
     * 加载所有图片
     * @param context
     * @param reload 是否需要重新加载，即是否允许在回调中返回缓存中的数据
     * @param callBack
     */
    public void loadAllImgs(Context context,boolean reload, final LoadAllImgCallBack callBack){
        if(mFolders == null || reload) {
            final WeakReference<Context> contextWeakReference = new WeakReference<>(context);
            addTask(new Runnable() {
                @Override
                public void run() {
                    MessageBean messageBean = new MessageBean();
                    messageBean.callBack = callBack;
                    messageBean.folders = mPhotoLoader.loadAllImages(contextWeakReference.get());
                    if (mHandler != null) {
                        Message msg = mHandler.obtainMessage(10, messageBean);
                        mHandler.sendMessage(msg);
                    }
                }
            });

        }else {
            callBack.success(mFolders);
        }
    }

    /**
     * 加载所有图片,若有缓存，则拿缓存的数据
     */
    public void loadAllImgs(Context context,LoadAllImgCallBack callBack){
        loadAllImgs(context,false,callBack);
    }

    public void loadThumbnail(ImageView imageView, final String path){
        imageView.setTag(path);
        addTask(new Runnable() {
            @Override
            public void run() {
                mPhotoLoader.loadThumbnail(path);
            }
        });

    }

    /**
     * 获取某一文件夹下的所有图片信息
     * @return
     */
    public List<Images> getImgFromFolder(String folderName){
        if(TextUtils.isEmpty(folderName) && mFolders != null){
            return null;
        }
        for (Folder folder : mFolders) {
            if(folderName.equals(folder.getFolderName())){
                return folder.getImges();
            }
        }
        return null;
    }

    public interface LoadAllImgCallBack{
        void success(List<Folder> folders);
        void fail();
    }

    class MessageBean{
        LoadAllImgCallBack callBack;
        List<Folder> folders;
        Exception exception;
    }


    /**
     * 释放资源
     */
    private void releaseRes() {
        if(executorService != null){
            executorService.shutdownNow();
            executorService = null;
        }

        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if(mPhotoLoader != null){
            mPhotoLoader = null;
        }
    }

    public static void release(){
        if(sInstance != null){
            sInstance.releaseRes();
            sInstance = null;
        }
    }

}
