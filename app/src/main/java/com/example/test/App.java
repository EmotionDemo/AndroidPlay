package com.example.test;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.example.test.network.BindHeaderCookieInterceptor;
import com.example.test.network.ReceiveCookieInterceptor;
import com.example.test.activity.model.Api;
import com.example.test.common.Constant;
import com.example.test.util.CrashHandler;
import com.example.test.util.SpUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressLint("Registered")
public class App extends Application {
    private static Retrofit retrofit;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.wanandroid.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
        mContext = this.getApplicationContext();
        //注册一个sp
        SpUtil.setEditor(mContext);
        CrashHandler.getInstance().init(this);
    }

    private static Retrofit getRetrofit() {
        return retrofit;
    }

    public static Api getApi(Class api) {
        return (Api) getRetrofit().create(api);
    }

    public static Context getContext() {
        if (mContext == null) {
            return null;
        }
        return mContext;
    }

    /**
     * 获取Okhttp客户端
     * @return
     */
    private OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new ReceiveCookieInterceptor())
                .addInterceptor(new BindHeaderCookieInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }
}
