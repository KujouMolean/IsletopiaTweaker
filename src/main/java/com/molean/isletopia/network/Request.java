package com.molean.isletopia.network;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String target = "";
    private String type = "";


    public Request(String target, String type) {
        this.target = target;
        this.type = type;
    }

    public Request() {

    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    private Map<String, String> data = new HashMap<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public void set(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }
}
