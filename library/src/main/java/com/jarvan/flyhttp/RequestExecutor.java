package com.jarvan.flyhttp;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 创建日期：2018/8/4 on 下午3:20
 * 描述: 网络请求执行器
 * 作者:张冰
 */
public class RequestExecutor {
    private static RequestExecutor instance;
    private ExecutorService mThreadPool = Executors.newFixedThreadPool(3);
    private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    private DataRequest dataRequest = new DataRequest();

    private RequestExecutor() {
    }

    public synchronized static RequestExecutor getInstance() {
        if (instance == null) {
            instance = new RequestExecutor();
        }
        return instance;
    }

    public void startRequest(TaskProtocol task) {
        startRequest(mThreadPool, task);
    }

    private void startRequest(ExecutorService threadPool, final TaskProtocol task) {
        Observable.just(task).subscribeOn(Schedulers.from(threadPool))
                .map(new Function<TaskProtocol, TaskProtocol>() {
                    @Override
                    public TaskProtocol apply(TaskProtocol task) throws Exception {
                        task.onStartRequest();
                        execRequest(task);
                        task.onEndRequest();
                        return task;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TaskProtocol>() {
                    @Override
                    public void accept(TaskProtocol taskProtocol) throws Exception {
                        try {
                            task.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    private void execRequest(TaskProtocol task) throws Exception {
        byte[] result = null;
        try {
            result = dataRequest.startRequest(task.getUrl(), task.getParams());
        } catch (Exception e) {
            ResponseBean bean = new ResponseBean();
            bean.setCode(0);
            bean.setMsg("网络异常，请检查网络");
            bean.setErrlog(e.toString());
            analysisData(task, new Gson().toJson(bean).getBytes());
            e.printStackTrace();
            e.printStackTrace();
        }
        analysisData(task, result);
    }

    public void analysisData(TaskProtocol task, byte[] source) throws Exception {
        Reader reader = new InputStreamReader(new ByteArrayInputStream(source));
        task.onRequestResult(reader);
        reader.close();
    }
}
