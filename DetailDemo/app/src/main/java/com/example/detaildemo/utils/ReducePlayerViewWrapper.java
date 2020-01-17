package com.example.detaildemo.utils;


import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * プレイヤー横画面時にスワイプ動作により、ビューのサイズとマージンを変更する用.
 */
public class ReducePlayerViewWrapper {

    /** SurfaceView.*/
    private SurfaceView mSecureVideoPlayer;
    /** チャンネルリストビュー.*/
    private RelativeLayout mChannelListView;
    /** コンテンツ情報ビュー.*/
    private RelativeLayout mContentDetailView;
    /** 再生前くるくる処理.*/
    private LinearLayout mProgressBarLayout;
    /** 録画コントローラビューRelativeLayout.*/
    private RelativeLayout mRecordCtrlView;
    /** SurfaceView レイアウトパラメータ.*/
    private RelativeLayout.LayoutParams mParams;
    /** チャンネルリストビュー レイアウトパラメータ.*/
    private RelativeLayout.LayoutParams mChannelListParams;
    /** コンテンツ情報ビュー レイアウトパラメータ.*/
    private RelativeLayout.LayoutParams mContentDetailParams;
    /** 再生前くるくる処理 レイアウトパラメータ.*/
    private RelativeLayout.LayoutParams mProgressBarLayoutParams;
    /** 録画コントローラビュー レイアウトパラメータ.*/
    private RelativeLayout.LayoutParams mRecordCtrlViewParams;

    public ReducePlayerViewWrapper(final SurfaceView secureVideoPlayer,
                                   final RelativeLayout channelListView,
                                   final RelativeLayout contentDetailView,
                                   final LinearLayout progressBarLayout,
                                   final RelativeLayout recordCtrlView) {
        mSecureVideoPlayer = secureVideoPlayer;
        mChannelListView = channelListView;
        mContentDetailView = contentDetailView;
        mProgressBarLayout = progressBarLayout;
        mRecordCtrlView = recordCtrlView;

        mParams = (RelativeLayout.LayoutParams) mSecureVideoPlayer.getLayoutParams();
        mChannelListParams = (RelativeLayout.LayoutParams) mChannelListView.getLayoutParams();
        mContentDetailParams = (RelativeLayout.LayoutParams) mContentDetailView.getLayoutParams();
        if (mProgressBarLayout.getLayoutParams() == null) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            mProgressBarLayout.setLayoutParams(layoutParams);
        }
        mProgressBarLayoutParams = (RelativeLayout.LayoutParams) mProgressBarLayout.getLayoutParams();
        if (mRecordCtrlView.getLayoutParams() == null) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mRecordCtrlView.setLayoutParams(params);
        }
        mRecordCtrlViewParams = (RelativeLayout.LayoutParams) mRecordCtrlView.getLayoutParams();
    }

    public int getWidth() {
        return mParams.width;
    }

    public int getHeight() {
        return mParams.height;
    }

    public void setWidth(final float width) {
        mParams.width = (int) width;
        mSecureVideoPlayer.setLayoutParams(mParams);
    }

    public void setHeight(final float height) {
        mParams.height = (int) height;
        mSecureVideoPlayer.setLayoutParams(mParams);
    }

    public void setMarginBottom(final int margin) {
        mParams.bottomMargin = margin;
        mSecureVideoPlayer.setLayoutParams(mParams);
    }

    public int getMarginBottom() {
        return mParams.bottomMargin;
    }

    public int getRecordWidth() {
        return mRecordCtrlViewParams.width;
    }

    public int getRecordHeight() {
        return mRecordCtrlViewParams.height;
    }

    public void setRecordWidth(final float width) {
        mRecordCtrlViewParams.width = (int) width;
        mRecordCtrlView.setLayoutParams(mRecordCtrlViewParams);
    }

    public void setRecordHeight(final float height) {
        mRecordCtrlViewParams.height = (int) height;
        mRecordCtrlView.setLayoutParams(mRecordCtrlViewParams);
    }

    public void setRecordMarginBottom(final int margin) {
        mRecordCtrlViewParams.bottomMargin = margin;
        mRecordCtrlView.setLayoutParams(mRecordCtrlViewParams);
    }

    public int getRecordMarginBottom() {
        return mRecordCtrlViewParams.bottomMargin;
    }

    public void setRuleToTopAndLeft() {
        mParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
        mParams.addRule(RelativeLayout.ALIGN_TOP);
        mParams.addRule(RelativeLayout.ALIGN_LEFT);
        mSecureVideoPlayer.setLayoutParams(mParams);
    }

    public void setChannelListTopMargin(final float margin) {
        mChannelListParams.topMargin = (int) margin;
        mChannelListView.setLayoutParams(mChannelListParams);
    }

    public void setContentDetailLeftMargin(final float margin) {
        mContentDetailParams.leftMargin = (int) margin;
        mContentDetailView.setLayoutParams(mContentDetailParams);
    }

    public int getContentDetailLeftMargin() {
        return mContentDetailParams.leftMargin;
    }

    public void setChannelListTopMargin(final int margin) {
        mChannelListParams.topMargin = margin;
        mChannelListView.setLayoutParams(mChannelListParams);
    }

    public int getChannelListTopMargin() {
        return mChannelListParams.topMargin;
    }

    public void setContentDetailBottomMargin(final float margin) {
        mContentDetailParams.bottomMargin = (int) margin;
        mContentDetailView.setLayoutParams(mContentDetailParams);
    }

    public int getContentDetailBottomMargin() {
        return mContentDetailParams.bottomMargin;
    }

    public void setProgressBarTopMargin(final float height) {
        int progressBarLayoutHeight = mProgressBarLayout.getHeight();
        mProgressBarLayoutParams.topMargin = (int) ((height - progressBarLayoutHeight) / 2);
        mProgressBarLayout.setLayoutParams(mProgressBarLayoutParams);
    }

    public void setProgressBarLeftMargin(final float width) {
        int progressBarLayoutWidth = mProgressBarLayout.getWidth();
        mProgressBarLayoutParams.leftMargin = (int) ((width - progressBarLayoutWidth) / 2);
        mProgressBarLayout.setLayoutParams(mProgressBarLayoutParams);
    }

    public void setRuleForProgressBar() {
        mProgressBarLayoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBarLayout.setLayoutParams(mProgressBarLayoutParams);
    }

    public void setRuleForProgressBarInFullScreen() {
        mProgressBarLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBarLayout.setLayoutParams(mProgressBarLayoutParams);
    }

    public void setProgressBarForGoPortraitScreen() {
        RelativeLayout.LayoutParams mProgressBarLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mProgressBarLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBarLayout.setLayoutParams(mProgressBarLayoutParams);
    }

    public void setRecordCtrlView(final RelativeLayout recordCtrlView) {
        mRecordCtrlView = recordCtrlView;
    }
}

