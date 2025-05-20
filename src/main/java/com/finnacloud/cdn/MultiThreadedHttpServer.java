/*
 * Copyright (c) 2025 TheFinnaCompany Ltd. All rights reserved.
 *
 * By accessing, using, or modifying this file, you acknowledge and agree that:
 * 1. All intellectual property rights, including copyright, are owned by TheFinnaCompany Ltd.
 * 2. This file is intended for internal use only within TheFinnaCompany Ltd, and any unauthorized use, distribution, or modification is prohibited.
 * 3. Any contributions made to this file, whether through code, documentation, or other content, are considered the exclusive property of TheFinnaCompany Ltd.
 * 4. Internal agreements, including confidentiality and non-disclosure obligations, apply to all use of this file.
 * 5. Unauthorized disclosure or use of this file outside of TheFinnaCompany Ltd may result in legal consequences.
 *
 * For inquiries or clarifications, please refer to the internal policies or contact corp@finnacloud.net
 */

package com.finnacloud.cdn;

import com.finnacloud.cdn.http.HTTPRequest;
import com.finnacloud.cdn.server.RequestManager;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class MultiThreadedHttpServer {
    public static Map<String, byte[]> fileCache = new HashMap<>();
    public static Map<String, String> etagCache = new HashMap<>();
    // instance
    private static MultiThreadedHttpServer instance;

    public static void main(String[] args) throws IOException {
        int port = 8080; // HTTP port
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start(); // spawn a new thread
        }
    }

    public MultiThreadedHttpServer() {
        // Singleton pattern
        if (instance == null) {
            instance = this;
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    OutputStream out = clientSocket.getOutputStream()
            ) {
                HTTPRequest request = new HTTPRequest(in,out);
                new RequestManager(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // get instnace
    public MultiThreadedHttpServer getInstance() {
        return instance;
    }
}