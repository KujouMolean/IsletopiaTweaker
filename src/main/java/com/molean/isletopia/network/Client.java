package com.molean.isletopia.network;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

public class Client {

    private Thread thread;

    private ServerSocket serverSocket;

    private Function<Request, Response> requstHandler;

    public Client(Function<Request, Response> requstHandler) {
        this.requstHandler = requstHandler;
    }

    public static Response send(Request request) {
        try {
            Socket socket = new Socket("localhost", 6129);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(new Gson().toJson(request).getBytes());
            socket.shutdownOutput();
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            String responseString = new String(bytes);
            Response response = new Gson().fromJson(responseString, Response.class);
            return response;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Response send(String hostname, int port, Request request) {
        try {
            Socket socket = new Socket(hostname, port);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(new Gson().toJson(request).getBytes());
            socket.shutdownOutput();
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            String responseString = new String(bytes);
            Response response = new Gson().fromJson(responseString, Response.class);
            return response;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void register(String name) {
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        Request request = new Request();
        thread = new Thread(() -> listen());
        thread.start();
        request.setType("register");
        request.setTarget("IsletopiaNetwork");
        request.getData().put("hostname", "localhost");
        request.getData().put("name", name);
        request.getData().put("port", serverSocket.getLocalPort() + "");
        Response response = send("localhost", 6129, request);
    }

    public void unregister(String name) throws IOException {
        thread.interrupt();
        serverSocket.close();
        Request request = new Request();
        request.setType("unregister");
        request.setTarget("IsletopiaNetwork");
        request.getData().put("name", name);
        Response response = send("localhost", 6129, request);
    }

    private void listen() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                InputStream inputStream = socket.getInputStream();
                String requestString = new String(inputStream.readAllBytes());
                Request request = new Gson().fromJson(requestString, Request.class);
                Response response = requstHandler.apply(request);
                String responseString = new Gson().toJson(response);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(responseString.getBytes());
                socket.close();
            } catch (IOException ignored) {

            }
        }
    }
}
