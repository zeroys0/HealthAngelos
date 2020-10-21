package com.pattonsoft.pattonutil1_0.net;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 *
 * Created by zhao on 2016/12/30.
 */

public interface BitmapService {
    //下载文件
    @GET
    Observable<ResponseBody> getImg(@Url String fileUrl);
    //下载文件
    @GET
    Observable<ResponseBody> getfile(@Url String fileUrl);
}
