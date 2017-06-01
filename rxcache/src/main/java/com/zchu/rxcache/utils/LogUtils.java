package com.zchu.rxcache.utils;

import android.util.Log;

import java.util.Collection;
import java.util.Iterator;

public final class LogUtils {
    private LogUtils() {
    }

    public static boolean DEBUG =false;

    public static void log(Object message) {
        StackTraceElement element = new Throwable().getStackTrace()[1];
        print(element, message, null);
    }
    public static void log(Object message, Throwable error) {
        StackTraceElement element = new Throwable().getStackTrace()[1];
        print(element, message, error);
    }

    public static void debug(Object message) {
        if (DEBUG) {
            StackTraceElement element = new Throwable().getStackTrace()[1];
            print(element, message, null);
        }
    }
    public static void debug(Object message, Throwable error) {
        if (DEBUG) {
            StackTraceElement element = new Throwable().getStackTrace()[1];
            print(element, message, error);
        }
    }



    private static void print(StackTraceElement element, Object message, Throwable error) {
        String className = element.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
        String tag = className+'.'+element.getMethodName()+'('+element.getFileName()+':'+element.getLineNumber()+')';
        String text = toString(message);

        if (error != null) {
            Log.e("[RxCache]", tag + "\n\t" + text, error);
        } else {
            Log.e("[RxCache]", tag + "\n\t" + text);
        }
    }

    private static String toString(Object message) {
        if (message == null) {
            return "[null]";
        }
        if (message instanceof Throwable) {
            return Log.getStackTraceString((Throwable) message);
        }
        if (message instanceof Collection) {
            return toString((Collection) message);
        }
        return String.valueOf(message);
    }
    private static String toString(Collection message) {
        Iterator it = message.iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            Object e = it.next();
            sb.append(e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append('\n').append(' ');
        }
    }

}
