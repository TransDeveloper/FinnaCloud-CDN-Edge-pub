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

import com.google.gson.Gson;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HTTPResponse {
    private static final Gson gson = new Gson();
    private static final Map<Integer, String> statusMessages = new HashMap<>();

    static {
        statusMessages.put(200, "OK");
        statusMessages.put(400, "Bad Request");
        statusMessages.put(404, "Not Found");
        statusMessages.put(500, "Internal Server Error");
        // add more if needed
    }

    private final HTTPRequest request;

    public HTTPResponse(HTTPRequest request) {
        this.request = request;
    }

    private static int responseLength(String response) {
        return response.getBytes(StandardCharsets.UTF_8).length;
    }

    private static String convertToJson(Object response) {
        return gson.toJson(response);
    }

    public void sendResponse(int statusCode, Object response) {
        String responseBody;
        String contentType;

        if (response instanceof String) {
            responseBody = (String) response;
            contentType = "text/plain";
        } else if (response instanceof byte[]) {
            responseBody = new String((byte[]) response, StandardCharsets.UTF_8);
            contentType = "application/octet-stream";
        } else {
            responseBody = convertToJson(response);
            contentType = "application/json";
        }

        int contentLength = responseLength(responseBody);
        String statusText = statusMessages.getOrDefault(statusCode, "OK");

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");
        responseBuilder.append("Content-Type: ").append(contentType).append("\r\n");
        responseBuilder.append("Content-Length: ").append(contentLength).append("\r\n");
        responseBuilder.append("Server: FinnaCloud \r\n");
        responseBuilder.append("Connection: close\r\n");
        responseBuilder.append("\r\n");
        responseBuilder.append(responseBody);

        try {
            OutputStream out = request.getOutputStream();
            out.write(responseBuilder.toString().getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

		public void sendFileWithHeaders(byte[] fileBytes, String mimeType, String filename, HashMap<String, String> headers) {
				try {
						if (fileBytes == null || fileBytes.length == 0) {
								sendResponse(404, "File not found or empty");
								return;
						}

						if (mimeType == null) {
								mimeType = "application/octet-stream";
						}

						String statusText = statusMessages.getOrDefault(200, "OK");
						StringBuilder responseBuilder = new StringBuilder();
						responseBuilder.append("HTTP/1.1 200 ").append(statusText).append("\r\n");
						responseBuilder.append("Content-Type: ").append(mimeType).append("\r\n");
						responseBuilder.append("Content-Disposition: inline; filename=\"").append(filename).append("\"\r\n");
						responseBuilder.append("Content-Length: ").append(fileBytes.length).append("\r\n");
						responseBuilder.append("Server: FinnaCloud").append("\r\n");
						// add custom headers
						headers.forEach((k, v) -> responseBuilder.append(k).append(": ").append(v).append("\r\n"));
//						headers.forEach((k, v) -> responseBuilder.append("\t").append(k).append(": ").append(v).append("\r\n"));
						responseBuilder.append("Connection: close\r\n");
						responseBuilder.append("\r\n");

						OutputStream out = request.getOutputStream();
						out.write(responseBuilder.toString().getBytes(StandardCharsets.UTF_8));
						out.write(fileBytes);
						out.flush();
				} catch (Exception e) {
						e.printStackTrace();
				}
		}

		public void sendFile(byte[] fileBytes, String mimeType, String filename) {
				sendFileWithHeaders(fileBytes, mimeType, filename, new HashMap<>());
		}

//    public void sendFile(byte[] fileBytes, String mimeType, String filename) {
//        try {
//            if (fileBytes == null || fileBytes.length == 0) {
//                sendResponse(404, "File not found or empty");
//                return;
//            }
//
//            if (mimeType == null) {
//                mimeType = "application/octet-stream";
//            }
//
//            String statusText = statusMessages.getOrDefault(200, "OK");
//            StringBuilder responseBuilder = new StringBuilder();
//            responseBuilder.append("HTTP/1.1 200 ").append(statusText).append("\r\n");
//            responseBuilder.append("Content-Type: ").append(mimeType).append("\r\n");
//            responseBuilder.append("Content-Disposition: inline; filename=\"").append(filename).append("\"\r\n");
//            responseBuilder.append("Content-Length: ").append(fileBytes.length).append("\r\n");
//		        responseBuilder.append("Server: FinnaCloud \r\n");
//            responseBuilder.append("Connection: close\r\n");
//            responseBuilder.append("\r\n");
//
//            OutputStream out = request.getOutputStream();
//            out.write(responseBuilder.toString().getBytes(StandardCharsets.UTF_8));
//            out.write(fileBytes);
//            out.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//            sendResponse(500, "Internal Server Error");
//        }
//    }
}
