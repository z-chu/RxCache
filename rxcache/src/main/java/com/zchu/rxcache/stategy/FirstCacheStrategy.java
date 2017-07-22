package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * 优先缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
class FirstCacheStrategy extends BaseStrategy {
    private FirstCacheStrategy() {
    }

    static FirstCacheStrategy INSTANCE = new FirstCacheStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = loadCache(rxCache, key, type,true);
        Observable<CacheResult<T>> remote = loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk,false);
        return cache.switchIfEmpty(remote);
    }
}
