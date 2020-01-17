package com.example.detaildemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.example.detaildemo.activity.ContentDetailActivity;
import com.example.detaildemo.common.DtvtConstants;
import com.example.detaildemo.data.bean.ContentsData;
import com.example.detaildemo.utils.ContentUtils;
import com.example.detaildemo.utils.DataConverter;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        final ContentsData contentsData = new ContentsData();
        contentsData.setServiceId("15");

        String recommendFlg = "recommendInfoKey";
        Intent intent = new Intent(this, ContentDetailActivity.class);
        ComponentName componentName = this.getComponentName();
        intent.putExtra(DtvtConstants.SOURCE_SCREEN, componentName.getClassName());
        if (ContentUtils.RECOMMEND_INFO_BUNDLE_KEY.equals(recommendFlg)) {
            intent.putExtra(recommendFlg, DataConverter.getContentDataToContentsDetail(contentsData, ContentUtils.RECOMMEND_INFO_BUNDLE_KEY));
        } else {
            intent.putExtra(recommendFlg, contentsData.getContentsId());
        }
        startActivity(intent);
    }
}
