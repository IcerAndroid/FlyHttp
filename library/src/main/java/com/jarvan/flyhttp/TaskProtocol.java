package com.jarvan.flyhttp;

import java.io.Reader;
import java.util.List;

/**
 * 创建日期：2018/8/4 on 下午1:45
 * 描述: 网络请求任务协议
 * 作者:张冰
 */
public interface TaskProtocol {

    String getUrl();

    List<NameValuePair> getParams();

    String getVersion();

    boolean onStartRequest();

    void onRequestResult(Reader reader) throws Exception;

    boolean onEndRequest();

    void onComplete();
}
