package com.example.detaildemo.view.player;


import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.utils.ContentDetailUtils;

/**
 * Ottプレイヤーレイアウト.
 */
public class OttPlayerViewLayout extends RelativeLayout {

    /** コンテキスト.*/
    private Context mContext;
    /** アクティビティ.*/
    private Activity mActivity;
    /**プログレスバー処理.*/
    private LinearLayout mProgressBarLayout;
    /** SurfaceView.*/
    private SurfaceView mSurfaceView;

    /**
     * コンストラクタ.
     * @param context コンテクスト
     */
    public OttPlayerViewLayout(final Context context) {
        this(context, null);
    }

    /**
     * コンストラクタ.
     * @param context コンテクスト
     * @param attrs attrs
     */
    public OttPlayerViewLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * コンストラクタ.
     * @param context コンテクスト
     * @param attrs attrs
     * @param defStyleAttr defStyleAttr
     */
    public OttPlayerViewLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mActivity = (Activity) context;
    }

    /**
     * Ottプレイヤービュー初期化.
     * @return SurfaceHolder SurfaceHolder
     */
    public SurfaceHolder initPlayerView() {
        if (mSurfaceView == null) {
            mSurfaceView = new SurfaceView(mContext);
            RelativeLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            mSurfaceView.setLayoutParams(layoutParams);
            this.addView(mSurfaceView);
            this.setVisibility(View.VISIBLE);
        }
        mActivity.getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        showProgress(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        requestFocus();
        return mSurfaceView.getHolder();
    }

    /**
     * 再生中のくるくる処理.
     * @param isShow true 表示　false 非表示
     */
    public void showProgress(final boolean isShow) {
        DTVTLogger.debug("OttPlayerViewLayout showProgress : " + isShow);
        if (mProgressBarLayout == null) {
            mProgressBarLayout = ContentDetailUtils.getProgressView(mContext);
            mProgressBarLayout.setVisibility(GONE);
        }
        if (isShow) {
            if (mProgressBarLayout.getVisibility() == VISIBLE) {
                return;
            }
            mProgressBarLayout.getChildAt(1).setVisibility(GONE);
            this.removeView(mProgressBarLayout);
            this.addView(mProgressBarLayout);
            mProgressBarLayout.setVisibility( View.VISIBLE);
        } else {
            this.removeView(mProgressBarLayout);
            mProgressBarLayout.setVisibility(View.GONE);
        }
    }
}
