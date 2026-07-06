package com.javaacademy.lab32.networking;

import java.io.*;
import java.net.*;

public class UrlConnectionExample {

    public String fetchContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", "JavaLab32");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    public int getContentLength(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getContentLength();
    }

    public String getContentType(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getContentType();
    }

    public long getLastModified(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getLastModified();
    }
}
