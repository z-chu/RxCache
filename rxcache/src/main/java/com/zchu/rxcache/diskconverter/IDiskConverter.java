package com.zchu.rxcache.diskconverter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 通用转换器
 *
 * Created by Chu on 2016/9/10.
 */
public interface IDiskConverter {

    /**
     * 读取
     *
     * @param source
     * @return
     */
    <T> T  load(InputStream source, Class<T> classOf);

    /**
     * 写入
     *
     * @param sink
     * @param data
     * @return
     */
    boolean writer(OutputStream sink, Object data);

}
