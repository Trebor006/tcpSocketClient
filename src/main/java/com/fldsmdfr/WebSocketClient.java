package com.fldsmdfr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WebSocketClient {
    public static void main(String[] args) {

        String serverAddress = "localhost";
        int serverPort = 8080;

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Conectado al servidor WebSocket en " + serverAddress + ":" + serverPort);

            // Envia un mensaje al servidor
            String message = "Hola, servidor WebSocket!";
            out.println(message);
            System.out.println("Mensaje enviado al servidor: " + message);

            // Puedes recibir respuestas del servidor aquí
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Respuesta del servidor: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}