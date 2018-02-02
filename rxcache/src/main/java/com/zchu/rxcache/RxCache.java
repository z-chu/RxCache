package com.zchu.rxcache;

import android.os.StatFs;

import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.diskconverter.IDiskConverter;
import com.zchu.rxcache.diskconverter.SerializableDiskConverter;
import com.zchu.rxcache.stategy.IFlowableStrategy;
import com.zchu.rxcache.stategy.IObservableStrategy;
import com.zchu.rxcache.utils.LogUtils;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;


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

    /**
     *
     * notice: Deprecated! Use {@link #transformObservable(String, Type, IObservableStrategy)} ()}  replace.
     */
    @Deprecated
    public <T> ObservableTransformer<T, CacheResult<T>> transformer(String key, Type type, IObservableStrategy strategy) {
        return transformObservable(key, type, strategy);
    }

    public <T> ObservableTransformer<T, CacheResult<T>> transformObservable(final String key, final Type type, final IObservableStrategy strategy) {
        return new ObservableTransformer<T, CacheResult<T>>() {
            @Override
            public ObservableSource<CacheResult<T>> apply(Observable<T> tObservable) {
                return strategy.execute(RxCache.this, key, tObservable, type);
            }
        };
    }

    public <T> FlowableTransformer<T, CacheResult<T>> transformFlowable(final String key, final Type type, final IFlowableStrategy strategy) {
        return new FlowableTransformer<T, CacheResult<T>>() {
            @Override
            public Publisher<CacheResult<T>> apply(Flowable<T> flowable) {
                return strategy.flow(RxCache.this, key, flowable, type);
            }
        };
    }


    /**
     * 读取
     */
    public <T> Observable< CacheResult<T>> load(final String key, final Type type) {
        return Observable.create(new ObservableOnSubscribe< CacheResult<T>>() {
            @Override
            public void subscribe(ObservableEmitter< CacheResult<T>> observableEmitter) throws Exception {
                CacheResult<T> load = cacheCore.load(getMD5MessageDigest(key), type);
                if (!observableEmitter.isDisposed()) {
                    if(load!=null) {
                        observableEmitter.onNext(load);
                        observableEmitter.onComplete();
                    }else{
                        observableEmitter.onError(new NullPointerException("Not find the key corresponding to the cache"));
                    }
                }
            }
        });
    }

    /**
     * 读取
     */
    public <T> Flowable< CacheResult<T>> load2Flowable(String key, Type type) {
        return load2Flowable(key, type, BackpressureStrategy.LATEST);
    }

    public <T> Flowable< CacheResult<T>> load2Flowable(final String key, final Type type, BackpressureStrategy backpressureStrategy) {
        return Flowable.create(new FlowableOnSubscribe< CacheResult<T>>() {
            @Override
            public void subscribe(FlowableEmitter< CacheResult<T>> flowableEmitter) throws Exception {
                CacheResult<T> load = cacheCore.load(getMD5MessageDigest(key), type);
                if (!flowableEmitter.isCancelled()) {
                    if(load!=null) {
                        flowableEmitter.onNext(load);
                        flowableEmitter.onComplete();
                    }else{
                        flowableEmitter.onError(new NullPointerException("Not find the key corresponding to the cache"));
                    }
                }
            }
        }, backpressureStrategy);
    }

    /**
     * 保存
     */
    public <T> Observable<Boolean> save(final String key, final T value, final CacheTarget target) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> observableEmitter) throws Exception {
                boolean save = cacheCore.save(getMD5MessageDigest(key), value, target);
                if (!observableEmitter.isDisposed()) {
                    observableEmitter.onNext(save);
                    observableEmitter.onComplete();
                }
            }
        });
    }

    /**
     * 保存
     */
    public <T> Flowable<Boolean> save2Flowable(final String key, final T value, final CacheTarget target) {
        return save2Flowable(key, value, target, BackpressureStrategy.LATEST);
    }

    /**
     * 保存
     */
    public <T> Flowable<Boolean> save2Flowable(final String key, final T value, final CacheTarget target, BackpressureStrategy strategy) {
        return Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> flowableEmitter) throws Exception {
                boolean save = cacheCore.save(getMD5MessageDigest(key), value, target);
                if (!flowableEmitter.isCancelled()) {
                    flowableEmitter.onNext(save);
                    flowableEmitter.onComplete();
                }
            }
        }, strategy);
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
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> observableEmitter) throws Exception {
                try {
                    cacheCore.clear();
                    if (!observableEmitter.isDisposed()) {
                        observableEmitter.onNext(true);
                        observableEmitter.onComplete();
                    }
                } catch (IOException e) {
                    LogUtils.log(e);
                    if (!observableEmitter.isDisposed()) {
                        observableEmitter.onError(e);
                    }
                }
            }
        });
    }

    /**
     * 清空缓存
     */
    public void clear2() throws IOException {
        cacheCore.clear();
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
                LogUtils.log(ignored);
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
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            LogUtils.log(e);
            return buffer;
        }
    }

}
