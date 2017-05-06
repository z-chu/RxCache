package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import rx.Observable;
import rx.functions.Func1;

/**
 * 优先网络
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
final class FirstRemoteStrategy extends BaseStrategy {
    private FirstRemoteStrategy() {
    }

    public static final FirstRemoteStrategy INSTANCE = new FirstRemoteStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source) {
        Observable<CacheResult<T>> cache = loadCache(rxCache, key);
        Observable<CacheResult<T>> remote = loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk)
                .onErrorReturn(new Func1<Throwable, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(Throwable throwable) {
                        return null;
                    }
                });
        return Observable.concat(remote, cache)
                .firstOrDefault(null, new Func1<CacheResult<T>, Boolean>() {
                    @Override
                    public Boolean call(CacheResult<T> result) {
                        return result != null && result.getData() != null;
                    }
                });

    }
}
