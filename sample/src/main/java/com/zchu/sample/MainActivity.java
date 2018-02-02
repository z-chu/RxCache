package com.zchu.sample;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.zchu.log.Logger;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;
import com.zchu.rxcache.diskconverter.GsonDiskConverter;
import com.zchu.rxcache.stategy.CacheStrategy;
import com.zchu.rxcache.stategy.FirstCacheTimeoutStrategy;
import com.zchu.rxcache.stategy.IStrategy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ServerAPI serverAPI;
    private RxCache rxCache;
    private TextView tvData;
    private Disposable mSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvData = findViewById(R.id.tv_data);
        bindOnClickLister(
                R.id.btn_first_remote,
                R.id.btn_first_cache,
                R.id.btn_first_cache_timeout,
                R.id.btn_only_remote,
                R.id.btn_only_cache,
                R.id.btn_cache_and_remote,
                R.id.btn_none,
                R.id.btn_clean_cache
        );
        serverAPI = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build()
                .create(ServerAPI.class);
        rxCache = new RxCache.Builder()
                .appVersion(2)
                .diskDir(new File(getCacheDir().getPath() + File.separator + "data-cache"))
                .diskConverter(new GsonDiskConverter())
                .diskMax(20 * 1024 * 1024)
                .setDebug(true)
                .build();
        Logger.init("RxCache");
    }

    public void bindOnClickLister(@IdRes int... ids) {
        for (int id : ids) {
            View view = findViewById(id);
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_first_remote:
                loadData(CacheStrategy.firstRemote());
                break;
            case R.id.btn_first_cache:
                loadData(CacheStrategy.firstCache());
                break;
            case R.id.btn_first_cache_timeout:
                loadData(CacheStrategy.firstCacheTimeout(5000));
                break;
            case R.id.btn_only_remote:
                loadData(CacheStrategy.onlyRemote());
                break;
            case R.id.btn_only_cache:
                loadData(CacheStrategy.onlyCache());
                break;
            case R.id.btn_cache_and_remote:
                loadData(CacheStrategy.cacheAndRemote());
                break;
            case R.id.btn_none:
                loadData(CacheStrategy.none());
                break;

            case R.id.btn_clean_cache:
                rxCache.clear().subscribe();
                tvData.setText("数据");
                break;
        }


    }

    private void loadData(IStrategy strategy) {
        if (mSubscription != null && !mSubscription.isDisposed()) {
            mSubscription.dispose();
        }
        tvData.setText("加载中...");
        serverAPI.getInTheatersMovies()
                .map(new Function<Movie, List<Movie.SubjectsBean>>() {
                    @Override
                    public List<Movie.SubjectsBean> apply(Movie movie) throws Exception {
                        return movie.subjects;
                    }
                })
                //泛型这样使用
                .compose(rxCache.< List<Movie.SubjectsBean>>transformObservable("custom_key", new TypeToken< List<Movie.SubjectsBean>>() {}.getType(), strategy))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CacheResult<List<Movie.SubjectsBean>>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mSubscription = disposable;
                    }

                    @Override
                    public void onNext(CacheResult<List<Movie.SubjectsBean>> listCacheResult) {
                        Logger.e(listCacheResult);
                        if (ResultFrom.ifFromCache(listCacheResult.getFrom())) {
                            tvData.setText("来自缓存： 写入时间" + listCacheResult.getTimestamp() + "\n " + listCacheResult.getData());
                        } else {
                            tvData.setText("来自网络：\n " + listCacheResult.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        tvData.setText(throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
