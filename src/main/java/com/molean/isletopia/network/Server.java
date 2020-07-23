package com.molean.isletopia.network;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private Map<String, InetSocketAddress> registrations = new HashMap<>();

    public Thread thread;

    public void closeNetwork() {
        thread.interrupt();
    }

    public boolean setupNetwork() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(6129);
        } catch (IOException e) {
            return false;
        }
        thread = new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    handleSocket(socket);
                } catch (IOException ignored) {
                }
            }
        });
        thread.start();
        return true;
    }

    private void handleSocket(Socket socket) {
        new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();
                String requestString = new String(inputStream.readAllBytes());
                Request request = new Gson().fromJson(requestString, Request.class);
                Response response = handleRequst(request);
                String responseString = new Gson().toJson(response);
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(responseString.getBytes());
                socket.close();
            } catch (IOException ignored) {

            }
        }).start();
    }

    private Response handleRequst(Request request) throws IOException {
        Response response = new Response();
        if (request.getTarget().equalsIgnoreCase("IsletopiaNetwork")) {
            if (request.getType().equalsIgnoreCase("register")) {
                String name = request.getData().get("name");
                String hostname = request.getData().get("hostname");
                int port = Integer.parseInt(request.getData().get("port"));
                InetSocketAddress address = new InetSocketAddress(hostname, port);
                registrations.put(name, address);
                response.setStatus("successfully");
            } else if (request.getType().equalsIgnoreCase("unregister")) {
                String name = request.getData().get("name");
                registrations.remove(name);
                response.setStatus("successfully");
            } else {
                response.setStatus("no such operation");
            }
        } else {
            String target = request.getTarget();
            InetSocketAddress address = registrations.get(target);
            if (address == null) {
                response.setStatus("no such target");
                return response;
            }
            String hostName = registrations.get(target).getHostName();
            int port = registrations.get(target).getPort();
            response = Client.send(hostName, port, request);
        }
        return response;
    }
}
