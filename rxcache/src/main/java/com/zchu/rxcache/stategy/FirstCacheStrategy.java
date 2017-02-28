package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import rx.Observable;
import rx.functions.Func1;

/**
 * 优先缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
class FirstCacheStrategy extends BaseStrategy {
    private FirstCacheStrategy() {
    }

    public static FirstCacheStrategy INSTANCE = new FirstCacheStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Class<T> classOf) {
        Observable<CacheResult<T>> cache = loadCache(rxCache, key, classOf);
        cache.onErrorReturn(new Func1<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> call(Throwable throwable) {
                return null;
            }
        });
        Observable<CacheResult<T>> remote = loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk);
        return Observable.concat(cache, remote)
                .firstOrDefault(null, new Func1<CacheResult<T>, Boolean>() {
                    @Override
                    public Boolean call(CacheResult<T> tResultData) {
                        return tResultData != null && tResultData.data != null;
                    }
                });
    }
}
