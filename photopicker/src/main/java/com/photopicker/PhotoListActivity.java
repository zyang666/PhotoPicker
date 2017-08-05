package com.photopicker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.photopicker.bean.Folder;
import com.photopicker.bean.Images;
import com.photopicker.manage.PhotoManager;
import com.photopicker.util.ImgUtil;
import com.photopicker.util.PermissionsUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.util.List;

/**
 * Created by zhangyang on 2017/8/2.
 *
 * 展示相册列表
 */

public class PhotoListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PhotoListActivity";
    private static final int REQUEST_CROP = 1;
    private TextView mText;
    private ImageView mImg;
    private Bitmap mBitmap;
    private EditText mEdtRight;
    private EditText mEdtLeft;
    private ImageView mTargetImg;
    private int CROP_MODE;
    private Context mContext;
    private UCropView mUCropView;
    private GestureCropImageView mCropImageView;
    private OverlayView mOverlayView;


    public static void start(Context context) {
        Intent intent = new Intent(context, PhotoListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_photo_list);
        mContext = this;

        initView();
    }

    private void initView() {
        mImg = (ImageView) findViewById(R.id.img);
        mTargetImg = (ImageView) findViewById(R.id.target_img);
        mEdtLeft = (EditText) findViewById(R.id.edt_left);
        mEdtRight = (EditText) findViewById(R.id.edt_right);



        findViewById(R.id.btn_crop).setOnClickListener(this);
        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_center).setOnClickListener(this);
        findViewById(R.id.btn_end).setOnClickListener(this);
        findViewById(R.id.btn_rotate).setOnClickListener(this);


        //检查读写权限
        boolean pass = PermissionsUtil.checkPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE,0);


    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_crop){



        }else if(id == R.id.btn){

            PhotoManager.get().loadAllImgs(this, new PhotoManager.LoadAllImgCallBack() {
                @Override
                public void success(List<Folder> folders) {
                    Images firstImg = folders.get(0).getFirstImg();

                    File inputFile = new File(firstImg.getImgPath());
                    File outFile = new File(mContext.getCacheDir().getAbsoluteFile() + "11.jpg");
                    Uri inputUri = Uri.fromFile(inputFile);
                    Uri outUri = Uri.fromFile(outFile);

                    Photo.createCropOption(inputUri,outUri)
                            .setRotateEnabled(false)
                            .setAspectRatio(1,1)
                            .start(PhotoListActivity.this,REQUEST_CROP);


                }

                @Override
                public void fail() {

                }
            });

        }else if(id == R.id.btn_start){
            CROP_MODE = ImgUtil.CROP_MODE_START;
        }else if(id == R.id.btn_center){
            CROP_MODE = ImgUtil.CROP_MODE_CENTER;
        }else if(id == R.id.btn_end){
            CROP_MODE = ImgUtil.CROP_MODE_END;
        }else if(id == R.id.btn_rotate){

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CROP){
            if(resultCode == RESULT_OK){
                Uri result = Photo.CropOption.getResult(data);
                mTargetImg.setImageBitmap(ImgUtil.decodeFile(mContext,result.getPath()));
            }
        }
    }
}
