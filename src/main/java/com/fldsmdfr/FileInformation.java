package com.fldsmdfr;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileInformation {

    public static final int HEAD_FILE = 0;
    public static final int END_FILE = -1;

    public volatile String idClient;
    public volatile String idServer;
    public volatile String name;
    public volatile long size;
    public volatile int partNumber;
    public volatile int partsTotal;

    public volatile long sizeSend;
    public volatile long sizePart;
    public volatile byte[] dataPart;

    public volatile String filePathClient;
    public volatile String filePathServer;

    public FileInformation() {
        this.idClient = null;
        this.idServer = null;
        this.name = null;
        this.size = 0;
        this.partNumber = 0;
        this.partsTotal = 0;

        this.sizeSend = 0;
        this.sizePart = 0;
        this.dataPart = null;
    }

    public String toString() {
        JSONObject json = new JSONObject();
        json.put("idClient", this.idClient);
        json.put("idServer", this.idServer);
        json.put("name", this.name);
        json.put("size", this.size);
        json.put("partNumber", this.partNumber);
        json.put("partsTotal", this.partsTotal);
        json.put("sizeSend", this.sizeSend);
        json.put("sizePart", this.sizePart);
        json.put("filePathClient", this.filePathClient);
        json.put("filePathServer", this.filePathServer);
//        if(this.dataPart != null) {
//            Charset utf8charset = StandardCharsets.UTF_8;
//            Charset iso88591charset = StandardCharsets.ISO_8859_1;
//            ByteBuffer inputBuffer = ByteBuffer.wrap(this.dataPart);
//            // decode UTF-8
//            CharBuffer data = iso88591charset.decode(inputBuffer);
//            // encode ISO-8559-1
//            ByteBuffer outputBuffer = utf8charset.encode(data);
//            byte[] outputData = outputBuffer.array();
//            json.put("dataPart", new String(outputData, StandardCharsets.UTF_8));
//        }
        return json.toString();
    }

    public void toFileInformation(JSONObject json) {
        this.idClient = json.getString("idClient");
        this.idServer = json.getString("idServer");
        this.name = json.getString("name");
        this.size = json.getLong("size");
        this.partNumber = json.getInt("partNumber");
        this.partsTotal = json.getInt("partsTotal");
        this.sizeSend = json.getLong("sizeSend");
        this.sizePart = json.getLong("sizePart");
        this.filePathClient = json.getString("filePathClient");
        this.filePathServer = json.getString("filePathServer");
        this.dataPart = null;
//        if(json.has("dataPart") && !json.isNull("dataPart")) {
//            byte[] inputByte =  json.getString("dataPart").getBytes(StandardCharsets.UTF_8);
//            Charset utf8charset = StandardCharsets.UTF_8;
//            Charset iso88591charset = StandardCharsets.ISO_8859_1;
//            ByteBuffer inputBuffer = ByteBuffer.wrap(inputByte);
//            // decode UTF-8
//            CharBuffer data = utf8charset.decode(inputBuffer);
//            // encode ISO-8559-1
//            ByteBuffer outputBuffer =iso88591charset .encode(data);
//            byte[] outputData = outputBuffer.array();
//
//            this.dataPart = outputData;
//        } else {
//            this.dataPart = null;
//        }
    }



}
