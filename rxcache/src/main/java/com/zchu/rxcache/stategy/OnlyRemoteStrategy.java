package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.RxCacheHelper;
import com.zchu.rxcache.data.CacheResult;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;


/**
 * 仅加载网络，但数据依然会被缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
class OnlyRemoteStrategy implements IStrategy {
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
            return RxCacheHelper.loadRemoteSync(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        } else {
            return RxCacheHelper.loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
    }

    @Override
    public <T> Publisher<CacheResult<T>> flow(RxCache rxCache, String key, Flowable<T> source, Type type) {
        if (isSync) {
            return RxCacheHelper.loadRemoteSyncFlowable(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        } else {
            return RxCacheHelper.loadRemoteFlowable(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
    }
}
