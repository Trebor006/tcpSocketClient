package com.fldsmdfr.event;


import javax.swing.event.EventListenerList;

public class EventReceivePackageManager {
    protected EventListenerList listenerList = new EventListenerList();

    public void addEventListener(EventReceivePackageListener listener) {
        listenerList.add(EventReceivePackageListener.class, listener);
    }

    public void removeEventListener(EventReceivePackageListener listener) {
        listenerList.remove(EventReceivePackageListener.class, listener);
    }

    public void fireEventReceivePackage(EventReceivePackage evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == EventReceivePackageListener.class) {
                ((EventReceivePackageListener) listeners[i + 1]).eventReceivePackageOccurred(evt);
            }
        }
    }
}
