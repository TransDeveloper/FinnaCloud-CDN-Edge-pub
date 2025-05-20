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

package com.finnacloud.cdn.http;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class HTTPRequest implements HTTPRequestInterface {

    private String method;
    private String path;
    private String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private String body = "";
		private OutputStream outputStream;

    public HTTPRequest(BufferedReader httpRequestStream, OutputStream httpResponseStream) {
        try {
            String requestLine = httpRequestStream.readLine();
            if (requestLine != null && !requestLine.isEmpty()) {
                String[] parts = requestLine.split(" ");
                if (parts.length >= 3) {
                    this.method = parts[0];
                    this.path = parts[1];
                    this.protocol = parts[2];
                }
            }

            String line;
            while ((line = httpRequestStream.readLine()) != null && !line.isEmpty()) {
                int colonIndex = line.indexOf(":");
                if (colonIndex > 0) {
                    String name = line.substring(0, colonIndex).trim();
                    String value = line.substring(colonIndex + 1).trim();
                    headers.put(name, value);
                    System.out.println("Header: " + name + " = " + value);
                }
            }

            StringBuilder bodyBuilder = new StringBuilder();
            while (httpRequestStream.ready() && (line = httpRequestStream.readLine()) != null) {
                bodyBuilder.append(line).append("\n");
            }
            body = bodyBuilder.toString().trim();
						this.outputStream = httpResponseStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

		public OutputStream getOutputStream() {
				return outputStream;
		}
}
