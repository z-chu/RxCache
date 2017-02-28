package com.zchu.rxcache.stategy;


import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import rx.Observable;

/**
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public interface IStrategy {

    <T> rx.Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Class<T> classOf);

}
