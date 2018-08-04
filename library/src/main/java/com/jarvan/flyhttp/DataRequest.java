package com.jarvan.flyhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * 创建日期：2018/8/4 on 下午3:40
 * 描述:  底层网络请求
 * 作者:张冰
 */
public class DataRequest {

    public byte[] startRequest(String url, List<NameValuePair> params) {
        try {
            ResponseBody body = new HttpRequest().post(url, params, null);
            return readInputStream(body.byteStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
            bos.flush();
        }
        bos.close();
        return bos.toByteArray();
    }

}
