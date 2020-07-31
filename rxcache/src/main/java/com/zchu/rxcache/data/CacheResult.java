package com.zchu.rxcache.data;


import io.reactivex.rxjava3.functions.Function;

/**
 * 数据
 */
public class CacheResult<T> {

    private ResultFrom from;
    private String key;
    private T data;
    private long timestamp;

    public CacheResult() {
    }

    public CacheResult(ResultFrom from, String key, T data) {
        this.from = from;
        this.key = key;
        this.data = data;
    }

    public CacheResult(ResultFrom from, String key, T data, long timestamp) {
        this.from = from;
        this.key = key;
        this.data = data;
        this.timestamp = timestamp;
    }

    public ResultFrom getFrom() {
        return from;
    }

    public void setFrom(ResultFrom from) {
        this.from = from;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CacheResult{" +
                "from=" + from +
                ", key='" + key + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }


    /**
     * 用于map操作符，只想拿CacheResult.data的数据
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    public static class MapFunc<T> implements Function<CacheResult<T>, T> {

        @Override
        public T apply(CacheResult<T> tCacheResult) throws Exception {
            if (tCacheResult != null) {
                return tCacheResult.getData();
            }
            return null;
        }
    }
}
