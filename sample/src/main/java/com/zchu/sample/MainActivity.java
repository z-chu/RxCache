package com.zchu.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zchu.log.Logger;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;
import com.zchu.rxcache.diskconverter.GsonDiskConverter;
import com.zchu.rxcache.stategy.CacheStrategy;
import com.zchu.rxcache.stategy.IStrategy;

import java.io.File;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GankApi gankApi;

    private RxCache rxCache;


    private Button btnFirstRemote;
    private Button btnFirstCache;
    private Button btnOnlyRemote;
    private Button btnOnlyCache;
    private Button btnCacheAndRemote;
    private TextView tvData;

    private Subscription mSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvData = (TextView) findViewById(R.id.tv_data);
        btnFirstRemote = (Button) findViewById(R.id.btn_first_remote);
        btnFirstCache = (Button) findViewById(R.id.btn_first_cache);
        btnOnlyRemote = (Button) findViewById(R.id.btn_only_remote);
        btnOnlyCache = (Button) findViewById(R.id.btn_only_cache);
        btnCacheAndRemote = (Button) findViewById(R.id.btn_cache_and_remote);
        btnFirstRemote.setOnClickListener(this);
        btnFirstCache.setOnClickListener(this);
        btnOnlyRemote.setOnClickListener(this);
        btnOnlyCache.setOnClickListener(this);
        btnCacheAndRemote.setOnClickListener(this);

        gankApi = new Retrofit.Builder()
                .baseUrl(GankApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new OkHttpClient.Builder().build())
                .build()
                .create(GankApi.class);
        rxCache = new RxCache.Builder()
                .appVersion(1)
                .diskDir(new File(getCacheDir().getPath() + File.separator + "data-cache"))
                .diskConverter(new GsonDiskConverter())
                .diskMax(20 * 1024 * 1024)
                .memoryMax(1*1024*1024)
                .build();
        Logger.init("RxCache");
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
            case R.id.btn_only_remote:
                loadData(CacheStrategy.onlyRemote());
                break;
            case R.id.btn_only_cache:
                loadData(CacheStrategy.onlyCache());
                break;
            case R.id.btn_cache_and_remote:
                loadData(CacheStrategy.cacheAndRemote());
                break;
        }


    }

    private void loadData(IStrategy strategy) {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        tvData.setText("加载中...");
        mSubscription = gankApi.getHistoryGank(1)
                .map(new Func1<GankBean,  List<GankBean.ResultsBean>>() {
                    @Override
                    public List<GankBean.ResultsBean> call(GankBean gankBean) {
                        return gankBean.getResults();
                    }
                })
                .compose(rxCache.<List<GankBean.ResultsBean>>transformer("custom_key", new TypeToken<List<GankBean.ResultsBean>>() {}.getType(), strategy))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CacheResult<List<GankBean.ResultsBean>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        tvData.setText(e.getMessage());
                    }

                    @Override
                    public void onNext(CacheResult<List<GankBean.ResultsBean>> listCacheResult) {
                        List<GankBean.ResultsBean> data = listCacheResult.getData();
                        Logger.e(data);
                        if (listCacheResult.getFrom() == ResultFrom.Cache) {
                            tvData.setText("来自缓存：\n" + listCacheResult.toString());
                        } else {
                            tvData.setText("来自网络：\n" + listCacheResult.toString());
                        }

                    }
                });
    }
}
