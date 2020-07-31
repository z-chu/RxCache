package com.zchu.rxcache.stategy;

import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;


/**
 * Created by Chu on 2017/6/24.
 * 仅加载网络，不缓存
 */

public final class NoneStrategy implements IStrategy  {

    private NoneStrategy() {
    }

    static final NoneStrategy INSTANCE = new NoneStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, final String key, Observable<T> source, Type type) {
        return source.map(new Function<T, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(@NonNull T t) throws Exception {
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        });
    }

    @Override
    public <T> Publisher<CacheResult<T>> flow(RxCache rxCache, final String key, Flowable<T> source, Type type) {
        return source.map(new Function<T, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(@NonNull T t) throws Exception {
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        });
    }
}
