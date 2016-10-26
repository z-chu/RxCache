package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import rx.Observable;
import rx.functions.Func1;

/**
 * 先缓存，后网络
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
 class CacheAndRemoteStrategy extends BaseStrategy{
    private CacheAndRemoteStrategy(){}
    public static CacheAndRemoteStrategy INSTANCE=new CacheAndRemoteStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source) {
        Observable<CacheResult<T>> cache = loadCache(rxCache,key);
        Observable<CacheResult<T>> remote = loadRemote(rxCache,key, source, CacheTarget.MemoryAndDisk);
        return Observable.concat(cache, remote)
                .filter(new Func1<CacheResult<T>, Boolean>() {
                    @Override
                    public Boolean call(CacheResult<T> result) {
                        return result.data != null;
                    }
                });
    }
}
