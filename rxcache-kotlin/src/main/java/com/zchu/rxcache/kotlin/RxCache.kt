package com.zchu.rxcache.kotlin

import com.google.gson.reflect.TypeToken
import com.zchu.rxcache.RxCache
import com.zchu.rxcache.data.CacheResult
import com.zchu.rxcache.stategy.IFlowableStrategy
import com.zchu.rxcache.stategy.IObservableStrategy
import io.reactivex.rxjava3.core.*

inline fun <reified T> RxCache.load(key: String): Observable<CacheResult<T>> {
    return load<T>(key, object : TypeToken<T>() {}.type)
}

inline fun <reified T> RxCache.load2Flowable(key: String): Flowable<CacheResult<T>> {
    return load2Flowable(key, object : TypeToken<T>() {}.type, BackpressureStrategy.LATEST)
}

inline fun <reified T> RxCache.load2Flowable(key: String, backpressureStrategy: BackpressureStrategy): Flowable<CacheResult<T>> {
    return load2Flowable(key, object : TypeToken<T>() {}.type, backpressureStrategy)
}

inline fun <reified T> RxCache.transformObservable(key: String, strategy: IObservableStrategy): ObservableTransformer<T, CacheResult<T>> {
    return transformObservable(key, object : TypeToken<T>() {}.type, strategy)
}

inline fun <reified T> RxCache.transformFlowable(key: String, strategy: IFlowableStrategy): FlowableTransformer<T, CacheResult<T>> {
    return transformFlowable(key, object : TypeToken<T>() {}.type, strategy)
}


inline fun <reified T> Observable<T>.rxCache(key: String, strategy: IObservableStrategy): Observable<CacheResult<T>> {
    return this.rxCache(RxCache.getDefault(), key, strategy)
}

inline fun <reified T> Observable<T>.rxCache(rxCache: RxCache, key: String, strategy: IObservableStrategy): Observable<CacheResult<T>> {
    return this.compose<CacheResult<T>>(rxCache.transformObservable(key, object : TypeToken<T>() {}.type, strategy))
}

inline fun <reified T> Flowable<T>.rxCache(key: String, strategy: IFlowableStrategy): Flowable<CacheResult<T>> {
    return this.rxCache(RxCache.getDefault(), key, strategy)
}

inline fun <reified T> Flowable<T>.rxCache(rxCache: RxCache, key: String, strategy: IFlowableStrategy): Flowable<CacheResult<T>> {
    return this.compose<CacheResult<T>>(rxCache.transformFlowable(key, object : TypeToken<T>() {}.type, strategy))
}






