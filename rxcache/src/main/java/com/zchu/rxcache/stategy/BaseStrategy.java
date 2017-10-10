package com.zchu.rxcache.stategy;

import com.zchu.rxcache.CacheTarget;
import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;
import com.zchu.rxcache.utils.LogUtils;

import java.lang.reflect.Type;
import java.util.ConcurrentModificationException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public abstract class BaseStrategy implements IStrategy {

    protected <T> Observable<CacheResult<T>> loadCache(final RxCache rxCache, final String key, Type type, final boolean needEmpty) {
        Observable<CacheResult<T>> observable = rxCache
                .<T>load(key, type)
                .flatMap(new Function<T, ObservableSource<CacheResult<T>>>() {
                    @Override
                    public ObservableSource<CacheResult<T>> apply(@NonNull T t) throws Exception {
                        if (t == null) {
                            return Observable.error(new NullPointerException("Not find the key corresponding to the cache"));
                        }
                        return Observable.just(new CacheResult<>(ResultFrom.Cache, key, t));
                    }
                });
        if (needEmpty) {
            observable = observable
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                        @Override
                        public ObservableSource<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                            return Observable.empty();
                        }
                    });
        }
        return observable;
    }

    protected <T> Observable<CacheResult<T>> loadRemote(final RxCache rxCache, final String key, Observable<T> source, final CacheTarget target, final boolean needEmpty) {
        Observable<CacheResult<T>> observable = source
                .map(new Function<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> apply(@NonNull T t) throws Exception {
                        LogUtils.debug("loadRemote result=" + t);
                        rxCache.save(key, t, target)
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        new Consumer<Boolean>() {
                                            @Override
                                            public void accept(@NonNull Boolean status) throws Exception {
                                                LogUtils.debug("save status => " + status);
                                            }
                                        },
                                        new Consumer<Throwable>() {
                                            @Override
                                            public void accept(@NonNull Throwable throwable) throws Exception {
                                                if (throwable instanceof ConcurrentModificationException) {
                                                    LogUtils.log("Save failed, please use a synchronized cache strategy :", throwable);
                                                } else {
                                                    LogUtils.log(throwable);
                                                }
                                            }
                                        });
                        return new CacheResult<>(ResultFrom.Remote, key, t);
                    }
                });
        if (needEmpty) {
            observable = observable
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                        @Override
                        public ObservableSource<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                            return Observable.empty();
                        }
                    });
        }
        return observable;
    }


    protected <T> Observable<CacheResult<T>> loadRemoteSync(final RxCache rxCache, final String key, Observable<T> source, final CacheTarget target, final boolean needEmpty) {
        Observable<CacheResult<T>> observable = source
                .flatMap(new Function<T, ObservableSource<CacheResult<T>>>() {
                    @Override
                    public ObservableSource<CacheResult<T>> apply(@NonNull T t) throws Exception {
                        return saveCacheSync(rxCache, key, t, target);
                    }
                });
        if (needEmpty) {
            observable = observable.onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                @Override
                public ObservableSource<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                    return Observable.empty();
                }
            });
        }
        return observable;

    }

    protected <T> Observable<CacheResult<T>> saveCacheSync(RxCache rxCache, final String key, final T t, CacheTarget target) {
        return rxCache.save(key, t, target)
                .map(new Function<Boolean, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> apply(@NonNull Boolean aBoolean) throws Exception {
                        return new CacheResult<>(ResultFrom.Remote, key, t);
                    }
                })
                .onErrorReturn(new Function<Throwable, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> apply(@NonNull Throwable throwable) throws Exception {
                        return new CacheResult<>(ResultFrom.Remote, key, t);
                    }
                });
    }

    @Override
    public abstract <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type);
}
