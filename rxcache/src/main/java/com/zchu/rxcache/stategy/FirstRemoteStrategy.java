package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;
import java.util.Arrays;

import io.reactivex.Observable;

/**
 * 优先网络
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public final class FirstRemoteStrategy extends BaseStrategy {
    private boolean isSync;

    public FirstRemoteStrategy() {
        isSync = false;
    }

    public FirstRemoteStrategy(boolean isSync) {
        this.isSync = isSync;
    }

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = loadCache(rxCache, key, type, true);
        Observable<CacheResult<T>> remote;
        if (isSync) {
            remote = loadRemoteSync(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        } else {
            remote = loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
        return Observable
                .concatDelayError(Arrays.asList(cache,remote))
                .take(1);
    }
}
