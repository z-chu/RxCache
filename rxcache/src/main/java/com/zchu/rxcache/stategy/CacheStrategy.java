package com.zchu.rxcache.stategy;

/**
 * Created by Chu on 2016/10/25.
 */

public final class CacheStrategy {

    /**
     * 优先网络
     */
    public static IStrategy firstRemote(){
        return  FirstRemoteStrategy.INSTANCE;
    }

    /**
     * 优先缓存
     */
    public static IStrategy firstCache(){
        return  FirstCacheStrategy.INSTANCE;
    }

    /**
     * 仅加载网络，但数据依然会被缓存
     */
    public static IStrategy  onlyRemote(){
        return  OnlyRemoteStrategy.INSTANCE;
    }

    /**
     * 仅加载缓存
     */
    public static IStrategy onlyCache(){
        return  OnlyCacheStrategy.INSTANCE;
    }

    /**
     * 先加载缓存，后加载网络
     */
    public static IStrategy cacheAndRemote(){
        return  CacheAndRemoteStrategy.INSTANCE;
    }

    /**
     * 仅加载网络，不缓存
     */
    public static IStrategy none(){
        return  NoneStrategy.INSTANCE;
    }
}
