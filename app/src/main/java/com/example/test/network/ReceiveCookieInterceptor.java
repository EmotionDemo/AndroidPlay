package com.example.test.network;

import android.text.TextUtils;

import com.example.test.util.SpUtil;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 获取Cookie
 */
public class ReceiveCookieInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            // 解析Cookie
            HashSet<String> cookieSet = new HashSet<>();
            for (String header : originalResponse.headers("Set-Cookie")) {
                if (!TextUtils.isEmpty(header)) {
                    cookieSet.add(header);
                }
            }
            SpUtil.putCookie(cookieSet );
        }
        return originalResponse;
    }
}
