package com.zchu.rxcache.stategy;

import com.zchu.rxcache.RxCache;
import com.zchu.rxcache.data.CacheResult;

import rx.Observable;

/**
 * 仅加载缓存
 * 作者: 赵成柱 on 2016/9/12 0012.
 */
 class OnlyCacheStrategy extends BaseStrategy{
    private OnlyCacheStrategy(){}

    public static final  OnlyCacheStrategy INSTANCE=new OnlyCacheStrategy();

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source) {
        return loadCache(rxCache,key);
    }
}
