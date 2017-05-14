package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * 优先缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
class FirstCacheStrategy extends BaseStrategy {
    private FirstCacheStrategy() {
    }

    public static FirstCacheStrategy INSTANCE = new FirstCacheStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source) {
        Observable<CacheResult<T>> cache = loadCache(rxCache, key);
        cache.onErrorReturn(new Function<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> apply(@NonNull Throwable throwable) throws Exception {
                return null;
            }
        });
        Observable<CacheResult<T>> remote = loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk);
        return Observable.concat(cache, remote)
                .filter(new Predicate<CacheResult<T>>() {
                    @Override
                    public boolean test(@NonNull CacheResult<T> result) throws Exception {
                        return result != null && result.getData() != null;
                    }
                }).firstElement().toObservable();
    }
}
