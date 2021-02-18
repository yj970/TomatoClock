package com.yj.tomatoclock;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import dev.utils.common.ObjectUtils;

public class JsonUtils {
    private static Gson gson = null;

    private JsonUtils() {
    }


    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String toString(Object object) {
        String gsonString = null;

        gsonString = gson().toJson(object);

        return gsonString;
    }

    /**
     * 转成bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T toObject(String gsonString, Class<T> cls) {
        T t = null;

        try {
            if (ObjectUtils.isEmpty(gsonString)) return null;
            t = gson().fromJson(gsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;
    }

    /**
     * 转成list
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> List<T> toList(String gsonString, Class<T> cls) {
        if (ObjectUtils.isEmpty(gsonString)) return null;
        Type type = new ParameterizedTypeImpl(cls);
        List<T> list = gson().fromJson(gsonString, type);
        return list;
    }

    /**
     * 转成list中有map的
     *
     * @param gsonString
     * @return
     */
    public static <T> List<Map<String, T>> toListMap(String gsonString) {
        List<Map<String, T>> list = null;
        list = gson().fromJson(gsonString,
                new TypeToken<List<Map<String, T>>>() {
                }.getType());
        return list;
    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> toMap(String gsonString) {
        Map<String, T> map = null;
        map = gson().fromJson(gsonString, new TypeToken<Map<String, T>>() {
        }.getType());
        return map;
    }

    public static JsonObject toJson(String gsonString) {


        return new JsonParser().parse(gsonString).getAsJsonObject();
    }

    /**
     * 是否是json字符串
     *
     * @param json
     * @return
     */
    public static boolean isJson(String json) {
        if (TextUtils.isEmpty(json)) return false;
        if ((json.startsWith("{") && json.endsWith("}")) || json.startsWith("[") || json.endsWith("]")) {
            return true;
        }
        return false;
    }

    public static Gson gson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static class ParameterizedTypeImpl implements ParameterizedType {
        Class clazz;

        public ParameterizedTypeImpl(Class clz) {
            clazz = clz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
