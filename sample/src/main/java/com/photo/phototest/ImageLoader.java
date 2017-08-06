package com.photo.phototest;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.photopicker.manage.PhotoLoader;

/**
 * Created by zy on 2017/8/6.
 *
 */

public class ImageLoader implements PhotoLoader{
    @Override
    public void load(ImageView imageView, String path) {
        Glide.with(imageView.getContext()).load(path).into(imageView);
    }
}
