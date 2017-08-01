package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;
import com.zchu.rxcache.utils.LogUtils;

import java.lang.reflect.Type;
import java.util.ConcurrentModificationException;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public abstract class BaseStrategy implements IStrategy {


    protected  <T> Observable<CacheResult<T>> loadCache(final RxCache rxCache, final String key, Type type) {
        return rxCache
                .<T>load(key, type)
                .map(new Func1<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(T o) {
                        LogUtils.debug("loadCache result=" + o);
                        return new CacheResult<>(ResultFrom.Cache, key, o);
                    }
                });
    }

    protected <T> Observable<CacheResult<T>> loadRemote(final RxCache rxCache, final String key, Observable<T> source, final CacheTarget target) {
        return source
                .map(new Func1<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(T t) {
                        LogUtils.debug("loadRemote result=" + t);
                        rxCache.save(key, t, target).subscribeOn(Schedulers.io())
                                .subscribe(new Subscriber<Boolean>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        if (e instanceof ConcurrentModificationException) {
                                            LogUtils.log("Save failed, please use a synchronized cache strategy :", e);
                                        } else {
                                            LogUtils.log(e);
                                        }
                                    }

                                    @Override
                                    public void onNext(Boolean status) {
                                        LogUtils.debug("save status => " + status);
                                    }
                                });
                        return new CacheResult<>(ResultFrom.Remote, key, t);
                    }
                });
    }

    protected <T> Observable<CacheResult<T>> loadRemoteSync(final RxCache rxCache, final String key, Observable<T> source, final CacheTarget target) {
        return source
                .flatMap(new Func1<T, Observable<CacheResult<T>>>() {
                    @Override
                    public Observable<CacheResult<T>> call(final T t) {
                        return saveCacheSync(rxCache, key, t, target);
                    }
                });

    }

    protected <T> Observable<CacheResult<T>> saveCacheSync(RxCache rxCache, final String key, final T t, CacheTarget target) {
        return rxCache.save(key, t, target)
                .map(new Func1<Boolean, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(Boolean aBoolean) {
                        return new CacheResult<>(ResultFrom.Remote, key, t);
                    }
                })
                .onErrorReturn(new Func1<Throwable, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(Throwable throwable) {
                        return new CacheResult<>(ResultFrom.Remote, key, t);
                    }
                });

    }

    @Override
    public abstract <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type);
}
