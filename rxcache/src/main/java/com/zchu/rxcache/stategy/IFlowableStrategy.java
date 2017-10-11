package com.zchu.rxcache.stategy;


import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.Flowable;


/**
 * author : zchu
 * date   : 2017/10/11
 * desc   :
 */
public interface IFlowableStrategy {

    <T> Publisher<CacheResult<T>> flow(RxCache rxCache, String key, Flowable<T> source, Type type);
}
