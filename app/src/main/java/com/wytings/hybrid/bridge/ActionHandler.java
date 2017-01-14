package com.wytings.hybrid.bridge;

import org.json.JSONObject;

/**
 * Created by rex on 12/31/16.
 */

public interface ActionHandler {
    void handle(JSONObject entity, CompleteListener listener);
}
