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

package com.finnacloud.cdn.server;

import com.finnacloud.cdn.content.GetContent;
import com.finnacloud.cdn.http.HTTPRequest;

public class RequestManager {
		private HTTPRequest request;
		public RequestManager(HTTPRequest request) {
				this.request = request;
				processRequest();
		}
		public void processRequest() {
				// Process the request here

				try {
						new GetContent(request);
//						HTTPResponse httpResponse = new HTTPResponse(request);
//						httpResponse.sendResponse(200, new HashMap<String, String>() {{
//								put("message", "Hello, World!");
//						}});

//						OutputStream outputStream = request.getOutputStream();
//						String response = "HTTP/1.1 200 OK\r\n" +
//								"Content-Type: text/plain\r\n" +
//								"Content-Length: 13\r\n" +
//								"\r\n" +
//								"Hello, World!";
//						outputStream.write(response.getBytes());
//						outputStream.flush();
				} catch (Exception e) {
						e.printStackTrace();
				}
		}
}
