package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;

import rx.Observable;
import rx.functions.Func1;

/**
 * 优先缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public final class FirstCacheStrategy extends BaseStrategy {
    private boolean isSync;

    public FirstCacheStrategy() {
        isSync = false;
    }

    public FirstCacheStrategy(boolean isSync) {
        this.isSync = isSync;
    }

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = loadCache(rxCache, key, type);
        cache = cache.filter(new Func1<CacheResult<T>, Boolean>() {
            @Override
            public Boolean call(CacheResult<T> result) {
                return result != null && result.getData() != null;
            }
        });
        Observable<CacheResult<T>> remote;
        if (isSync) {
            remote = loadRemoteSync(rxCache, key, source, CacheTarget.MemoryAndDisk);
        } else {
            remote = loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk);
        }
        return Observable
                .concat(cache, remote)
                .take(1);
    }
}
