package com.fldsmdfr.event;

import java.util.EventObject;

public class EventReceivePackage extends EventObject {

    private String data;

    public EventReceivePackage(Object source, String data) {
        super(source);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
