package com.zchu.rxcache.stategy;

/**
 * Created by Chu on 2016/10/25.
 */

public final class CacheStrategy {

    private static FirstRemoteStrategy sFirstRemoteStrategy;
    private static FirstRemoteStrategy sFirstRemoteSyncStrategy;
    private static FirstCacheStrategy sFirstCacheStrategy;
    private static FirstCacheStrategy sFirstCacheSyncStrategy;
    private static OnlyRemoteStrategy sOnlyRemoteStrategy;
    private static OnlyRemoteStrategy sOnlyRemoteSyncStrategy;
    private static OnlyCacheStrategy sOnlyCacheStrategy;
    private static CacheAndRemoteStrategy sCacheAndRemoteStrategy;
    private static CacheAndRemoteStrategy sCacheAndRemoteSyncStrategy;


    /**
     * 优先网络,缓存用异步的方式保存
     */
    public static IStrategy firstRemote() {
        if (sFirstRemoteStrategy == null) {
            sFirstRemoteStrategy = new FirstRemoteStrategy();
        }
        return sFirstRemoteStrategy;
    }

    /**
     * 优先网络,缓存用同步的方式保存
     */
    public static IStrategy firstRemoteSync() {
        if (sFirstRemoteSyncStrategy == null) {
            sFirstRemoteSyncStrategy = new FirstRemoteStrategy(true);
        }
        return sFirstRemoteSyncStrategy;
    }

    /**
     * 优先缓存,缓存用异步的方式保存
     */
    public static IStrategy firstCache() {
        if (sFirstCacheStrategy == null) {
            sFirstCacheStrategy = new FirstCacheStrategy();
        }
        return sFirstCacheStrategy;
    }

    /**
     * 优先缓存,缓存用同步的方式保存
     */
    public static IStrategy firstCacheSync() {
        if (sFirstCacheSyncStrategy == null) {
            sFirstCacheSyncStrategy = new FirstCacheStrategy(true);
        }
        return sFirstCacheSyncStrategy;
    }

    /**
     * 仅加载网络，但数据依然会被缓存
     */
    public static IStrategy onlyRemote() {
        if (sOnlyRemoteStrategy == null) {
            sOnlyRemoteStrategy = new OnlyRemoteStrategy();
        }
        return sOnlyRemoteStrategy;
    }

    /**
     * 仅加载网络，但数据依然会被缓存
     */
    public static IStrategy onlyRemoteSync() {
        if (sOnlyRemoteSyncStrategy == null) {
            sOnlyRemoteSyncStrategy = new OnlyRemoteStrategy(true);
        }
        return sOnlyRemoteSyncStrategy;
    }

    /**
     * 仅加载缓存
     */
    public static IStrategy onlyCache() {
        if (sOnlyCacheStrategy == null) {
            sOnlyCacheStrategy = new OnlyCacheStrategy();
        }
        return sOnlyCacheStrategy;
    }

    /**
     * 先加载缓存，后加载网络，缓存用异步的方式保存
     */
    public static IStrategy cacheAndRemote() {
        if (sCacheAndRemoteStrategy == null) {
            sCacheAndRemoteStrategy = new CacheAndRemoteStrategy();
        }
        return sCacheAndRemoteStrategy;
    }

    /**
     * 先加载缓存，后加载网络，缓存用同步的方式保存
     */
    public static IStrategy cacheAndRemoteSync() {
        if (sCacheAndRemoteSyncStrategy == null) {
            sCacheAndRemoteSyncStrategy = new CacheAndRemoteStrategy(true);
        }
        return sCacheAndRemoteSyncStrategy;
    }

    /**
     * 仅加载网络，不缓存
     */
    public static IStrategy none() {
        return NoneStrategy.INSTANCE;
    }
}
