package com.example.detaildemo.view;


import android.content.Context;
import android.util.AttributeSet;

import androidx.drawerlayout.widget.DrawerLayout;

/**
 * CustomDrawerLayout.
 */
public class CustomDrawerLayout extends DrawerLayout{
    /**
     * コンストラクター.
     * @param context context
     */
    public CustomDrawerLayout(final Context context) {
        super(context);
    }

    /**
     * コンストラクター.
     * @param context context
     * @param attrs attrs
     */
    public CustomDrawerLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * コンストラクター.
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public CustomDrawerLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

