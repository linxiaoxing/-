package com.example.detaildemo.fragment;


import android.util.SparseArray;

import androidx.fragment.app.Fragment;

import com.example.detaildemo.common.DTVTLogger;
import com.example.detaildemo.utils.ContentDetailUtils;

/**
 * コンテンツ詳細フラグメントファクトリー.
 */
public class DtvContentsDetailFragmentFactory {
    /**フラグメント初期化.*/
    private final SparseArray<Fragment> mFragments = new SparseArray<>();
    /** the position of the first fragment.*/
    private final static int CONTENTS_DETAIL_FRAGMENT_POSITION_ZERO = 0;
    /** the position of the second fragment.*/
    private final static int CONTENTS_DETAIL_FRAGMENT_POSITION_FIRST = 1;
    /** the position of the third fragment.*/
    private final static int CONTENTS_DETAIL_FRAGMENT_POSITION_SECOND = 2;
    /**
     * フラグメントクラスの生成、取得.
     * @param position タブポジション
     * @param tabType タブタイプ
     * @return fragment
     */
    public Fragment createFragment(final int position, final ContentDetailUtils.TabType tabType) {
        DTVTLogger.start();
        Fragment fragment = mFragments.get(position);
        if (fragment == null) {
            switch (position) {
                case CONTENTS_DETAIL_FRAGMENT_POSITION_ZERO:
                    fragment = new DtvContentsDetailFragment();
                    break;
                case CONTENTS_DETAIL_FRAGMENT_POSITION_FIRST:
                    if (ContentDetailUtils.TabType.VOD_EPISODE == tabType) {
                        fragment = new DtvContentsEpisodeFragment();
                    } else {
                        fragment = new DtvContentsChannelFragment();
                    }
                    break;
                case CONTENTS_DETAIL_FRAGMENT_POSITION_SECOND:
                    fragment = new DtvContentsBroadcastFragment();
                    break;
                default:
                    break;
            }
            mFragments.put(position, fragment);
        }
        return fragment;
    }
}