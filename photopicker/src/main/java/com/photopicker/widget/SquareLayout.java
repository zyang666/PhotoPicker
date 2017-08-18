package com.photopicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.photopicker.R;

/**
 * Created by zy on 2017/8/6.
 *
 */

public class SquareLayout extends RelativeLayout{

    private static final int VERTICAL = 1;
    private static final int HORIZONTAL = 2;
    private int mOrientation = VERTICAL;

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareLayout);
            mOrientation = typedArray.getInt(R.styleable.SquareLayout_orientation, 1);
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mOrientation == VERTICAL) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }else {
            super.onMeasure(heightMeasureSpec,heightMeasureSpec);
        }
    }
}
