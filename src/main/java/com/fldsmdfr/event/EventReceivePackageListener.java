package com.fldsmdfr.event;

import java.util.EventListener;

public interface EventReceivePackageListener extends EventListener {
    void eventReceivePackageOccurred(EventReceivePackage evt);
}
