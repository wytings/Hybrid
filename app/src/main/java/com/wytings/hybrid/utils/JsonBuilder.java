package com.wytings.hybrid.utils;


import org.json.JSONObject;

/**
 * Created by rex on 12/29/16.
 */

public class JsonBuilder {

    private JSONObject jsonObject;

    public JsonBuilder() {
        jsonObject = new JSONObject();
    }

    public JsonBuilder put(String key, Object value) {
        try {
            jsonObject.putOpt(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

    public Object toObject() {
        return jsonObject;
    }
}
