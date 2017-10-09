package com.zchu.rxcache;

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
 * author : zchu
 * date   : 2017/10/9
 * desc   :
 */

public class RxCacheHelper {

    public static <T> Observable<CacheResult<T>> loadCache(final RxCache rxCache, final String key, Type type, final boolean needEmpty) {
        return rxCache
                .<T>load(key, type)
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                    @Override
                    public ObservableSource<? extends T> apply(@NonNull Throwable throwable) throws Exception {
                        if (needEmpty) {
                            return Observable.empty();
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                })
                .map(new Function<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> apply(@NonNull T o) throws Exception {
                        LogUtils.debug("loadCache result=" + o);
                        return new CacheResult<>(ResultFrom.Cache, key, o);
                    }
                });
    }

    public static <T> Observable<CacheResult<T>> loadRemote(final RxCache rxCache, final String key, Observable<T> source, final CacheTarget target, final boolean needEmpty) {
        return source
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                    @Override
                    public ObservableSource<? extends T> apply(@NonNull Throwable throwable) throws Exception {
                        if (needEmpty) {
                            return Observable.empty();
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                })
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
    }


    public static <T> Observable<CacheResult<T>> loadRemoteSync(final RxCache rxCache, final String key, Observable<T> source, final CacheTarget target, final boolean needEmpty) {
        return source
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends T>>() {
                    @Override
                    public ObservableSource<? extends T> apply(@NonNull Throwable throwable) throws Exception {
                        if (needEmpty) {
                            return Observable.empty();
                        } else {
                            return Observable.error(throwable);
                        }
                    }
                })
                .flatMap(new Function<T, ObservableSource<CacheResult<T>>>() {
                    @Override
                    public ObservableSource<CacheResult<T>> apply(@NonNull T t) throws Exception {
                        return saveCacheSync(rxCache, key, t, target);
                    }
                });
    }

    public static <T> Observable<CacheResult<T>> saveCacheSync(RxCache rxCache, final String key, final T t, CacheTarget target) {
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

}
