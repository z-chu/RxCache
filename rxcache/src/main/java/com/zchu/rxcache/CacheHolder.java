package com.zchu.rxcache;

class CacheHolder<T> {

    public CacheHolder(T data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public T data;
    public long timestamp;
}
