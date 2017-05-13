package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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
                .onErrorReturn(new Function<Throwable, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> apply(@NonNull Throwable throwable) throws Exception {
                        return null;
                    }
                });
        return Observable.concat(remote, cache)
                .filter(new Predicate<CacheResult<T>>() {
                    @Override
                    public boolean test(@NonNull CacheResult<T> result) throws Exception {
                        return result != null && result.getData() != null;
                    }
                }).firstElement().toObservable();
    }
}
