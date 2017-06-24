package com.zchu.rxcache.stategy;

import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;

import java.lang.reflect.Type;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Chu on 2017/6/24.
 * 仅加载网络，不缓存
 */

 class NoneStrategy implements IStrategy {

    private NoneStrategy(){}

    public static final  NoneStrategy INSTANCE=new NoneStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, final String key, Observable<T> source, Type type) {

        return source.map(new Func1<T, CacheResult<T>>() {
            @Override
            public CacheResult<T> call(T t) {
                return new CacheResult<>(ResultFrom.Remote, key, t);
            }
        });
    }
}
