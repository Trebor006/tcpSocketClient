package com.fldsmdfr;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class WebSocketClient extends Thread{

    private String serverAddress;
    private int serverPort;

    private volatile HashMap<String, String> clients;

    private static final String SERVER_ADDRESS_DEFAULT = "localhost";
    private static final int SERVER_PORT_DEFAULT = 8080;

    private Socket socket;
    private String id;
    private String userName;
    private PrintWriter out;
    private BufferedReader in;
    private DataInputStream dis;
    private DataOutputStream dout;
    private volatile boolean started;

    private static final String ACTION_USERNAME = "USERNAME";
    private static final String ACTION_CONNECT_CLIENT = "CONNECT_CLIENT";
    private static final String ACTION_DISCONNECT_CLIENT = "DISCONNECT_CLIENT";
    private static final String ACTION_LIST_CLIENTS = "LIST_CLIENTS";
    private static final String ACTION_MESSAGE = "MESSAGE";
    private static final String ACTION_FILE = "FILE";

    public WebSocketClient() {
        this.started = false;
    }

    public void startClient(String userName) throws IOException {
        this.startClient(SERVER_ADDRESS_DEFAULT, SERVER_PORT_DEFAULT, userName);
    }

    public void startClient(String serverAddress, int serverPort, String userName) throws IOException {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.started = true;
        this.clients = new HashMap<>();
        this.userName = userName;
        this.socket = new Socket(this.serverAddress, this.serverPort);
        this.id = socket.getLocalAddress().toString() + ":" + socket.getLocalPort();

        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.dis = new DataInputStream(socket.getInputStream());
        this.dout = new DataOutputStream(socket.getOutputStream());

        this.start();

        this.sendUserName();
    }


    @Override
    public void run() {
        try {
            while(this.started) {
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
                sendUserName();
                break;
            }
            case ACTION_MESSAGE: {
                try {
                    String source = this.in.readLine();
                    String message = this.in.readLine();
                    System.out.println("source: " + source);
                    System.out.println("message: " + message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            }
            case ACTION_LIST_CLIENTS: {
                try {
                    String listClients = this.in.readLine();
                    System.out.println("clientes: " + listClients);
                    JSONObject jsonClients = new JSONObject(listClients);
                    Iterator<String> keys = jsonClients.keys();
                    clients = new HashMap<>();
                    while (keys.hasNext()) {
                        String idCliente = keys.next();
                        clients.put(idCliente, jsonClients.getString(idCliente));
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            }
            case ACTION_CONNECT_CLIENT: {
                try {
                    String clientIdNew = this.in.readLine();
                    String clientUserNameNew = this.in.readLine();
                    System.out.println("clientNew: " + clientIdNew + " - "  + clientUserNameNew);
                    clients.put(clientIdNew, clientUserNameNew);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            }
        }
    }


    private void sendUserName() {
        System.out.println("Enviando USERNAME: " + this.userName);
        out.println(ACTION_USERNAME);
        sleepClient(100);
        out.println(this.userName);
    }

    public void sendMessage(String target, String message) {
        System.out.println("Enviando MENSAJE a " + target + ": " + message);
        out.println(ACTION_MESSAGE);
        sleepClient(100);
        out.println(target);
        sleepClient(100);
        out.println(message);
    }

    private void sleepClient(int sleep) {
        try {
            sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresar el Usuario: ");
        String userName = scanner.nextLine();

        WebSocketClient client = new WebSocketClient();
        try {
            client.startClient(userName);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        while (true) {
            System.out.println("Ingresar la opcion: ");
            String opcion = scanner.nextLine();
            switch (opcion) {
                case "salir":
                    return;
                case "mensaje": {
                    System.out.println("Ingresar el Destino: ");
                    String target = scanner.nextLine();
                    System.out.println("Ingresar el Mensaje: ");
                    String message = scanner.nextLine();
                    client.sendMessage(target, message);
                    break;
                }
            }

        }

//        String serverAddress = "localhost";
//        int serverPort = 8080;
//
//        try (Socket socket = new Socket(serverAddress, serverPort);
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//
//            System.out.println("Conectado al servidor WebSocket en " + serverAddress + ":" + serverPort);
//
//            // Envia un mensaje al servidor
//            String message = "Hola, servidor WebSocket!";
//            out.println(message);
//            System.out.println("Mensaje enviado al servidor: " + message);
//
//            Thread.sleep(5000);
//
//            message = "otro mensaje, servidor WebSocket!";
//            out.println(message);
//            System.out.println("Mensaje enviado al servidor: " + message);
//
//            Thread.sleep(5000);
//
//            message = "otro mensaje, servidor WebSocket!";
//            out.println(message);
//            System.out.println("Mensaje enviado al servidor: " + message);
//
//            // Puedes recibir respuestas del servidor aquí
//            String response;
//            while ((response = in.readLine()) != null) {
//                System.out.println("Respuesta del servidor: " + response);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}