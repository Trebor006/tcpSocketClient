package com.fldsmdfr;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DataPackage {
    private String source;
    private String target;
    private String data;
    private byte[] dataPart;
    private String action;

    public DataPackage(String source, String target, String data, String action, byte[] dataPart) {
        this.source = source;
        this.target = target;
        this.data = data;
        this.action = action;
        this.dataPart = dataPart;
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
        if(json.has("dataPart") && !json.isNull("dataPart")) {
            JSONArray dataPartj = json.getJSONArray("dataPart");
            byte[] inputByte =  new byte[dataPartj.length()];
            for (int i = 0; i <dataPartj.length(); i++) {
                inputByte[i] = (byte) dataPartj.getInt(i);
            }

            //byte[] inputByte =  json.getString("dataPart").getBytes(StandardCharsets.UTF_8);
            Charset utf8charset = StandardCharsets.UTF_8;
            Charset iso88591charset = StandardCharsets.ISO_8859_1;
            ByteBuffer inputBuffer = ByteBuffer.wrap(inputByte);
            // decode UTF-8
            CharBuffer data = utf8charset.decode(inputBuffer);
            // encode ISO-8559-1
            ByteBuffer outputBuffer =iso88591charset .encode(data);
            byte[] outputData = outputBuffer.array();

            this.dataPart = outputData;
        } else {
            this.dataPart = null;
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

    public byte[] getDataPart() {
        return dataPart;
    }

    public void setDataPart(byte[] dataPart) {
        this.dataPart = dataPart;
    }

    public static JSONObject dataPackageToJSONObject(DataPackage dataPackage) {
        JSONObject jsonObject =  new JSONObject();
        jsonObject.put("source", dataPackage.getSource());
        jsonObject.put("target", dataPackage.getTarget());
        jsonObject.put("data", dataPackage.getData());
        jsonObject.put("action", dataPackage.getAction());
        if(dataPackage.getDataPart() != null) {
            Charset utf8charset = StandardCharsets.UTF_8;
            Charset iso88591charset = StandardCharsets.ISO_8859_1;
            ByteBuffer inputBuffer = ByteBuffer.wrap(dataPackage.getDataPart());
            // decode UTF-8
            CharBuffer data = iso88591charset.decode(inputBuffer);
            // encode ISO-8559-1
            ByteBuffer outputBuffer = utf8charset.encode(data);
            byte[] outputData = outputBuffer.array();
            JSONArray dataPartj =  new JSONArray();
            for (int i = 0; i <outputData.length; i++) {
                dataPartj.put(Byte.toUnsignedInt(outputData[i]));
            }
            jsonObject.put("dataPart", dataPartj);
            //jsonObject.put("dataPart", new String(outputData, StandardCharsets.UTF_8));
        }
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
