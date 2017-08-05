package com.zchu.rxcache.stategy;

import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import java.lang.reflect.Type;

import rx.Observable;

/**
 * 仅加载缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
public final class OnlyCacheStrategy extends BaseStrategy {

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        return loadCache(rxCache, key, type);
    }
}
