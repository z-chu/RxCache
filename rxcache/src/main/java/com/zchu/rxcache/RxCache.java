package com.zchu.rxcache;

import android.os.StatFs;

import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.diskconverter.IDiskConverter;
import com.zchu.rxcache.diskconverter.SerializableDiskConverter;
import com.zchu.rxcache.stategy.IStrategy;
import com.zchu.rxcache.utils.LogUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.security.MessageDigest;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.exceptions.Exceptions;


/**
 * RxJava remote data cache processing library, support Serializable, JSON
 * 作者: 赵成柱 on 2016/9/9 0012.
 */
public final class RxCache {


    private final CacheCore cacheCore;

    private RxCache(Builder builder) {
        LruMemoryCache memoryCache = null;
        if (builder.memoryMaxSize > 0) {
            memoryCache = new LruMemoryCache(builder.memoryMaxSize);
        }
        LruDiskCache lruDiskCache = null;
        if (builder.diskMaxSize > 0) {
            lruDiskCache = new LruDiskCache(builder.diskConverter, builder.diskDir, builder.appVersion, builder.diskMaxSize);
        }
        cacheCore = new CacheCore(memoryCache, lruDiskCache);

    }

    public <T> ObservableTransformer<T, CacheResult<T>> transformer(final String key, final Type type, final IStrategy strategy) {
        return new ObservableTransformer<T, CacheResult<T>>() {
            @Override
            public ObservableSource<CacheResult<T>> apply(Observable<T> tObservable) {
                return strategy.execute(RxCache.this, getMD5MessageDigest(key), tObservable, type);
            }
        };
    }

    private static abstract class SimpleSubscribe<T> implements ObservableOnSubscribe<T> {
        @Override
        public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
            try {
                T data = execute();
                if (!subscriber.isDisposed()) {
                    subscriber.onNext(data);
                }
            } catch (Throwable e) {
                LogUtils.log(e);
                Exceptions.throwIfFatal(e);
                if (!subscriber.isDisposed()) {
                    subscriber.onError(e);
                }
                return;
            }

            if (!subscriber.isDisposed()) {
                subscriber.onComplete();
            }
        }

        abstract T execute() throws Throwable;
    }

    /**
     * 读取
     */
    public <T> Observable<T> load(final String key, final Type type) {
        return Observable.create(new SimpleSubscribe<T>() {
            @Override
            T execute() {
                LogUtils.debug("loadCache  key=" + key);
                return cacheCore.load(getMD5MessageDigest(key), type);
            }
        });
    }

    /**
     * 保存
     */
    public <T> Observable<Boolean> save(final String key, final T value, final CacheTarget target) {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                return cacheCore.save(getMD5MessageDigest(key), value, target);
            }
        });
    }

    /**
     * 是否包含
     *
     * @param key
     * @return
     */
    public boolean containsKey(final String key) {
        return cacheCore.containsKey(getMD5MessageDigest(key));
    }

    /**
     * 删除缓存
     */
    public boolean remove(final String key) {
        return cacheCore.remove(getMD5MessageDigest(key));
    }

    /**
     * 清空缓存
     */
    public Observable<Boolean> clear() {
        return Observable.create(new SimpleSubscribe<Boolean>() {
            @Override
            Boolean execute() throws Throwable {
                cacheCore.clear();
                return true;
            }
        });
    }

    /**
     * 构造器
     */
    public static final class Builder {
        private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
        private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
        private static final int DEFAULT_MEMORY_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 8);//运行内存的8分之1
        private Integer memoryMaxSize;
        private Long diskMaxSize;
        private int appVersion;
        private File diskDir;
        private IDiskConverter diskConverter;

        public Builder() {
        }

        /**
         * 不设置,默认为运行内存的8分之1.设置0,或小于0，则不开启内存缓存;
         */
        public Builder memoryMax(int maxSize) {
            this.memoryMaxSize = maxSize;
            return this;
        }

        /**
         * 不设置，默认为1.需要注意的是,每当版本号改变,缓存路径下存储的所有数据都会被清除掉,所有的数据都应该从网上重新获取.
         */
        public Builder appVersion(int appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder diskDir(File directory) {
            this.diskDir = directory;
            return this;
        }


        public Builder diskConverter(IDiskConverter converter) {
            this.diskConverter = converter;
            return this;
        }

        /**
         * 不设置， 默为认50MB.设置0,或小于0，则不开启硬盘缓存;
         */
        public Builder diskMax(long maxSize) {
            this.diskMaxSize = maxSize;
            return this;
        }

        public Builder setDebug(boolean debug) {
            LogUtils.DEBUG = debug;
            return this;
        }

        public RxCache build() {
            if (this.diskDir == null) {
                throw new NullPointerException("DiskDir can not be null.");
            }
            if (!this.diskDir.exists()) {
                this.diskDir.mkdirs();
            }
            if (this.diskConverter == null) {
                this.diskConverter = new SerializableDiskConverter();
            }
            if (memoryMaxSize == null) {
                memoryMaxSize = DEFAULT_MEMORY_CACHE_SIZE;
            }
            if (diskMaxSize == null) {
                diskMaxSize = calculateDiskCacheSize(diskDir);
            }
            appVersion = Math.max(1, this.appVersion);
            return new RxCache(this);
        }

        private static long calculateDiskCacheSize(File dir) {
            long size = 0;

            try {
                StatFs statFs = new StatFs(dir.getAbsolutePath());
                long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
                // Target 2% of the total space.
                size = available / 50;
            } catch (IllegalArgumentException ignored) {
            }
            // Bound inside min/max size for disk cache.
            return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
        }

    }

    static String getMD5MessageDigest(String buffer) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer.getBytes());
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

}
