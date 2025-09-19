package com.forum.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import jakarta.servlet.http.HttpServletRequest;

/**
 */
public class ServletUtil {
	private ServletUtil() {
	}

	public static String getJsonString(HttpServletRequest req) throws IOException {
		StringBuilder str = new StringBuilder();
		String line;
		try (Reader rd = req.getReader(); BufferedReader brd = new BufferedReader(rd)) {
			while ((line = brd.readLine()) != null) {
				str.append(line);
			}
		}
		return str.toString();
	}
}
