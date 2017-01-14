package com.wytings.hybrid.bridge;

import com.wytings.hybrid.utils.Logs;

import org.json.JSONObject;

public class HybridData {
    public final int id;
    public final String type;
    public final JSONObject entity;

    public HybridData(int id, String type, JSONObject entity) {
        this.id = id;
        this.type = type;
        this.entity = entity;
    }

    public static HybridData parse(String dataJson) {
        int id = Integer.MIN_VALUE;
        String type = "";
        JSONObject entity = new JSONObject();
        try {
            JSONObject request = new JSONObject(dataJson);
            id = request.optInt("id", Integer.MIN_VALUE);
            type = request.optString("type", "");
            entity = request.optJSONObject("entity");
            entity = entity == null ? new JSONObject() : entity;
        } catch (Exception e) {
            Logs.e("fail to parse json data -> " + e);
        }
        return new HybridData(id, type, entity);
    }

    @Override
    public String toString() {
        return "HybridData{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", entity=" + entity +
                '}';
    }
}
