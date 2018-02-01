package com.zchu.rxcache;


import com.zchu.rxcache.data.CacheResult;
import com.zchu.rxcache.data.ResultFrom;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.Key;

/**
 * 缓存核心
 * 作者: 赵成柱 on 2016/9/9
 */
class CacheCore {

    private LruMemoryCache memory;
    private LruDiskCache disk;

    CacheCore(LruMemoryCache memory, LruDiskCache disk) {
        this.memory = memory;
        this.disk = disk;
    }


    /**
     * 读取
     */
    <T> CacheResult<T> load(String key, Type type) {
        if (memory != null) {
            CacheHolder<T> result = memory.load(key);
            if (result != null) {
                return new CacheResult<>(ResultFrom.Memory, key, result.data, result.timestamp);
            }
        }
        if (disk != null) {
            CacheHolder<T> result = disk.load(key, type);
            if (result != null) {
                return new CacheResult<>(ResultFrom.Disk, key, result.data, result.timestamp);
            }
        }
        return null;
    }

    /**
     * 保存
     */
    <T> boolean save(String key, T value, CacheTarget target) {
        if (value == null) { //如果要保存的值为空,则删除
            boolean memoryRemove = true;
            if (memory != null) {
                memoryRemove = memory.remove(key);
            }
            boolean diskRemove = true;
            if (disk != null) {
                diskRemove = disk.remove(key);
            }
            return memoryRemove && diskRemove;
        }
        boolean save = false;
        if (target.supportMemory() && memory != null) {
            save = memory.save(key, value);
        }
        if (target.supportDisk() && disk != null) {
            return disk.save(key, value);
        }
        return save;
    }

    /**
     * 是否包含
     */
    boolean containsKey(String key) {
        return memory != null && memory.containsKey(key) || disk != null && disk.containsKey(key);
    }

    /**
     * 删除缓存
     */
    boolean remove(String key) {
        boolean isRemove = true;
        if (memory != null) {
            isRemove = memory.remove(key);
        }
        if (disk != null) {
            isRemove = isRemove && disk.remove(key);
        }
        return isRemove;
    }

    /**
     * 清空缓存
     */
    void clear() throws IOException {
        if (memory != null) {
            memory.clear();
        }
        if (disk != null) {
            disk.clear();
        }
    }

}
