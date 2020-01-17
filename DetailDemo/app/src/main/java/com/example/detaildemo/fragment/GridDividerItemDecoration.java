package com.example.detaildemo.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.detaildemo.common.DTVTLogger;

/**
 * GridDividerItemDecoration.
 */
class GridDividerItemDecoration extends RecyclerView.ItemDecoration {
    /** コンテキスト.*/
    private Context mContext;
    /** 間隔.*/
    private float mInterval;
    /** 列数.*/
    private int mSpanCount;

    /**
     * コンストラクター.
     * @param context context
     * @param interval interval
     * @param spanCount spanCount
     */
    public GridDividerItemDecoration(final Context context, final  float interval, final int spanCount) {
        this.mContext = context;
        this.mInterval = interval;
        this.mSpanCount = spanCount;
    }


    @Override
    public void getItemOffsets(@NonNull final Rect outRect, @NonNull final View view, @NonNull final RecyclerView parent, @NonNull final RecyclerView.State state) {
        DTVTLogger.start();
        int position = parent.getChildAdapterPosition(view);
        int edgeSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mInterval, mContext.getResources().getDisplayMetrics());
        int column = position % mSpanCount;
        outRect.left = edgeSpace - column * edgeSpace / mSpanCount;
        outRect.right = (column + 1) * edgeSpace / mSpanCount;
        if (position < mSpanCount) {
            outRect.top = edgeSpace;
        }
        DTVTLogger.end();
    }
}
