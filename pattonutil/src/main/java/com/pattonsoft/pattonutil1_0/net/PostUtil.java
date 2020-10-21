package com.pattonsoft.pattonutil1_0.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RXJava + Retrofit2 实现 Post表单请求
 * Created by zhao on 2016/12/16.
 */

public class PostUtil {
    //请求参数封装 带文件
    public static RequestBody getRequestBody(Map<String, String> map, String[] keys, File[] files) {
        //表单提交
        MultipartBody.Builder build = new MultipartBody.Builder();
        build.setType(MultipartBody.FORM);
        //一般参数 键值对
        for (Map.Entry<String, String> entry : map.entrySet()) {
            build.addFormDataPart(entry.getKey(), entry.getValue());
        }
        // 文件
        for (int i = 0; i < keys.length; i++) {
            build.addFormDataPart(keys[i], files[i].getName(), RequestBody.create(MediaType.parse("multipart/form-data"), files[i]));
        }
        RequestBody requestBody = build.build();
        return requestBody;
    }

    //请求参数封装
    public static RequestBody getRequestBody(Map<String, String> map) {
        //表单提交
        MultipartBody.Builder build = new MultipartBody.Builder();
        build.setType(MultipartBody.FORM);
        //一般参数 键值对
        for (Map.Entry<String, String> entry : map.entrySet()) {
            build.addFormDataPart(entry.getKey(), entry.getValue());
        }

        RequestBody requestBody = build.build();
        return requestBody;
    }

    //Post请求封装 返回HttpResult
    public static void PostWithMapBack(String baseUrl, String postUrl, Map<String, String> map, final CallBack<HttpResult<Map<String, Object>>> back) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        PostService service = retrofit.create(PostService.class);
        RequestBody requestBody = getRequestBody(map);
        service.PostWithMapBack(postUrl, requestBody)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<HttpResult<Map<String, Object>>>() {

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        back.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        //请求开始
                        back.onComplete();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        back.onSubscribe(d);
                    }

                    @Override
                    public void onNext(HttpResult<Map<String, Object>> mapHttpResult) {
                        back.onSuccess(mapHttpResult);
                    }
                });
    }

    //Post请求封装（含文件） 返回HttpResult
    public static void PostWithMapBack(String baseUrl, String postUrl, Map<String, String> map, String[] keys, File[] files, final CallBack<HttpResult<Map<String, Object>>> back) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        PostService service = retrofit.create(PostService.class);
        RequestBody requestBody = getRequestBody(map, keys, files);
        service.PostWithMapBack(postUrl, requestBody)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<HttpResult<Map<String, Object>>>() {

                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        back.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        //请求开始
                        back.onComplete();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        back.onSubscribe(d);
                    }

                    @Override
                    public void onNext(HttpResult<Map<String, Object>> mapHttpResult) {
                        back.onSuccess(mapHttpResult);
                    }
                });
    }

    //Post请求封装 返回String
    public static void PostWithStringBack(String baseUrl, String postUrl, Map<String, String> map, final CallBack<String> back) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        PostService service = retrofit.create(PostService.class);
        RequestBody requestBody = getRequestBody(map);
        service.PostWithStringBack(postUrl, requestBody)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        back.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        //请求开始
                        back.onComplete();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        back.onSubscribe(d);
                    }

                    @Override
                    public void onNext(String mapHttpResult) {
                        back.onSuccess(mapHttpResult);
                    }
                });
    }

    //Post请求封装（含文件）返回String
    public static void PostWithStringBack(String baseUrl, String postUrl, Map<String, String> map, String[] keys, File[] files, final CallBack<String> back) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        PostService service = retrofit.create(PostService.class);
        RequestBody requestBody = getRequestBody(map, keys, files);
        service.PostWithStringBack(postUrl, requestBody)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        back.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        //请求开始
                        back.onComplete();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        back.onSubscribe(d);
                    }

                    @Override
                    public void onNext(String mapHttpResult) {
                        back.onSuccess(mapHttpResult);
                    }
                });
    }

    //Post请求封装 返回ResponseBody
    public static void PostWithResponseBodyBack(String baseUrl, String postUrl, Map<String, String> map, final CallBack<ResponseBody> back) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        PostService service = retrofit.create(PostService.class);
        RequestBody requestBody = getRequestBody(map);
        service.PostWithResponseBodyBack(postUrl, requestBody)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        back.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        //请求开始
                        back.onComplete();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        back.onSubscribe(d);
                    }

                    @Override
                    public void onNext(ResponseBody mapHttpResult) {
                        back.onSuccess(mapHttpResult);
                    }
                });
    }

    //Post请求封装 （含文件） 返回ResponseBody
    public static void PostWithResponseBodyBack(String baseUrl, String postUrl, Map<String, String> map, String[] keys, File[] files, final CallBack<ResponseBody> back) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        PostService service = retrofit.create(PostService.class);
        RequestBody requestBody = getRequestBody(map, keys, files);
        service.PostWithResponseBodyBack(postUrl, requestBody)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onError(Throwable e) {
                        //请求失败
                        back.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        //请求开始
                        back.onComplete();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        back.onSubscribe(d);
                    }

                    @Override
                    public void onNext(ResponseBody mapHttpResult) {
                        back.onSuccess(mapHttpResult);
                    }
                });
    }

    //返回结果回调
    public interface CallBack<T> {

        void onSubscribe(Disposable d);

        void onComplete();

        void onError(Throwable e);

        void onSuccess(T s);
    }

    public static void getBitmap(String baseUrl, String Img, final CallBack<Bitmap> back) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                //     .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        BitmapService bitmapService = retrofit.create(BitmapService.class);
        bitmapService.getImg(Img)
                .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .map(new Function<ResponseBody, Bitmap>() {

                    @Override
                    public Bitmap apply(ResponseBody responseBody) throws Exception {
                        InputStream is = null;
                        try {
                            is = responseBody.byteStream();
                        } finally {
                            try {
                                if (is != null) {
                                    is.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (is != null) {
                            return BitmapFactory.decodeStream(is);
                        }
                        return null;
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())//在Android主线程中展示
                .subscribe(new Observer<Bitmap>() {

                    @Override
                    public void onError(Throwable arg0) {
                        back.onError(arg0);
                    }

                    @Override
                    public void onComplete() {
                        back.onComplete();
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        back.onSubscribe(d);
                    }

                    @Override
                    public void onNext(Bitmap arg0) {
                        back.onSuccess(arg0);

                    }
                });
    }

}
