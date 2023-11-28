package com.fldsmdfr.event;

import java.util.EventListener;

public interface EventClientListener extends EventListener {
    void eventClientOccurred(EventClient evt);
}
