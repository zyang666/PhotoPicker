package com.photopicker.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.photopicker.bean.Images;
import com.photopicker.bean.ThumbnailImg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyang on 2017/8/2.
 *
 */

public class ImgUtil {

    /**居中裁剪(默认)*/
    public static final int CROP_MODE_CENTER = 0;
    /**从图片的左上角开始裁剪*/
    public static final int CROP_MODE_START = 1;
    /**从图片的右下角开始裁剪*/
    public static final int CROP_MODE_END = 2;


    /**
     * 解析图片,如果图片大于屏幕大小，则默认会将图片按比例缩放至屏幕大小<br/>{@link #decodeFile(String, int, int)}
     * @param context
     * @param imgPath 图片本地路径
     * @return
     */
    public static Bitmap decodeFile(Context context,String imgPath){
        //获取屏幕大小
        Point screenSize = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(screenSize);
        return decodeFile(imgPath,screenSize.x,screenSize.y);
    }

    /**
     * 解析图片,按比例缩放图片
     *
     * @param imgPath 图片本地路径
     * @param width 需要返回的图片的宽度
     * @param height 需要返回的图片的高度
     * @return
     */
    public static Bitmap decodeFile(String imgPath,int width,int height){
        if(TextUtils.isEmpty(imgPath) || width == 0 || height == 0){
            return null;
        }

        File file = new File(imgPath);
        if(!file.exists()){
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath,options);

        int w = options.outWidth;
        int h = options.outHeight;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }

    /**
     * 自动裁剪，居中裁剪，裁剪比例为 1：1
     * @param bitmap
     * @return
     */
    public static Bitmap autoCrop(Bitmap bitmap){
        return autoCrop(bitmap,ImgUtil.CROP_MODE_CENTER,1,1);
    }

    /**
     * 自动裁剪，居中裁剪，裁剪比例通过参数传入
     * @param bitmap 需要裁剪的图片
     * @param scaleW 宽度比例
     * @param scaleH 高度比例
     * @return
     */
    public static Bitmap autoCrop(Bitmap bitmap,int scaleW,int scaleH){
        return autoCrop(bitmap,CROP_MODE_CENTER,scaleW,scaleH);
    }

    /**
     * 批量裁剪图片
     * @param context
     * @param inputPaths 需要裁剪的图片的路径集
     * @param outDirectoryPath 裁剪后的图片保存的文件夹的路径
     * @return
     */
    public static List<String> autoCrop(Context context,List<String> inputPaths,String outDirectoryPath){
        if(inputPaths == null || inputPaths.isEmpty()){
            return null;
        }

        List<String> outPaths = new ArrayList<>();
        for (String inputPath : inputPaths) {
            String outImgPath = outDirectoryPath + System.currentTimeMillis()+".jpg";
            autoCrop(context,inputPath,outImgPath);
            File file = new File(outImgPath);
            if(file.exists()){
                outPaths.add(outImgPath);
            }
        }
        return outPaths;
    }

    /**
     * 自动裁剪图片,居中裁剪，裁剪比例为 1:1
     * @param context
     * @param inputPath 图片原路径
     * @param outPath 裁剪后的图片存放的路径
     */
    public static void autoCrop(Context context,String inputPath,String outPath){
        if(TextUtils.isEmpty(outPath)){
            return;
        }
        Bitmap bitmap = decodeFile(context, inputPath);
        if(bitmap == null){
            return;
        }

        File outFile = new File(outPath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);
            Bitmap cropBmp = autoCrop(bitmap);
            cropBmp.compress(Bitmap.CompressFormat.JPEG,100,fos);
            if(!cropBmp.isRecycled()){
                cropBmp.recycle();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 自动裁剪图片，实现裁剪的核心方法
     *
     * @param bitmap 需要裁剪的图片,如果需要对图片路径进行操作获取bitmap，可使用
     *               <br/>{@link #decodeFile(Context, String)},{@link #decodeFile(String, int, int)}获取bitmap对象<br/>
     *               或者调用{@link #autoCrop(Context, String, String)}
     *
     * @param mode 裁剪模式:居中裁剪{@link #CROP_MODE_CENTER},
     *             <br/>从图片的左上角开始裁剪{@link #CROP_MODE_START},
     *             <br/>从图片的右下角开始裁剪{@link #CROP_MODE_END}
     *
     * @param scaleW 宽度比例
     * @param scaleH 高度比例
     * @return
     */
    public static Bitmap autoCrop(Bitmap bitmap,int mode,int scaleW,int scaleH){
        if(bitmap == null) return null;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        //按比例计算图片宽高
        Point point = calculateWH(w, h, scaleW, scaleH);
        int cropW = point.x;
        int cropH = point.y;

        int left;
        int top;
        if(mode == ImgUtil.CROP_MODE_START){
            left = 0;
            top = 0;

        }else if(mode == CROP_MODE_END){
            left = (w > cropW) ? w - cropW : 0;
            top = (h > cropH) ? h - cropH : 0;

        }else {
            left = (w > cropW) ? (w - cropW)/2 : 0;
            top = (h > cropH) ? (h -cropH)/2 : 0;
        }

        Bitmap cropBmp = Bitmap.createBitmap(bitmap, left, top, cropW, cropH, null, false);
        /*if(!cropBmp.equals(bitmap) && !bitmap.isRecycled()){
            bitmap.recycle();
        }*/
        return cropBmp;
    }

    /**
     * 按比例计算宽高
     * @param srcW 源图图宽度
     * @param srcH 源图高度
     * @param scaleW 宽度比例
     * @param scaleH 高度比例
     */
    private static Point calculateWH(int srcW,int srcH,int scaleW,int scaleH){
        Point point = new Point();
        int minWH = Math.min(srcW, srcH);
        int w = minWH * scaleW;
        int h = minWH * scaleH;

        if(w > srcW){
            int i = w - srcW;
            float be = i / (w*1f);
            w = Math.round(w - i);
            h = Math.round(h*(1f - be));
        }

        if(h > srcH){
            int i = h - srcH;
            float be = i / (h*1f);
            h = Math.round(h - i);
            w = Math.round(w*(1f - be));
        }

        point.x = w;
        point.y = h;
        return point;
    }




    /**
     * 获取手机中的所有图片(大图)
     * @param context
     * @return
     */
    public static List<Images> getAllImges(Context context) {
        Cursor cursor = null;
        try {
            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int folderName = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            List<Images> imageList = new ArrayList<Images>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Images image = new Images();
                    image.setId(cursor.getInt(idColumn));
                    image.setImgPath(cursor.getString(dateColumn));
                    image.setFolderName(cursor.getString(folderName));
                    imageList.add(image);
                }
            }

            //排序
            Collections.sort(imageList, new Comparator<Images>() {
                @Override
                public int compare(Images o1, Images o2) {
                    return o2.getId() - o1.getId();
                }
            });
            return imageList;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            } catch (Exception e) {
            }

        }
        return null;
    }

    /**
     * 获取手机所有的缩略图
     * @param context
     * @return 返回缩略图集合，key：缩略图所对应的大图的图片id，value：缩略图bean
     */
    public static Map<Integer,ThumbnailImg> getAllThumbnailsSet(Context context) {
        Cursor cursor = null;
        try {
            String[] projection = new String[]{
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.Thumbnails.DATA,
                    MediaStore.Images.Thumbnails.IMAGE_ID,};

            cursor = context.getContentResolver().query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            int idColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID);
            int dateColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
            int imageIdColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);

            Map<Integer,ThumbnailImg> tempThumbnailsSet = new HashMap<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ThumbnailImg thumbnailImg = new ThumbnailImg();
                    thumbnailImg.setId(cursor.getInt(idColumn));
                    thumbnailImg.setPath(cursor.getString(dateColumn));
                    thumbnailImg.setImageId(cursor.getInt(imageIdColumn));
                    tempThumbnailsSet.put(thumbnailImg.getImageId(),thumbnailImg);
                }
            }
            return tempThumbnailsSet;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

}
