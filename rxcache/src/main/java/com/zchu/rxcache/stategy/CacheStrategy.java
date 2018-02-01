package com.zchu.rxcache.stategy;

/**
 * Created by Chu on 2016/10/25.
 */

public final class CacheStrategy {




    /**
     * 优先网络,缓存用异步的方式保存
     */
    public static IStrategy firstRemote() {

        return new FirstRemoteStrategy();
    }

    /**
     * 优先网络,缓存用同步的方式保存
     */
    public static IStrategy firstRemoteSync() {
        return  new FirstRemoteStrategy(true);
    }

    /**
     * 优先缓存,缓存用异步的方式保存
     */
    public static IStrategy firstCache() {
        return  new FirstCacheStrategy();
    }

    /**
     * 优先缓存,缓存用同步的方式保存
     */
    public static IStrategy firstCacheSync() {
        return  new FirstCacheStrategy(true);
    }

    /**
     * 优先缓存,并设置超时时间
     */
    public static IStrategy firstCacheTimeout(long milliSecond) {
        return  new FirstCacheTimeoutStrategy(milliSecond);
    }

    /**
     * 优先缓存,并设置超时时间
     */
    public static IStrategy firstCacheTimeoutSync(long milliSecond) {
        return  new FirstCacheTimeoutStrategy(milliSecond,true);
    }


    /**
     * 仅加载网络，但数据依然会被缓存
     */
    public static IStrategy onlyRemote() {
        return new OnlyRemoteStrategy();
    }

    /**
     * 仅加载网络，但数据依然会被缓存
     */
    public static IStrategy onlyRemoteSync() {
        return  new OnlyRemoteStrategy(true);
    }

    /**
     * 仅加载缓存
     */
    public static IStrategy onlyCache() {
        return new OnlyCacheStrategy();
    }

    /**
     * 先加载缓存，后加载网络，缓存用异步的方式保存
     */
    public static IStrategy cacheAndRemote() {
        return new CacheAndRemoteStrategy();
    }

    /**
     * 先加载缓存，后加载网络，缓存用同步的方式保存
     */
    public static IStrategy cacheAndRemoteSync() {
        return new CacheAndRemoteStrategy(true);
    }

    /**
     * 仅加载网络，不缓存
     */
    public static IStrategy none() {
        return NoneStrategy.INSTANCE;
    }
}
