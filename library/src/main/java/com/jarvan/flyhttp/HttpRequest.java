package com.jarvan.flyhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 创建日期：2018/8/4 on 下午3:42
 * 描述:
 * 作者:张冰
 */
public class HttpRequest {
    private static OkHttpClient sOkHttpClient;
    private int connectTimeout = 10;
    private int readTimeout = 10;
    private int writeTimeout = 10;

    public HttpRequest() {
        init();
    }

    private synchronized void init() {
        if (sOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
            builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
            builder.readTimeout(readTimeout, TimeUnit.SECONDS);
            sOkHttpClient = builder.build();
        }
    }

    public ResponseBody get(String url, Map<String, String> header) throws IOException {
        if (TextUtils.isEmpty(url)) {
            throw new IOException("请求地址不能为空");
        }
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                String value = entry.getValue();
                if (!TextUtils.isEmpty(value)) {
                    builder.addHeader(entry.getKey(), value);
                }
            }
        }
        return execRequest(builder.build());
    }

    public ResponseBody post(String url, List<NameValuePair> params, Map<String, String> header)
            throws IOException {
        if (TextUtils.isEmpty(url)) {
            throw new IOException("请求地址不能为空");
        }
        FormBody.Builder body = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (NameValuePair param : params) {
                if (!TextUtils.isEmpty(param.getName()) && !TextUtils.isEmpty(param.getValue())) {
                    body.add(param.getName(), param.getValue());
                }
            }
        }
        RequestBody requestBody = body.build();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(requestBody);

        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                String value = entry.getValue();
                if (!TextUtils.isEmpty(value)) {
                    builder.addHeader(entry.getKey(), value);
                }
            }
        }
        return execRequest(builder.build());
    }

    private ResponseBody execRequest(Request request) throws IOException {
        Response response = sOkHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            if (response.body() != null) {
                response.body().close();
            }
            throw new IOException("服务器异常：" + response);
        }
        return response.body();
    }
}
