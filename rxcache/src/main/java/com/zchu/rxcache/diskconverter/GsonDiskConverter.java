package com.zchu.rxcache.diskconverter;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.zchu.rxcache.utils.LogUtils;
import com.zchu.rxcache.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Chu on 2017/2/28.
 */

public class GsonDiskConverter implements IDiskConverter {

    private Gson gson = new Gson();

    @Override
    public <T> T load(InputStream source, Class<T> classOf) {
        T value = null;
        try {
            value = gson.fromJson(new InputStreamReader(source), classOf);
        } catch (JsonIOException | JsonSyntaxException e) {
            LogUtils.log(e);
        } finally {
            Utils.close(source);
        }
        return value;
    }

    @Override
    public boolean writer(OutputStream sink, Object data) {
        try {
            String json = gson.toJson(data);
            byte[] bytes = json.getBytes();
            sink.write(bytes, 0, bytes.length);
            sink.flush();
            return true;
        } catch (JsonIOException | IOException e) {
            LogUtils.log(e);
        }
        return false;
    }
}
