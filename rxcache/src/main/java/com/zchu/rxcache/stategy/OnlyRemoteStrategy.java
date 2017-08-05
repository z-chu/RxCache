package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.Observable;


/**
 * 仅加载网络，但数据依然会被缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
class OnlyRemoteStrategy extends BaseStrategy {
    private boolean isSync;

    public OnlyRemoteStrategy() {
        isSync = false;
    }

    public OnlyRemoteStrategy(boolean isSync) {
        this.isSync = isSync;
    }

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        if (isSync) {
            return loadRemoteSync(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        } else {
            return loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
    }
}
