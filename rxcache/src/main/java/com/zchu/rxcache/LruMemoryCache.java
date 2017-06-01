package com.zchu.rxcache;


import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.zchu.rxcache.utils.LogUtils;
import com.zchu.rxcache.utils.MemorySizeOf;
import com.zchu.rxcache.utils.Occupy;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Chu on 2016/9/10.
 */
class LruMemoryCache {
    private LruCache<String, Object> mCache;
    private HashMap<String, Integer> memorySizeMap;//储存初次加入缓存的size，规避对象在内存中大小变化造成的测量出错
    private Occupy occupy;

    public LruMemoryCache(final int cacheSize) {
        memorySizeMap = new HashMap<>();
        byte to = 0;
        byte t4 = 4;
        occupy = new Occupy(to, to, t4);
        mCache = new LruCache<String, Object>(cacheSize) {
            @Override
            protected int sizeOf(String key, Object value) {
                return memorySizeMap.get(key);
            }
        };
    }

    public <T> T load(String key, long existTime) {
        return (T) mCache.get(key);
    }

    public <T> boolean save(String key, T value) {
        if (null != value) {
            memorySizeMap.put(key, (int) countSize(value));
            mCache.put(key, value);
        }
        return true;
    }

    public boolean containsKey(String key) {
        return memorySizeMap.containsKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public final boolean remove(String key) {
        Object remove = mCache.remove(key);
        if (remove != null) {
            memorySizeMap.remove(key);
            return true;
        }
        return false;
    }

    public void clear() {
        memorySizeMap.clear();
        mCache.evictAll();
    }

    private long countSize(Object value) {
        if (value == null) {
            return 0;
        }

        //  更优良的内存大小算法
        long size;
        if (value instanceof Bitmap) {
            LogUtils.debug("Bitmap");
            size = MemorySizeOf.sizeOf((Bitmap) value);
        } else if (value instanceof Serializable) {
            LogUtils.debug("Serializable");
            try {
                size = MemorySizeOf.sizeOf((Serializable) value);
            } catch (IOException e) {
                size = occupy.occupyof(value);
            }
        } else {
            size = occupy.occupyof(value);
        }
        LogUtils.debug("size=" + size + " value=" + value);
        return size;
    }

}
