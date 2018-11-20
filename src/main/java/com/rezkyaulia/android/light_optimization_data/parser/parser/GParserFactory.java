
package com.rezkyaulia.android.light_optimization_data.parser.parser;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Rezky Aulia Pratama on 7/4/2017.
 */
public final class GParserFactory extends ParserJson.Factory {

    private final Gson gson;

    public GParserFactory() {
        this.gson = new Gson();
    }

    public GParserFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public ParserJson<ResponseBody, ?> responseBodyParser(@NonNull Type type) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GResBodyParserJson<>(gson, adapter);
    }

    @Override
    public ParserJson<?, RequestBody> requestBodyParser(@NonNull Type type) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GReqBodyParserJson<>(gson, adapter);
    }

    @Override
    public Object getObject(@NonNull String string, @NonNull Type type) {
        try {
            return gson.fromJson(string, Objects.requireNonNull(type));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getString(@NonNull Object object) {
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public HashMap<String, String> getStringMap(@NonNull Object object) {
        try {
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            return gson.fromJson(gson.toJson(object), type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
