package com.zchu.sample;


import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface ServerAPI {
    String BASE_URL = "https://api.github.com";

    @GET("/users")
    Observable<String> fetchUsers();

}
