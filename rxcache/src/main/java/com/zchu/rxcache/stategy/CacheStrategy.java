package com.zchu.rxcache.stategy;

/**
 * Created by Chu on 2016/10/25.
 */

public final class CacheStrategy {

    public static IStrategy firstRemote(){
        return  FirstRemoteStrategy.INSTANCE;
    }
    public static IStrategy firstCache(){
        return  FirstCacheStategy.INSTANCE;
    }
    public static IStrategy  onlyRemote(){
        return  OnlyRemoteStrategy.INSTANCE;
    }
    public static IStrategy onlyCache(){
        return  OnlyCacheStrategy.INSTANCE;
    }
    public static IStrategy cacheAndRemote(){
        return  CacheAndRemoteStrategy.INSTANCE;
    }

}
