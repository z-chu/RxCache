package com.zchu.rxcache.diskconverter;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.zchu.rxcache.utils.LogUtils;
import com.zchu.rxcache.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * Created by Chu on 2017/2/28.
 */

public class GsonDiskConverter implements IDiskConverter {

    private Gson mGson = new Gson();


    public GsonDiskConverter() {
        this(new Gson());
    }

    public GsonDiskConverter(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.mGson = gson;
    }


    @Override
    public <T> T load(InputStream source, Type type) {

        T value = null;
        try {
            TypeAdapter<?> adapter = mGson.getAdapter(TypeToken.get(type));
            JsonReader jsonReader = mGson.newJsonReader(new InputStreamReader(source));
            value = (T) adapter.read(jsonReader);
        } catch (JsonIOException | JsonSyntaxException e) {
            LogUtils.log(e);
        } catch (IOException e) {
            LogUtils.log(e);
        } finally {
            Utils.close(source);
        }
        return value;
    }

    @Override
    public boolean writer(OutputStream sink, Object data) {
        try {

            String json = mGson.toJson(data);
            byte[] bytes = json.getBytes();
            sink.write(bytes, 0, bytes.length);
            sink.flush();
            return true;
        } catch (JsonIOException | IOException e) {
            LogUtils.log(e);
        } finally {
            Utils.close(sink);
        }
        return false;
    }
}
