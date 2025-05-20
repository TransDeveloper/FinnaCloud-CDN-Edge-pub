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

package com.finnacloud.cdn.content;

import com.finnacloud.cdn.http.HTTPRequest;
import com.finnacloud.cdn.http.HTTPResponse;
import com.finnacloud.cdn.MultiThreadedHttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class GetContent {
		private final HTTPRequest request;
		private final HTTPResponse response;

		private HashMap<String, String> settings = new HashMap<>(){{
				put("acs.listing", "deny");
		}};
		// todo: static for testing, use redis or some sort of ram cache for acl, replace

		public GetContent(HTTPRequest httpRequest) throws IOException {
				this.request = httpRequest;
				this.response = new HTTPResponse(httpRequest);

				String path = resolvePath();
				File file = new File(path);

				if (!file.exists()) {
						System.out.println("File does not exist: " + file.getAbsolutePath());
						response.sendResponse(404, new HashMap<>() {{
								put("error", "File not found");
								put("path", httpRequest.getPath());
						}});
						return;
				}

				if (file.isDirectory()) {
						handleDirectory(file);
				} else {
						handleFile(file);
				}
		}

		private String resolvePath() {
				String host = request.getHeader("Host");
				if (host != null && host.contains(":")) {
						host = host.substring(0, host.indexOf(":"));
				}
				String path = request.getPath();
				String fullPath = "/Users/sysadmin/Documents/Documents - MacBook Pro/" + path;
				System.out.println("Resolved path: " + fullPath);
				return fullPath;
		}

		private void handleDirectory(File directory) {
				if (settings.get("acs.listing").equals("deny")) {
						response.sendResponse(403, new HashMap<>() {{
								put("error", "Directory listing is denied.");
						}});
						return;
				}
				String[] files = directory.list();
				if (files != null) {
						System.out.println("Directory contents:");
						for (String fileName : files) {
								System.out.println(fileName);
						}
						response.sendResponse(200, new HashMap<>() {{
								put("message", "listing directory contents");
								put("files", files);
						}});
				} else {
						response.sendResponse(500, new HashMap<>() {{
								put("error", "Failed to list directory contents.");
						}});
				}
		}
		private String generateETag(File file) {
				return "\"" + file.lastModified() + "-" + file.length() + "\"";
		}
		private void handleFile(File file) throws IOException {
				String clientETag = request.getHeader("If-None-Match");
				String etag = MultiThreadedHttpServer.etagCache.computeIfAbsent(file.getAbsolutePath(), k -> generateETag(file));

				if (etag.equals(clientETag)) {
						response.sendResponse(304, ""); // Not Modified
						return;
				}

				// Optional: use cached content if available
				byte[] bytes = MultiThreadedHttpServer.fileCache.computeIfAbsent(file.getAbsolutePath(), k -> {
						try (FileInputStream fis = new FileInputStream(file)) {
								return fis.readAllBytes();
						} catch (IOException e) {
								e.printStackTrace();
								return null;
						}
				});

				if (bytes == null) {
						response.sendResponse(500, "Failed to read file");
						return;
				}

				String mimeType = java.nio.file.Files.probeContentType(file.toPath());
				if (mimeType == null) {
						mimeType = "application/octet-stream";
				}
				response.sendFileWithHeaders(bytes, mimeType, file.getName(), new HashMap<>(){{
						put("ETag", etag);
				}});

//				try (FileInputStream fis = new FileInputStream(file)) {
//						byte[] bytes = fis.readAllBytes();
//						String mimeType = java.nio.file.Files.probeContentType(file.toPath());
//						if (mimeType == null) {
//								mimeType = "application/octet-stream";
//						}
//						response.sendFile(bytes, mimeType, file.getName());
//				} catch (IOException e) {
//						e.printStackTrace();
//						response.sendResponse(500, new HashMap<>() {{
//								put("error", "Failed to read or send file.");
//						}});
//				}
		}
}

/*
Overall, we need to advance this and use FinnaCloud's Storage Cluster APIs
Save locally on POPs to cache it
serve from local pop cache (unless we detect content has been modified, which we can use webhooks from F/C:SC etc)
 */