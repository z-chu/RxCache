package com.zchu.rxcache.stategy;

import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;

import rx.Observable;
import rx.functions.Func1;

/**
 * 仅加载缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public final class OnlyCacheStrategy extends BaseStrategy {

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache= loadCache(rxCache, key, type);

        return   cache.flatMap(new Func1<CacheResult<T>, Observable<CacheResult<T>>>() {
            @Override
            public Observable<CacheResult<T>> call(CacheResult<T> result) {
                if(result==null||result.getData()==null){
                    return Observable.error(new NullPointerException("Not find the key corresponding to the cache"));
                }
                return Observable.just(result);
            }
        });
    }
}
