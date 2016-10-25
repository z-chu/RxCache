package com.zchu.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zchu.log.Logger;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.diskconverter.SerializableDiskConverter;
import com.zchu.rxcache.stategy.CacheStrategy;
import com.zchu.sample.utils.MD5;

import java.io.File;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GankApi gankApi;

    private RxCache rxCache;
    private Button btnLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLoad = (Button) findViewById(R.id.btn_load);
        btnLoad.setOnClickListener(this);
        gankApi = new Retrofit.Builder()
                .baseUrl(GankApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build()
                .create(GankApi.class);
        rxCache = new RxCache.Builder()
                .appVersion(2)
                .diskDir(new File(getCacheDir().getPath() + File.separator + "data-cache"))
                .diskConverter(new SerializableDiskConverter())
                .build();
        Logger.init("RxCache");
    }

    @Override
    public void onClick(View view) {
        gankApi.getHistoryGank(1)
                .compose(rxCache.<GankBean>transformer(MD5.getMessageDigest("custom_key"), CacheStrategy.cacheAndRemote()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CacheResult<GankBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onNext(CacheResult<GankBean> gankBeanCacheResult) {
                        Logger.e(gankBeanCacheResult);
                     //   Logger.e(gankBeanCacheResult.data);
                    }
                });
    }
}
