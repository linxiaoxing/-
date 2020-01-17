package com.example.appupdate.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appupdate.R;
import com.example.appupdate.http.UpdateAppHttpUtil;
import com.example.appupdate.java.UpdateAppManager;
import com.example.appupdate.java.listener.ExceptionHandler;
import com.example.appupdate.java.utils.AppUpdateUtils;
import com.example.appupdate.java.utils.DrawableUtil;

public class JavaActivity extends AppCompatActivity {
    private static final String TAG = JavaActivity.class.getSimpleName();
    private String mUpdateUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json.txt";
    private String mUpdateUrl1 = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/json/json1.txt";
    private boolean isShowDownloadProgress;
    private String mApkFileUrl = "https://raw.githubusercontent.com/WVector/AppUpdateDemo/master/apk/sample-debug.apk";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);
        DrawableUtil.setTextSolidTheme((Button) findViewById(R.id.btn_diy_1));
        DrawableUtil.setTextSolidTheme((Button) findViewById(R.id.btn_diy_2));
        DrawableUtil.setTextSolidTheme((Button) findViewById(R.id.btn_constraint));
        DrawableUtil.setTextSolidTheme((Button) findViewById(R.id.btn_diy_3));
        DrawableUtil.setTextSolidTheme((Button) findViewById(R.id.btn_download));
        DrawableUtil.setTextSolidTheme((Button) findViewById(R.id.btn_default_silence));
        DrawableUtil.setTextSolidTheme((Button) findViewById(R.id.btn_default_silence_diy_dialog));
        DrawableUtil.setTextStrokeTheme((Button) findViewById(R.id.btn_default), 0xffe94339);
        DrawableUtil.setTextSolidTheme((Button) findViewById(R.id.btn_download));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                switch (requestCode) {
                    // 得到通过UpdateDialogFragment默认dialog方式安装，用户取消安装的回调通知，以便用户自己去判断，比如这个更新如果是强制的，但是用户下载之后取消了，在这里发起相应的操作
                    case AppUpdateUtils.REQ_CODE_INSTALL_APP:
                        Toast.makeText(this, "用户取消了安装包的更新", Toast.LENGTH_LONG).show();

                        break;
                    default:
                }
                break;
        }
    }

    /**
     * 最简方式
     *
     * @param view
     */
    public void updateApp(View view) {
        new UpdateAppManager
                .Builder()
                //当前Activity
                .setActivity(this)
                //更新地址
                .setUpdateUrl(mUpdateUrl)
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                })
                //实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                .build()
                .update();
    }
}

