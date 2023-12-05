package com.fldsmdfr;

import org.json.JSONObject;

public class DataPackage {
    private String source;
    private String target;
    private String data;
    private String action;

    public DataPackage(String source, String target, String data, String action) {
        this.source = source;
        this.target = target;
        this.data = data;
        this.action = action;

    }

    public DataPackage(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        if(json.has("source")) {
            this.source = json.getString("source");
        }
        if(json.has("target")) {
            this.target = json.getString("target");
        }
        if(json.has("data")) {
            this.data = json.getString("data");
        }
        if(json.has("action")) {
            this.action = json.getString("action");
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public static JSONObject dataPackageToJSONObject(DataPackage dataPackage) {
        JSONObject jsonObject =  new JSONObject();
        jsonObject.put("source", dataPackage.getSource());
        jsonObject.put("target", dataPackage.getTarget());
        jsonObject.put("data", dataPackage.getData());
        jsonObject.put("action", dataPackage.getAction());
        return jsonObject;
    }

    public static String toString(DataPackage dataPackage) {
        return DataPackage.dataPackageToJSONObject(dataPackage).toString();
    }

    public static DataPackage toDataPackage(String jsonString) {
        return new DataPackage(jsonString);
    }

    @Override
    public String toString() {
        return DataPackage.toString(this);
    }
}
