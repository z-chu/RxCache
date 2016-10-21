package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *
 * 优先缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
 class FirstCacheStategy extends BaseStrategy{
    private FirstCacheStategy(){
    }
    public static FirstCacheStategy INSTANCE=new FirstCacheStategy();
    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source) {
        Observable<CacheResult<T>> cache = loadCache(rxCache,key);
        cache.onErrorReturn(new Func1<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> call(Throwable throwable) {
                return null;
            }
        });
        Observable<CacheResult<T>> remote = loadRemote(rxCache,key, source, CacheTarget.MemoryAndDisk);
        return Observable.concat(cache, remote)
                .firstOrDefault(null, new Func1<CacheResult<T>, Boolean>() {
                    @Override
                    public Boolean call(CacheResult<T> tResultData) {
                        return tResultData != null && tResultData.data != null;
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}
