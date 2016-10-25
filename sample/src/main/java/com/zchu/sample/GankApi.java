package com.zchu.sample;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Chu on 2016/10/25.
 */

public interface GankApi {
    String BASE_URL = "http://gank.io/api/";

    @GET("history/content/20/{page}")
    Observable<GankBean> getHistoryGank(@Path("page") int page);
}
