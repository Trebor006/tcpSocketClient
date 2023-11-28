package com.fldsmdfr.view;

import com.fldsmdfr.FTPConfiguration;
import com.fldsmdfr.WebSocketClient;
import com.fldsmdfr.event.EventClient;
import com.fldsmdfr.event.EventClientListener;
import com.fldsmdfr.event.EventClientManager;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

public class WebSocketClientView implements EventClientListener {
    private JPanel jPanel;
    private JTextField textFieldIP;
    private JTextField textFieldPort;
    private JButton buttonStatus;
    private JButton buttonSubirFile;
    private JTextArea textAreaLog;
    private JLabel labelIP;
    private JLabel labelPort;

    JFileChooser fileChooser = new JFileChooser();

    WebSocketClient socketClient;
    EventClientManager eventClientManager;

    public WebSocketClientView() {
        textFieldIP.setText(FTPConfiguration.ipFTP);
        textFieldPort.setText(FTPConfiguration.portFTP + "");



        eventClientManager = new EventClientManager();
        eventClientManager.addEventListener(this);
        socketClient = new WebSocketClient();
        socketClient.setEventClientManager(eventClientManager);
        buttonSubirFile.setEnabled(false);

        buttonStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonStatus.setEnabled(false);
                if(socketClient.isStarted()) {
                    buttonStatus.setText("Deteniendo");
                    socketClient.stopClient();
                    return;
                }

                int port;
                try {
                    port = Integer.parseInt(textFieldPort.getText());
                } catch (Exception exception) {
                    buttonStatus.setEnabled(false);
                    JOptionPane.showMessageDialog(null, "El puerto es un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String ip = textFieldIP.getText();

                buttonStatus.setText("Iniciando");
                FTPConfiguration.portFTP = port;
                FTPConfiguration.ipFTP = ip;

                socketClient = new WebSocketClient();
                socketClient.setEventClientManager(eventClientManager);
                socketClient.setServerPort(port);
                socketClient.setServerAddress(ip);
                String userName = new Date().getTime() + "";
                socketClient.startClient(userName);
            }
        });

        buttonSubirFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String filePath = selectedFile.getPath();
                    String fileAbsolutePath = selectedFile.getAbsolutePath();
                    System.out.println("Archivo seleccionado File Path: " + filePath);
                    System.out.println("Archivo seleccionado File Absolute Path: " + fileAbsolutePath);
                    socketClient.sendFile("server", filePath);
                } else {
                    System.out.println("No se seleccionó ningún archivo.");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("WebSocketClient");
        frame.setContentPane(new WebSocketClientView().jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void eventClientOccurred(EventClient evt) {
        JSONObject data = evt.getData();

        // Paint Boton status
        if(socketClient.isStarted()) {
            buttonStatus.setText("Detener");
            buttonSubirFile.setEnabled(true);
        } else {
            buttonStatus.setText("Iniciar");
            buttonSubirFile.setEnabled(false);
        }
        buttonStatus.setEnabled(true);

        // Paint Log
        String log = data.getString("log");
        textAreaLog.append(log + '\n');
    }
}
