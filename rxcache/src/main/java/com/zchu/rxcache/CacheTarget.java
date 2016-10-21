package com.zchu.rxcache;

/**
 * 缓存目标
 * 作者: 赵成柱 on 2016/9/9
 */
public enum CacheTarget {
    Memory,
    Disk,
    MemoryAndDisk;

    public boolean supportMemory() {
        return this==Memory || this== MemoryAndDisk;
    }

    public boolean supportDisk() {
        return this==Disk || this== MemoryAndDisk;
    }

}
