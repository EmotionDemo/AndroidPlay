package com.example.test.network;

import com.example.test.util.SpUtil;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求头绑上一个Header
 */
public class BindHeaderCookieInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        HashSet<String> cookieSet = (HashSet<String>) SpUtil.getCookie();
        final Request.Builder builder = chain.request().newBuilder();
        // 添加Cookie
        if (cookieSet != null) {
            for (String cookie : cookieSet) {
                builder.addHeader("Cookie", cookie);
            }
        }
        return chain.proceed(builder.build());
    }
}
