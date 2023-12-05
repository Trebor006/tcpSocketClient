package com.fldsmdfr.event;


import javax.swing.event.EventListenerList;

public class EventProcessPackageManager {
    protected EventListenerList listenerList = new EventListenerList();

    public void addEventListener(EventProcessPackageListener listener) {
        listenerList.add(EventProcessPackageListener.class, listener);
    }

    public void removeEventListener(EventProcessPackageListener listener) {
        listenerList.remove(EventProcessPackageListener.class, listener);
    }

    public void fireEventProcessPackage(EventProcessPackage evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == EventProcessPackageListener.class) {
                ((EventProcessPackageListener) listeners[i + 1]).eventProcessPackageOccurred(evt);
            }
        }
    }
}
