package com.zchu.rxcache;


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
    <T> T load(String key) {
        if (memory != null) {
            T result = memory.load(key, 0);
            if (result != null) {
                return result;
            }
        }

        if (disk != null) {
            T result = disk.load(key, 0);
            if (result != null) {
                if(memory!=null){
                    memory.save(key,result);
                }
                return result;
            }
        }

        return null;
    }

    /**
     * 保存
     */
    <T> boolean save(String key, T value, CacheTarget target) {
        if (value == null) { //如果要保存的值为空,则删除
            return memory.remove(key) && disk.remove(key);
        }

        if (target.supportMemory() && memory != null) {
            memory.save(key, value);
        }
        if (target.supportDisk() && disk != null) {
            return disk.save(key, value);
        }

        return false;
    }

    /**
     * 是否包含
     *
     * @param key
     * @return
     */
    boolean containsKey(String key) {
        if (memory != null) {
            if (memory.containsKey(key)) {
                return true;
            }
        }
        if (disk != null) {
            if (disk.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    boolean remove(String key) {
        if (memory != null) {
            return memory.remove(key);
        }
        if (disk != null) {
            return disk.remove(key);
        }
        return true;
    }

    /**
     * 清空缓存
     */
    void clear() {
        if (memory != null) {
            memory.clear();
        }
        if (disk != null) {
            disk.clear();
        }
    }

}
