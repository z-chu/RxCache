package com.zchu.sample;


import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ServerAPI {
    String BASE_URL = "https://api.douban.com";

    @GET("/v2/movie/in_theaters?city=上海")
    Observable<Movie> getInTheatersMovies();

}
