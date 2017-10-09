package com.zchu.rxcache.stategy;


import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.Flowable;
import io.reactivex.Observable;


/**
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public interface IStrategy {

    <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type);

    <T> Publisher<CacheResult<T>> flow(RxCache rxCache, String key, Flowable<T> source, Type type);
}
