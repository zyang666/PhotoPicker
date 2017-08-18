package com.photopicker.manage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.photopicker.bean.Folder;
import com.photopicker.bean.Images;
import com.photopicker.bean.ThumbnailImg;
import com.photopicker.util.PhotoUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zy on 2017/8/6.
 *
 * 图片数据管理类
 */

public class PhotoManager implements Handler.Callback {
    private static final String TAG = "PhotoManager";
    private static final int LOAD_ALL_IMG = 10;
    private static final int RESULT = 12;

    /**
     * 裁剪的集合,key : 未裁剪原路路径，value：裁剪后的路径
     */
    private Map<String,String> mCropPaths = new ConcurrentHashMap<>();

    /**
     * 已选择的图片路径
     */
    private Vector<String> mSelectedPaths = new Vector<>();

    /**
     * 已选择的图片对象
     */
    private Vector<Images> mSelectedImgs = new Vector<>();



    private List<Folder> mFolders;
    private static ExecutorService executorService;
    private Handler mHandler;
    private PhotoLoader mPhotoLoader;

    private PhotoManager(){
        executorService = Executors.newSingleThreadExecutor();
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
            case LOAD_ALL_IMG://加载全部图片
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

            case RESULT://获取结果
                MessageBean resultObj = (MessageBean) msg.obj;
                if(resultObj.resultCallBack != null) {
                    if (resultObj.result != null){
                        resultObj.resultCallBack.callback(resultObj.result);
                    }
                }
                return true;
        }

        return false;
    }

    public void addTask(Runnable runnable){
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
                    messageBean.folders = loadAllImages(contextWeakReference.get());
                    if (mHandler != null) {
                        Message msg = mHandler.obtainMessage(LOAD_ALL_IMG, messageBean);
                        mHandler.sendMessage(msg);
                    }
                }
            });

        }else {
            callBack.success(mFolders);
        }
    }

    @WorkerThread
    private List<Folder> loadAllImages(Context context) {
        if (context == null) return null;
        List<Images> imageList = PhotoUtil.getAllImges(context);
        Map<Integer, ThumbnailImg> thumbnailsSet = PhotoUtil.getAllThumbnailsSet(context);

        //将获得的图片按文件夹分类出来
        Map<String, Folder> tempFolder = new HashMap<>();
        int size = imageList.size();
        for (int i = 0; i < size; i++) {
            Images images = imageList.get(i);
            ThumbnailImg thumbnailImg = thumbnailsSet.get(images.getId());
            if (thumbnailImg != null) {
                images.setThumbnail(thumbnailImg.getPath());
            }

            Folder folder = tempFolder.get(images.getFolderName());
            if (folder == null) {
                folder = new Folder();
                folder.setFolderName(images.getFolderName());
                folder.setFirstImg(images);
                tempFolder.put(folder.getFolderName(), folder);
            }
            folder.addImg(images);
        }

        List<Folder> folderList = new ArrayList<>();
        Folder folderAll = new Folder();
        folderAll.setFolderName("所有图片");
        folderAll.setImges(imageList);
        if (imageList.size() > 0) {
            folderAll.setFirstImg(imageList.get(0));
        }
        folderList.add(folderAll);

        for (String key : tempFolder.keySet()) {
            folderList.add(tempFolder.get(key));
        }
        return folderList;
    }

    /**
     * 加载所有图片,若有缓存，则拿缓存的数据
     */
    public void loadAllImgs(Context context,LoadAllImgCallBack callBack){
        loadAllImgs(context,false,callBack);
    }

    public void setPhotoLoader(PhotoLoader photoLoader){
        mPhotoLoader = photoLoader;
    }

    public void loadImage(final ImageView imageView, final String path){
        if(mPhotoLoader != null){
            //如果图片的路径有裁剪过的图片，则显示裁剪过的图片
            String cropPath = mCropPaths.get(path);
            if(!TextUtils.isEmpty(cropPath)){
                mPhotoLoader.load(imageView, cropPath);
            }else {
                mPhotoLoader.load(imageView, path);
            }
        }else {
            Log.e(TAG, "loadImage: 请设置图片加载器");
        }
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

    public int getSelectedCount(){
        return mSelectedPaths.size();
    }

    public List<String> getSelectedPaths(){
        return mSelectedPaths;
    }

    public Map<String,String> getCropPaths(){
        return mCropPaths;
    }

    public List<Images> getSelectedImgs(){
        return mSelectedImgs;
    }

    /**
     * 选择一张图片
     * @param images
     */
    public void selector(Images images){
        if(images != null){
            String path = images.getImgPath();
            if(!TextUtils.isEmpty(path) && !mSelectedPaths.contains(path)){
                mSelectedPaths.add(path);
            }
            if(!mSelectedImgs.contains(images)) {
                mSelectedImgs.add(images);
            }
        }
    }

    /**
     * 取消选择一张图片
     * @param images
     */
    public void cancelSelector(Images images){
        if(images != null){
            String path = images.getImgPath();
            if(!TextUtils.isEmpty(path) && mSelectedPaths.contains(path)){
                mSelectedPaths.remove(path);
            }
            if(mSelectedImgs.contains(images)) {
                mSelectedImgs.remove(images);
            }
        }

//        cancelSelector(images,true);
    }

    /**
     * 取消选择一张图片
     * @param images
     */
    public void cancelSelector(Images images,boolean deleteSelectedImage){
        if(images != null){
            String path = images.getImgPath();
            if(!TextUtils.isEmpty(path) && mSelectedPaths.contains(path)){
                mSelectedPaths.remove(path);
            }
            if(deleteSelectedImage && mSelectedImgs.contains(images)) {
                mSelectedImgs.remove(images);
            }
        }
    }

    /**
     * 保存一张裁剪过的图片路径
     * @param srcPath 未裁剪的图片路径
     * @param cropPath 以裁剪的图片路径，与srcPath对应
     */
    public void saveCropImg(String srcPath,String cropPath){
        if(!TextUtils.isEmpty(srcPath) && !TextUtils.isEmpty(cropPath)){
            mCropPaths.put(srcPath,cropPath);
        }
    }

    /**
     * 获取结果，未裁剪的图片自动进行裁剪，裁剪比例为1：1
     * @param context
     * @param cropOutDirectoryPath 裁剪之后的图片存放的目录
     * @param resultCallBack
     */
    public void getCropAllResult(Context context, final String cropOutDirectoryPath, final ResultCallBack resultCallBack){
        final WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        addTask(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> result = new ArrayList<>();
                for (String selectedImg : mSelectedPaths) {
                    String cropPath = mCropPaths.get(selectedImg);
                    if(TextUtils.isEmpty(cropPath)){
                        Context context = contextWeakReference.get();
                        if(context == null){
                            setCropAllResult(result,resultCallBack);
                            return;
                        }

                        String outPath = cropOutDirectoryPath+"/"+System.currentTimeMillis() + ".jpg";
                        PhotoUtil.autoCrop(context,selectedImg,outPath);
                        File file = new File(outPath);
                        if (file.exists()) {
                            result.add(outPath);
                        }else {
                            Log.e(TAG, "onClick: 裁剪失败，selectedImg="+selectedImg);
                            result.add(selectedImg);
                        }
                    }else {
                        result.add(cropPath);
                    }
                }

                setCropAllResult(result,resultCallBack);
            }
        });
    }

    private void setCropAllResult(ArrayList<String> result, ResultCallBack resultCallBack){
        if (mHandler != null) {
            MessageBean messageBean = new MessageBean();
            messageBean.resultCallBack = resultCallBack;
            messageBean.result = result;
            Message msg = mHandler.obtainMessage(RESULT, messageBean);
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 获取选择的结果
     * @return
     */
    public ArrayList<String>  getResult(){
        ArrayList<String> result = new ArrayList<>();
        for (String selectedImg : mSelectedPaths) {
            String cropPath = mCropPaths.get(selectedImg);
            if(!TextUtils.isEmpty(cropPath)){
                result.add(cropPath);
            }else {
                result.add(selectedImg);
            }
        }
        return result;
    }


    public interface LoadAllImgCallBack{
        void success(List<Folder> folders);
        void fail();
    }

    class MessageBean{
        LoadAllImgCallBack callBack;
        List<Folder> folders;
        
        ResultCallBack resultCallBack;
        ArrayList<String> result;
    }

    public interface ResultCallBack{
        void callback(ArrayList<String> result);
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

        if(mFolders != null){
            mFolders.clear();
            mFolders = null;
        }
    }

    public static void release(){
        if(sInstance != null){
            sInstance.releaseRes();
            sInstance = null;
        }
    }

}
