package com.zchu.rxcache.stategy;

import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.data.CacheResult;

import rx.Observable;

/**
 * 仅加载网络，但数据依然会被缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
 class OnlyRemoteStrategy extends BaseStrategy{
    private OnlyRemoteStrategy(){}

    public static final  OnlyRemoteStrategy INSTANCE=new OnlyRemoteStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source,Class<T> classOf) {
        return loadRemote(rxCache,key, source, CacheTarget.MemoryAndDisk);
    }
}
