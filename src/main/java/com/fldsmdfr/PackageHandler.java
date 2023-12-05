package com.fldsmdfr;

import com.fldsmdfr.event.*;
import org.json.JSONObject;

import java.io.IOException;

public class PackageHandler extends Thread {

    private String packageString;
    private EventProcessPackageManager eventProcessPackageManager;

    public PackageHandler(String packageString, EventProcessPackageManager eventProcessPackageManage) {
        this.packageString = packageString;
        this.eventProcessPackageManager = eventProcessPackageManage;
    }

    @Override
    public void run() {
        DataPackage dataPackage = DataPackage.toDataPackage(packageString);
        executeAction(dataPackage);

    }

    private void executeAction(DataPackage dataPackage) {
        System.out.println("Accion recibida: " + dataPackage.getAction());
        switch (dataPackage.getAction()) {
            case Protocol.ACTION_USERNAME: {
                notifyEventProcessPackage(dataPackage);
                break;
            }
            case Protocol.ACTION_MESSAGE: {
                notifyEventProcessPackage(dataPackage);
                break;
            }
            case Protocol.ACTION_LIST_CLIENTS: {
                notifyEventProcessPackage(dataPackage);
                break;
            }
            case Protocol.ACTION_CONNECT_CLIENT: {
                notifyEventProcessPackage(dataPackage);
                break;
            }
            case Protocol.ACTION_DISCONNECT_CLIENT: {
                notifyEventProcessPackage(dataPackage);
                break;
            }
            case Protocol.ACTION_FILE_PART: {
                try {
                    FileInformation fileInformation = new FileInformation();
                    fileInformation.toFileInformation(new JSONObject(dataPackage.getData()));
                    FileInformation fileInformationOut = FileTransfer.readPart(fileInformation);
                    DataPackage dataPackageSend = new DataPackage(dataPackage.getSource(), dataPackage.getTarget(), fileInformationOut.toString(), Protocol.ACTION_FILE_PART, fileInformationOut.dataPart);
                    notifyEventProcessPackage(dataPackageSend);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            }
            case Protocol.ACTION_FILE_END: {
                notifyEventProcessPackage(dataPackage);
                break;
            }


        }
    }

    public void notifyEventProcessPackage(DataPackage dataPackage) {
        if (this.eventProcessPackageManager == null) {
            return;
        }
        eventProcessPackageManager.fireEventProcessPackage(new EventProcessPackage(this, dataPackage));
    }
}


