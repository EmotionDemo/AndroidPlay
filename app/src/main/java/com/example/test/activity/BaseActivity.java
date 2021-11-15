package com.example.test.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.util.ScreenUtil;

public class BaseActivity extends AppCompatActivity {
    protected int[] widthAndHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDeepStatusBar(true, this);
        setContentView(R.layout.activity_base);
        ScreenUtil.screenAdapt(this);
        widthAndHeight = ScreenUtil.getScreenProps(this);
    }

    /**
     * 设置沉浸式
     *
     * @param isChange
     * @param mActivity
     * @return
     */
    private void setDeepStatusBar(boolean isChange, Activity mActivity) {
        if (!isChange) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 透明状态栏
            Window window = mActivity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    /* | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION */
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            // 设置状态栏文字颜色及图标为深色
            mActivity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


}
