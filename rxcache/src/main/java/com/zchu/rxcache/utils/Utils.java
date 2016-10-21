package com.zchu.rxcache.utils;

import java.io.Closeable;
import java.io.IOException;


/**
 * 工具类
 */
public final class Utils {

    private Utils() {
    }

    public static void close(Closeable close) {
        if (close != null) {
            try {
                closeThrowException(close);
            } catch (IOException ignored) {
            }
        }
    }

    public static void closeThrowException(Closeable close) throws IOException {
        if (close != null) {
            close.close();
        }
    }

}
