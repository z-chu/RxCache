package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * 优先网络
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
final class FirstRemoteStrategy extends BaseStrategy {
    private FirstRemoteStrategy() {
    }

    static final FirstRemoteStrategy INSTANCE = new FirstRemoteStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = loadCache(rxCache, key, type, false);
        Observable<CacheResult<T>> remote = loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk, true);
        return remote.switchIfEmpty(cache);
    }
}
