package com.fldsmdfr;

import com.fldsmdfr.event.EventClient;
import com.fldsmdfr.event.EventClientManager;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class WebSocketClient extends Thread {

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
    private BufferedOutputStream bos;
    private volatile boolean started;

    private static final String ACTION_USERNAME = "USERNAME";
    private static final String ACTION_CONNECT_CLIENT = "CONNECT_CLIENT";
    private static final String ACTION_DISCONNECT_CLIENT = "DISCONNECT_CLIENT";
    private static final String ACTION_LIST_CLIENTS = "LIST_CLIENTS";
    private static final String ACTION_MESSAGE = "MESSAGE";
    private static final String ACTION_FILE = "FILE";

    private EventClientManager eventClientManager; // Notificacion a la vista

    public WebSocketClient() {
        this.started = false;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isStarted() {
        return started;
    }

    public void setEventClientManager(EventClientManager eventClientManager) {
        this.eventClientManager = eventClientManager;
    }

    public void startClient(String userName) {
        this.startClient(SERVER_ADDRESS_DEFAULT, SERVER_PORT_DEFAULT, userName);
    }

    public void startClient(String serverAddress, int serverPort, String userName) {
        try {
//            this.serverAddress = serverAddress;
//            this.serverPort = serverPort;
            this.started = true;
            this.clients = new HashMap<>();
            this.userName = userName;
            this.socket = new Socket(this.serverAddress, this.serverPort);
            this.id = socket.getLocalAddress().toString() + ":" + socket.getLocalPort();

            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.dis = new DataInputStream(socket.getInputStream());
            this.dout = new DataOutputStream(socket.getOutputStream());
            this.bos = new BufferedOutputStream(socket.getOutputStream());

            this.start();

            this.sendUserName();

            String log = "Cliente conectado al server " + this.serverAddress + ":" + this.serverPort + " {id: " + this.id + " - userName: " + this.userName + "}";
            this.notifyEventClient(log);
        } catch (IOException e) {
            this.started = false;
            String log = "Error al conectar al Servidor " + this.serverAddress + ":" + this.serverPort;
            this.notifyEventClient(log);
        }
    }

    public void stopClient() {
        this.started = false;
        try{this.out.close();} catch (Exception ignored) {}
        try{this.in.close();} catch (Exception ignored) {}
        try{this.dis.close();} catch (Exception ignored) {}
        try{this.dout.close();} catch (Exception ignored) {}
        try{this.bos.close();} catch (Exception ignored) {}
        // todo falta liberar recursos
        String log = "Desconectado del server " + this.serverAddress + ":" + this.serverPort + " {id: " + this.id + " - userName: " + this.userName + "}";
        this.notifyEventClient(log);
    }


    @Override
    public void run() {
        try {
            while (this.started) {
                String action = this.in.readLine();
                this.executeAction(action);
            }

        } catch (IOException e) {
            this.stopClient();
            e.printStackTrace();
        }
    }

    private void executeAction(String action) {
        System.out.println("Accion recibida: " + action);
        switch (action) {
            case ACTION_USERNAME: {
                sendUserName();
                break;
            }
            case ACTION_MESSAGE: {
                try {
                    String source = this.in.readLine();
                    String message = this.in.readLine();
                    System.out.println("source: " + source);
                    System.out.println("message: " + message);
                    JSONObject data = new JSONObject();
                    data.put("action", ACTION_MESSAGE);
                    data.put("id", this.id);
                    data.put("source", source);
                    data.put("target", id);
                    data.put("message", message);
                    this.notifyEventClient(data.toString());
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
                    JSONObject data = new JSONObject();
                    data.put("action", ACTION_LIST_CLIENTS);
                    data.put("id", this.id);
                    data.put("clients", listClients);
                    this.notifyEventClient(data.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            }
            case ACTION_CONNECT_CLIENT: {
                try {
                    String clientIdNew = this.in.readLine();
                    String clientUserNameNew = this.in.readLine();
                    String log =  "clientNew: " + clientIdNew + " - " + clientUserNameNew;
                    System.out.println(log);
                    clients.put(clientIdNew, clientUserNameNew);
                    this.notifyEventClient(log);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            }
            case ACTION_DISCONNECT_CLIENT: {
                try {
                    String clientId = this.in.readLine();
                    String log = "clientDisconect: " + clientId;
                    System.out.println(log);
                    clients.remove(clientId);
                    this.notifyEventClient(log);
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

    public void sendFile(String target, String filePath) {
        final File localFile = new File(filePath);
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localFile))) {
            System.out.println("Enviando File a " + target + ": " + filePath);
            out.println(ACTION_FILE);
            sleepClient(100);
            out.println(target);
            sleepClient(100);
            out.println(localFile.getName());
            sleepClient(100);
            out.println(localFile.length());
            sleepClient(100);

            byte[] byteArray = new byte[8192];
            int readByteArray;
            while ((readByteArray = bis.read(byteArray)) != -1) {
                bos.write(byteArray, 0, readByteArray);
            }
            bos.flush();
            bis.close();
            sleepClient(100);
            String log = "Archivo enviado " + filePath;
            this.notifyEventClient(log);
        } catch (IOException e) {
            String log = "Error al enviar el archivo " + filePath;
            this.notifyEventClient(log);
        }
    }

    private void sleepClient(int sleep) {
        try {
            sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyEventClient(String log) {
        if (eventClientManager == null) {
            return;
        }
        JSONObject data = new JSONObject();
        data.put("isStarted", this.started);
        data.put("log", log);
        eventClientManager.fireEventServer(new EventClient(this, data));
    }


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresar el Usuario: ");
        String userName = scanner.nextLine();

        WebSocketClient client = new WebSocketClient();
        client.startClient(userName);

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