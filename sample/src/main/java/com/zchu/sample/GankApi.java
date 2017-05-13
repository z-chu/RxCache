package com.zchu.sample;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Chu on 2016/10/25.
 */

public interface GankApi {
    String BASE_URL = "http://gank.io/api/";

    @GET("history/content/3/{page}")
    Observable<GankBean> getHistoryGank(@Path("page") int page);
}
