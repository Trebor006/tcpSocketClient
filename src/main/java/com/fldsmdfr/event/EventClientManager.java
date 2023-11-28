package com.fldsmdfr.event;


import javax.swing.event.EventListenerList;

public class EventClientManager {
    protected EventListenerList listenerList = new EventListenerList();

    public void addEventListener(EventClientListener listener) {
        listenerList.add(EventClientListener.class, listener);
    }

    public void removeEventListener(EventClientListener listener) {
        listenerList.remove(EventClientListener.class, listener);
    }

    public void fireEventServer(EventClient evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == EventClientListener.class) {
                ((EventClientListener) listeners[i + 1]).eventClientOccurred(evt);
            }
        }
    }
}
