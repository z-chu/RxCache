package com.zchu.rxcache.data;

/**
 * 数据来源
 */
public enum ResultFrom {
    Remote, Disk, Memory;

    public static boolean ifFromCache(ResultFrom from) {
        return from == Disk || from == Memory;
    }
}
