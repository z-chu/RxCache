package com.zchu.rxcache.data;

/**
 * 数据
 */
public class CacheResult<T> {

    private ResultFrom from;
    private String key;
    private T data;

    public CacheResult() {
    }
    public CacheResult(ResultFrom from, String key, T data) {
        this.from = from;
        this.key = key;
        this.data = data;
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

    @Override
    public String toString() {
        return "ResultData{" +
                "from=" + from +
                ", key='" + key + '\'' +
                ", data=" + data +
                '}';
    }

}
