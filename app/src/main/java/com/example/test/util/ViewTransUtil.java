package com.example.test.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;


public class ViewTransUtil {

    /**
     * 设置显示时动画,从底部向上抽出
     * @param relativeLayout
     */
    public static void setVisibleTrans(LinearLayout relativeLayout){
        TranslateAnimation showAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        showAnim.setDuration(500);
        relativeLayout.startAnimation(showAnim);
        relativeLayout.setVisibility(View.VISIBLE);
    }


    /**
     * 设置消失动画，从顶部向下消失
     * @param relativeLayout
     */
    public static void setGoneTrans(LinearLayout relativeLayout){
        TranslateAnimation hideAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        hideAnim.setDuration(500);
        relativeLayout.startAnimation(hideAnim);
        relativeLayout.setVisibility(View.GONE);
    }
}
