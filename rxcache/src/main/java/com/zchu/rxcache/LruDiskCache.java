package com.zchu.rxcache;

import com.jakewharton.disklrucache.DiskLruCache;
import com.zchu.rxcache.diskconverter.IDiskConverter;
import com.zchu.rxcache.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Created by Z.Chu on 2016/9/10.
 */
class LruDiskCache {
    private IDiskConverter mDiskConverter;
    private DiskLruCache mDiskLruCache;


    LruDiskCache(IDiskConverter diskConverter, File diskDir, int appVersion, long diskMaxSize) {
        this.mDiskConverter = diskConverter;
        try {
            mDiskLruCache = DiskLruCache.open(diskDir, appVersion, 1, diskMaxSize);
        } catch (IOException e) {
            LogUtils.log(e);
        }
    }

    <T> T load(String key, long existTime, Type type) {
        if (mDiskLruCache == null) {
            return null;
        }
        try {
            DiskLruCache.Editor edit = mDiskLruCache.edit(key);
            if (edit == null) {
                return null;
            }
            InputStream source = edit.newInputStream(0);
            T value;
            if (source != null) {
                value = mDiskConverter.load(source, type);
                edit.commit();
                return value;
            }
            edit.abort();
        } catch (IOException e) {
            LogUtils.log(e);
        }
        return null;
    }


    <T> boolean save(String key, T value) {
        if (mDiskLruCache == null) {
            return false;
        }
        //如果要保存的值为空,则删除
        if (value == null) {
            return remove(key);
        }
        try {
            DiskLruCache.Editor edit = mDiskLruCache.edit(key);
            if (edit == null) {
                return false;
            }
            OutputStream sink = edit.newOutputStream(0);
            if (sink != null) {
                mDiskConverter.writer(sink, value);
                edit.commit();
                return true;
            }
            edit.abort();
        } catch (IOException e) {
            LogUtils.log(e);
        }
        return false;
    }


    boolean containsKey(String key) {
        try {
            return mDiskLruCache.get(key) != null;
        } catch (IOException e) {
            LogUtils.log(e);
        }
        return false;
    }

    /**
     * 删除缓存
     */
    final boolean remove(String key) {
        try {
            return mDiskLruCache.remove(key);
        } catch (IOException e) {
            LogUtils.log(e);
        }
        return false;
    }

    void clear() throws IOException {
        mDiskLruCache.delete();

    }


}
