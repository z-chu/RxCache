package com.zchu.rxcache.diskconverter;


import com.zchu.rxcache.utils.LogUtils;
import com.zchu.rxcache.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


public class SerializableDiskConverter implements IDiskConverter {

    @Override
    public <T> T load(InputStream source, Class<T> classOf) {
        T value = null;
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(source);
            value = (T) oin.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LogUtils.log(e);
        } finally {
            Utils.close(oin);
        }
        return value;
    }

    @Override
    public boolean writer(OutputStream sink, Object data) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(sink);
            oos.writeObject(data);
            oos.flush();
            return true;
        } catch (IOException e) {
            LogUtils.log(e);
            return false;
        } finally {
            Utils.close(oos);
        }
    }

}
