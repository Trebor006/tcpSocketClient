package com.fldsmdfr.event;

import com.fldsmdfr.DataPackage;

import java.util.EventObject;

public class EventProcessPackage extends EventObject {

    private DataPackage data;

    public EventProcessPackage(Object source, DataPackage data) {
        super(source);
        this.data = data;
    }

    public DataPackage getData() {
        return data;
    }

    public void setData(DataPackage data) {
        this.data = data;
    }
}
