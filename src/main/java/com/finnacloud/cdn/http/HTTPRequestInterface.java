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

public interface HTTPRequestInterface {
		String getMethod();

		String getPath();

		String getProtocol();

		String getHeader(String name);

		String getBody();

		void setMethod(String method);

		void setPath(String path);

		void setProtocol(String protocol);

		void setHeader(String name, String value);

		void setBody(String body);

		void addHeader(String name, String value);
}
