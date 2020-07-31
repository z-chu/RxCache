package com.zchu.rxcache.stategy;


import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Observable;


/**
 * author : zchu
 * date   : 2017/10/11
 * desc   :
 */
public interface IObservableStrategy {

    <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type);
}
