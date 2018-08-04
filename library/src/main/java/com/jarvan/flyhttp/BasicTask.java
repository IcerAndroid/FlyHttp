package com.jarvan.flyhttp;

import android.os.Build;
import android.text.TextUtils;

import java.io.Reader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 创建日期：2018/8/4 on 下午1:50
 * 描述:
 * 作者:张冰
 */
public abstract class BasicTask<T> implements TaskProtocol {
    private static final int SUCCESS_CODE = 10000;
    protected ResponseBean<T> mResponseBean;
    protected List<NameValuePair> mParams = new ArrayList<>();
    private ResponseListener<T> mResponseListener;

    public BasicTask() {
    }

    protected String getScheme() {
        return ConfigConstant.SCHEME;
    }

    protected abstract String getHost();

    protected abstract String getPath();

    @Override
    public String getUrl() {
        return String.format(Locale.CHINA, "%s%s%s", getScheme(), getHost(), getPath());
    }

    @Override
    public List<NameValuePair> getParams() {
        return mParams;
    }

    @Override
    public String getVersion() {
        return ConfigConstant.VERSION;
    }

    @Override
    public boolean onStartRequest() {
        return false;
    }

    @Override
    public void onRequestResult(Reader reader) throws Exception {

    }

    @Override
    public boolean onEndRequest() {
        return false;
    }

    @Override
    public void onComplete() {
        if (mResponseListener != null) {
            if (mResponseBean != null && mResponseBean.getCode() == SUCCESS_CODE) {
                mResponseListener.onSuccess(mResponseBean.getData());
            } else {
                if (mResponseBean == null) {
                    mResponseBean = new ResponseBean<>();
                    mResponseBean.setCode(-1);
                    mResponseBean.setMsg("对不起，服务器忙");
                }
                mResponseListener.onFailure(mResponseBean.getCode(), mResponseBean.getMsg());
            }

            mResponseListener.onComplete();
        }

    }

    /**
     * 添加参数
     *
     * @param key   参数名
     * @param value value值
     */
    public void addParams(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            mParams.add(new NameValuePair(key, value));
        }
    }

    /**
     * 请求监听
     */
    public interface ResponseListener<T> {

        void onSuccess(T result);

        void onFailure(int code, String msg);

        void onComplete();
    }
}
