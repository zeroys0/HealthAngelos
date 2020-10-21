package com.pattonsoft.pattonutil1_0.net;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Post 请求
 * Created by zhao on 2016/12/21.
 */

public interface PostService {

    /**
     * @param url  请求api
     * @param Body 请求体
     * @return
     */
    @POST()
    Observable<HttpResult<Map<String, Object>>> PostWithMapBack(@Url String url, @Body RequestBody Body);

    /**
     * @param url  请求api
     * @param Body 请求体
     * @return
     */
    @POST()
    Observable<String> PostWithStringBack(@Url String url, @Body RequestBody Body);

    /**
     * @param url  请求api
     * @param Body 请求体
     * @return
     */
    @POST()
    Observable<ResponseBody> PostWithResponseBodyBack(@Url String url, @Body RequestBody Body);

}
