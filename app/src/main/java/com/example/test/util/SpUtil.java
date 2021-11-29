package com.example.test.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SpUtil {
    private static SharedPreferences.Editor editor;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @SuppressLint("CommitPrefEdits")
    public static void setEditor(Context context) {
        mContext = context;
        if (editor == null) {
            editor = context.getSharedPreferences("COOKIE", Context.MODE_PRIVATE).edit();
        }
    }

    /**
     * 获取Sp实例
     * @return
     */
    private static SharedPreferences getSp() {
        return mContext.getSharedPreferences("COOKIE", Context.MODE_PRIVATE);
    }

    /**
     * 设置cookie
     *
     * @param cookieSet
     */
    public static void putCookie(HashSet cookieSet) {
        editor.putStringSet("cookie", cookieSet);
        editor.commit();
    }

    /**
     * 获取Cookie
     *
     * @return
     */
    public static Set<String> getCookie() {
        return getSp().getStringSet("cookie", new HashSet<>());
    }

    /**
     * 保存用户信息
     *
     * @param userSet
     */
    public static void putUserInfo(String userSet) {
        editor.putString("userInfo", userSet);
        editor.commit();
    }

    /***
     * 获取用户信息
     *
     * @return
     */
    public static String getUser() {
        return getSp().getString("userInfo", "");
    }

    /**
     * 清除本地Cookie
     */
    public static void clearCookie() {
        editor.remove("cookie").commit();
    }

}
