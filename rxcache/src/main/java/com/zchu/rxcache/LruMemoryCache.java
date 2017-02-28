package com.zchu.rxcache;


import android.support.v4.util.LruCache;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
               return  calcSize(value);
            }
        };
    }

    public <T> T load(String key, long existTime, Class<T> classOf) {
         return (T) mCache.get(key);
    }

    public <T> boolean save(String key, T value) {
        if (null != value) {
            mCache.put(key, (Serializable) value);
            mKeySet.add(key);
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
        mKeySet.remove(key);
        return mCache.remove(key) != null;
    }

    public void clear() {
        mKeySet.clear();
        mCache.evictAll();
    }

    /**
     * 测量Serializable的内存占用大小
     */
    private static int calcSize(Serializable o) {
        int ret = 0;
        class DumbOutputStream extends OutputStream {
            int count = 0;
            public void write(int b) throws IOException {
                count++; // 只计数，不产生字节转移
            }
        }
        DumbOutputStream buf = new DumbOutputStream();
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(buf);
            os.writeObject(o);
            ret = buf.count;
        } catch (IOException e) {
            // No need handle this exception
            e.printStackTrace();
            ret = -1;
        } finally {
            try {
                os.close();
            } catch (Exception e) {
            }
        }
        return ret;
    }

}
