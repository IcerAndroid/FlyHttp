package com.jarvan.flyhttp;

import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2018/8/4 on 下午2:05
 * 描述:
 * 作者:张冰
 */
public abstract class FormTask<T> extends BasicTask<T> {
    private static final int REQ_DELAIY_MIL_SEC = 400;
    private static final int MAX_RETRY_CROUNT = 3;
    protected final static Gson mGson = new Gson();
    private volatile int mRetryCount;
    private boolean needRetry = true;

    public FormTask() {
        addCommonParams();
    }

    @Override
    protected String getHost() {
        return ConfigConstant.HOST;
    }

    @Override
    public boolean onStartRequest() {
        return true;
    }

    public void addCommonParams() {
        mParams.add(new NameValuePair("_secdata", "commom"));
    }

    public Class getTypeClass() {
        return null;
    }

    @Override
    public void onRequestResult(Reader reader) throws Exception {
        Type type;
        if (getTypeClass() != null) {
            type = getType(ResponseBean.class, getTypeClass());
        } else {
            type = new TypeToken<ResponseBean<T>>() {
            }.getType();
        }
        mResponseBean = mGson.fromJson(reader, type);
    }

    @Override
    public boolean onEndRequest() {
        printParams();
        printResult();
        if (needRetry) {
            if (mRetryCount < MAX_RETRY_CROUNT) {
                mRetryCount++;
                if (mResponseBean != null && mResponseBean.getCode() != 0) {
                    mRetryCount = 0;
                } else {
                    try {
                        // 在当前请求的异步线程中，执行重试并等待
                        if (!isMainThread()) {
                            Thread.sleep(REQ_DELAIY_MIL_SEC);
                            retryRequest();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    /**
     * 打印结果
     */
    private void printResult() {
        StringBuilder sb = new StringBuilder("MarsTask.onRequestResult");
        sb.append(" retry=");
        sb.append(mRetryCount);
        sb.append(" path=");
        sb.append(getScheme());
        sb.append(getHost());
        sb.append(getPath());
        sb.append("\nresult=[");
        sb.append(mResponseBean);
        sb.append("]");
        Log.i("JarvaZ", sb.toString());
    }

    private void retryRequest() {
        //TODO 重新请求
        RequestExecutor.getInstance().startRequest(this);
    }

    private boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    private void printParams() {
        StringBuilder sb = new StringBuilder("HttpTask.onRequestResult");
        sb.append(" retry=");
        sb.append(mRetryCount);
        sb.append(" path=");
        sb.append(getScheme());
        sb.append(getHost());
        sb.append(getPath());

    }

    protected ParameterizedType getType(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
}
