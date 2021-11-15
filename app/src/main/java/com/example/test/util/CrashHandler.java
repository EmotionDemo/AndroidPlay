/*
 * 版权所有 2009-2012山东新北洋信息技术股份有限公司保留所有权力。
 */
package com.example.test.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.example.test.activity.BaseActivity;

/**
 *
 * Created by wangfeng1 on 2018/2/5.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "MyCrash";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler instance = new CrashHandler();
    private Context mContext;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 保证只用一个实例
    private CrashHandler() {
    }

    // 获取CrashHandler实例，单例模式
    public static CrashHandler getInstance() {
        return instance;
    }

    /**
     * 初始化 autr 王峰
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaugthException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序默认的处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaugthException发生时会转入该函数处理 autr 王峰
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleExcepion(throwable) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, throwable);
        }
    }

    /**
     * 自定义错误处理，手机错误信息，发送错误报告 autr 王峰
     */
    private boolean handleExcepion(Throwable ex) {
        if (ex == null) {
            Log.d(TAG, "[handleExcepion]: 未捕获异常处理：无异常信息");
            return false;
        }
        try {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Looper.loop();
                }
            }.start();
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.flush();
            printWriter.close();
            String result = writer.toString();
            Log.d(TAG, "Fail handleExcepion===>" + result);
        } catch (Exception e) {
            Log.d(TAG, "[handleExcepion]: 异常返回 = ", e);
            return false;
        }
        return true;
    }
}
