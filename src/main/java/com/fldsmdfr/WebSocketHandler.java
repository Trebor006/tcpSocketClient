package com.fldsmdfr;

import java.io.*;
import java.net.Socket;

public class WebSocketHandler extends Thread {
    private final Socket clientSocket;
    private String id;
    private String userName;
    private PrintWriter out;
    private BufferedReader in;
    private DataInputStream dis;
    private DataOutputStream dout;

    private static final String ACTION_USERNAME = "USERNAME";
    private static final String ACTION_CONNECT_CLIENT = "CONNECT_CLIENTE";
    private static final String ACTION_DISCONNECT_CLIENT = "DISCONNECT_CLIENT";
    private static final String ACTION_LIST_CLIENTS = "LIST_CLIENTS";
    private static final String ACTION_MESSAGE = "MESSAGE";
    private static final String ACTION_FILE = "FILE";


    public WebSocketHandler(Socket socket, String id) throws IOException {
        this.clientSocket = socket;
        this.id = id;

        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.dis = new DataInputStream(clientSocket.getInputStream());
        this.dout = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void run() {
        try {
            while(true) {
                String action = this.in.readLine();
                this.executeAction(action);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeAction(String action) {
        System.out.println("Accion recibida: " + action);
        switch (action) {
            case ACTION_USERNAME : {
                try {
                    this.userName = this.in.readLine();
                    System.out.println("userName: " + this.userName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case ACTION_MESSAGE: {
                try {
                    String target = this.in.readLine();
                    String message = this.in.readLine();
                    System.out.println("target: " + target);
                    System.out.println("message: " + message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            }
        }
    }

    private void sendUserName() {

    }

    private void sendMessage() {

    }

    private void sendListClients() {

    }
}
