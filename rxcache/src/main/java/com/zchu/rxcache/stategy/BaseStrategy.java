package com.zchu.rxcache.stategy;

import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.utils.LogUtils;
import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
abstract class BaseStrategy implements IStrategy {


    <T> Observable<CacheResult<T>> loadCache(final RxCache rxCache, final String key) {
        return rxCache
                .<T>load(key)
                .map(new Func1<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(T o) {
                        LogUtils.debug("loadCache result=" + o);
                        return new CacheResult<>(ResultFrom.Cache, key,  o);
                    }
                });
    }

     <T> Observable<CacheResult<T>> loadRemote(final RxCache rxCache, final String key, Observable<T> source, final CacheTarget target) {
        return source
                .map(new Func1<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(T t) {
                        LogUtils.debug("loadRemote result=" + t);
                        rxCache.save(key, t,target).subscribeOn(Schedulers.io())
                                .subscribe(new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean status) {
                                        LogUtils.debug("save status => " + status);
                                    }
                                });
                        return new CacheResult<>(ResultFrom.Remote, key, t);
                    }
                });
    }

    @Override
    public abstract <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source);
}
