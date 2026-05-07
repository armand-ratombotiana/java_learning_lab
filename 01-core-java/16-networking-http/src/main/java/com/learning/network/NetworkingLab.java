package com.learning.network;

import java.net.*;
import java.net.http.*;
import java.time.*;
import java.util.*;

public class NetworkingLab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Networking & HTTP Lab ===\n");

        System.out.println("1. URL & URI Parsing:");
        URI uri = new URI("https://api.example.com/users?id=123&sort=name#section");
        System.out.println("   Scheme: " + uri.getScheme());
        System.out.println("   Host: " + uri.getHost());
        System.out.println("   Path: " + uri.getPath());
        System.out.println("   Query: " + uri.getQuery());
        System.out.println("   Fragment: " + uri.getFragment());

        System.out.println("\n2. InetAddress:");
        InetAddress local = InetAddress.getLocalHost();
        System.out.println("   Local host: " + local.getHostName() + " (" + local.getHostAddress() + ")");
        InetAddress[] addresses = InetAddress.getAllByName("google.com");
        for (InetAddress addr : addresses) {
            System.out.println("   google.com -> " + addr.getHostAddress());
            break;
        }

        System.out.println("\n3. HTTP Client (Java 11+):");
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://httpbin.org/get"))
            .header("Accept", "application/json")
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();
        System.out.println("   Request: GET " + request.uri());

        HttpRequest postRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://httpbin.org/post"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"key\": \"value\"}"))
            .build();
        System.out.println("   Request: POST " + postRequest.uri());

        System.out.println("\n4. ServerSocket (TCP):");
        System.out.println("   ServerSocket server = new ServerSocket(port);");
        System.out.println("   Socket client = server.accept();");
        System.out.println("   BufferedReader in = new BufferedReader(");
        System.out.println("       new InputStreamReader(client.getInputStream()));");
        System.out.println("   PrintWriter out = new PrintWriter(client.getOutputStream(), true);");
        System.out.println("   String line = in.readLine();");
        System.out.println("   out.println(\"HTTP/1.1 200 OK\\r\\n\");");

        System.out.println("\n5. DatagramSocket (UDP):");
        System.out.println("   DatagramSocket socket = new DatagramSocket(port);");
        System.out.println("   byte[] buf = new byte[1024];");
        System.out.println("   DatagramPacket packet = new DatagramPacket(buf, buf.length);");
        System.out.println("   socket.receive(packet);");
        System.out.println("   String msg = new String(packet.getData(), 0, packet.getLength());");

        System.out.println("\n6. URLConnection:");
        System.out.println("   URL url = new URL(\"https://example.com\");");
        System.out.println("   URLConnection conn = url.openConnection();");
        System.out.println("   conn.setRequestProperty(\"User-Agent\", \"Mozilla/5.0\");");
        System.out.println("   conn.connect();");
        System.out.println("   BufferedReader rd = new BufferedReader(");
        System.out.println("       new InputStreamReader(conn.getInputStream()));");
        System.out.println("   String line; while ((line = rd.readLine()) != null) { ... }");

        System.out.println("\n7. WebSocket:");
        System.out.println("   WebSocket.Listener listener = new WebSocket.Listener() {");
        System.out.println("       onOpen(webSocket) { webSocket.request(1); }");
        System.out.println("       onText(webSocket, data, last) {");
        System.out.println("           System.out.println(\"Received: \" + data);");
        System.out.println("           webSocket.request(1);");
        System.out.println("       }");
        System.out.println("   };");
        System.out.println("   HttpClient wsClient = HttpClient.newHttpClient();");
        System.out.println("   wsClient.newWebSocketBuilder()");
        System.out.println("       .buildAsync(URI.create(\"ws://echo.websocket.org\"), listener);");

        System.out.println("\n8. Network Interfaces:");
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            System.out.println("   Interface: " + ni.getName() + " (" + ni.getDisplayName() + ")");
            byte[] mac = ni.getHardwareAddress();
            if (mac != null) {
                StringBuilder sb = new StringBuilder();
                for (byte b : mac) sb.append(String.format("%02X:", b));
                System.out.println("   MAC: " + sb.substring(0, sb.length() - 1));
            }
        }

        System.out.println("\n=== Networking Lab Complete ===");
    }
}