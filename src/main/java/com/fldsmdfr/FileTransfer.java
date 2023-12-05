package com.fldsmdfr;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileTransfer {
    //static CopyOnWriteArrayList<FileInformation> colaTransferencia =  new CopyOnWriteArrayList<>();

    public static final int PART_SIZE = 1024;

    public static FileInformation readPart(FileInformation fileInformation) throws IOException {
        FileInformation fileInformationSend =  cloneFileInformation(fileInformation);
        try (RandomAccessFile archivo = new RandomAccessFile(fileInformation.filePathClient, "rws")) {
            // Mover el puntero a la posición especificada
            archivo.seek(fileInformation.sizeSend);

            // Leer los datos en la posición especificada
            byte[] buffer = new byte[PART_SIZE];
            int bytesRead = archivo.read(buffer);

            fileInformationSend.partNumber = fileInformation.partNumber + 1;
            fileInformationSend.sizePart = bytesRead;
            fileInformationSend.dataPart = buffer;
        }
        return fileInformationSend;
    }

    public static FileInformation writePart(FileInformation fileInformation) throws IOException {
        FileInformation fileInformationSend =  cloneFileInformation(fileInformation);
        try (RandomAccessFile archivo = new RandomAccessFile(fileInformation.filePathServer, "rws")) {
            // Mover el puntero a la posición especificada
            archivo.seek(fileInformation.sizeSend);
            archivo.write(fileInformation.dataPart, 0, (int) fileInformation.sizePart);

            sleepClient(100);

            fileInformationSend.sizeSend = fileInformation.sizeSend +  fileInformation.sizePart;

        }
        return fileInformationSend;
    }

    static FileInformation cloneFileInformation(FileInformation fileInformationIn) {
        FileInformation fileInformationOut = new FileInformation();
        fileInformationOut.idClient = fileInformationIn.idClient;
        fileInformationOut.idServer = fileInformationIn.idServer;
        fileInformationOut.name = fileInformationIn.name;
        fileInformationOut.size = fileInformationIn.size;
        fileInformationOut.partNumber = fileInformationIn.partNumber;
        fileInformationOut.partsTotal = fileInformationIn.partsTotal;

        fileInformationOut.sizeSend = fileInformationIn.sizeSend;

        fileInformationOut.filePathClient = fileInformationIn.filePathClient;
        fileInformationOut.filePathServer = fileInformationIn.filePathServer;
        return fileInformationOut;
    }



    private static void sleepClient(int sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

