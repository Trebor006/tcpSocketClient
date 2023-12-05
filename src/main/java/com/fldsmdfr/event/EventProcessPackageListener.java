package com.fldsmdfr.event;

import java.util.EventListener;

public interface EventProcessPackageListener extends EventListener {
    void eventProcessPackageOccurred(EventProcessPackage evt);
}
