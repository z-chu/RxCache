package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

/**
 * 先缓存，后网络
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
class CacheAndRemoteStrategy extends BaseStrategy {
    private CacheAndRemoteStrategy() {
    }

    static CacheAndRemoteStrategy INSTANCE = new CacheAndRemoteStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = loadCache(rxCache, key, type,true);
        Observable<CacheResult<T>> remote = loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk,true);
        return Observable.concat(cache, remote)
                .filter(new Predicate<CacheResult<T>>() {
                    @Override
                    public boolean test(@NonNull CacheResult<T> result) throws Exception {
                        return result != null && result.getData() != null;
                    }
                });
    }
}
