package com.zchu.rxcache;


import android.support.v4.util.LruCache;

import com.zchu.rxcache.utils.LogUtils;
import com.zchu.rxcache.utils.Memory;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by Chu on 2016/9/10.
 */
class LruMemoryCache {
    private LruCache<String, Serializable> mCache;
    private final HashSet<String> mKeySet;

    public LruMemoryCache(final int cacheSize) {
        mKeySet = new HashSet<>();
        mCache = new LruCache<String, Serializable>(cacheSize) {
            @Override
            protected int sizeOf(String key, Serializable value) {
                try {
                    return Memory.sizeOf(value);
                } catch (IOException e) {
                    throw new MemorySizeMeasureException();
                }
            }
        };
    }

    public <T> T load(String key, long existTime, Class<T> classOf) {
        return (T) mCache.get(key);
    }

    public <T> boolean save(String key, T value) {
        if (null != value) {
            Serializable put = null;
            try {
                put = mCache.put(key, (Serializable) value);
            } catch (MemorySizeMeasureException e) {
                LogUtils.log(e);
            }
            if (put != null) {
                mKeySet.add(key);
            }
        }
        return true;
    }

    public boolean containsKey(String key) {
        return mKeySet.contains(key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public final boolean remove(String key) {
        Serializable remove = mCache.remove(key);
        if (remove != null) {
            mKeySet.remove(key);
            return true;
        }
        return false;
    }

    public void clear() {
        mKeySet.clear();
        mCache.evictAll();
    }

}
