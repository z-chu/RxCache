package com.zchu.rxcache;

import com.jakewharton.disklrucache.DiskLruCache;
import com.zchu.rxcache.diskconverter.IDiskConverter;
import com.zchu.rxcache.utils.LogUtils;
import com.zchu.rxcache.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

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

    <T> T load(String key, long existTime) {
        if (mDiskLruCache == null) {
            return null;
        }
        try {
            DiskLruCache.Editor edit = mDiskLruCache.edit(key);
            if (edit == null) {
                return null;
            }
            Class<T> classOf = loadClass(key);
            InputStream source = edit.newInputStream(0);
            T value;
            if (source != null && classOf != null) {
                value = mDiskConverter.load(source, classOf);
                edit.commit();
                return value;
            }
            edit.abort();
        } catch (IOException  e) {
            LogUtils.log(e);
        }
        return null;
    }

    /**
     * 获取保存的Object对象的Class
     */
    private Class loadClass(String key) {
        DiskLruCache.Editor edit = null;
        InputStream classSource=null;
        try {
            edit = mDiskLruCache.edit(RxCache.getMD5MessageDigest(key + "CLASS"));

            if (edit == null) {
                return null;
            }
             classSource = edit.newInputStream(0);
            if (classSource != null) {
                ObjectInputStream classInputStream = new ObjectInputStream(classSource);
                Class classOf = (Class) classInputStream.readObject();
                Utils.close(classInputStream);
                edit.commit();
                return classOf;
            }
            edit.abort();
        } catch (IOException | ClassNotFoundException e) {
            LogUtils.log(e);
        }finally {
            Utils.close(classSource);
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
            if (saveClass(key, value.getClass())) {
                OutputStream sink = edit.newOutputStream(0);
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

    /**
     * 将Object对象的Class序列化保存的磁盘，以便读取时做转换
     */
    private boolean saveClass(String key, Class aClass) {
        OutputStream sink = null;
        try {
            DiskLruCache.Editor edit = mDiskLruCache.edit(RxCache.getMD5MessageDigest(key + "CLASS"));
            if (edit == null) {
                return false;
            }
            sink = edit.newOutputStream(0);
            if (sink != null) {
                ObjectOutputStream classOutputStream = new ObjectOutputStream(sink);
                classOutputStream.writeObject(aClass);
                classOutputStream.flush();
                edit.commit();
                return true;
            }
            edit.abort();
        } catch (IOException e) {
            LogUtils.log(e);
        } finally {
            Utils.close(sink);
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

    void clear() {
        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            LogUtils.log(e);
        }
    }


}
