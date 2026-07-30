package com.security.deep.lab03;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class PenetrationTesting {

    public static Map<Integer, String> portScan(String host, int startPort, int endPort, int timeoutMs) {
        Map<Integer, String> openPorts = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(200);
        List<Future<Void>> futures = new ArrayList<>();
        for (int port = startPort; port <= endPort; port++) {
            final int p = port;
            futures.add(executor.submit(() -> {
                try (Socket s = new Socket()) {
                    s.connect(new InetSocketAddress(host, p), timeoutMs);
                    openPorts.put(p, getServiceName(p));
                } catch (Exception ignored) {}
                return null;
            }));
        }
        for (Future<Void> f : futures) {
            try { f.get(30, TimeUnit.SECONDS); } catch (Exception ignored) {}
        }
        executor.shutdown();
        return openPorts;
    }

    public static String getServiceName(int port) {
        return switch (port) {
            case 21 -> "FTP";
            case 22 -> "SSH";
            case 23 -> "Telnet";
            case 25 -> "SMTP";
            case 53 -> "DNS";
            case 80 -> "HTTP";
            case 110 -> "POP3";
            case 143 -> "IMAP";
            case 443 -> "HTTPS";
            case 445 -> "SMB";
            case 3306 -> "MySQL";
            case 3389 -> "RDP";
            case 5432 -> "PostgreSQL";
            case 6379 -> "Redis";
            case 8080 -> "HTTP-Proxy";
            case 8443 -> "HTTPS-Alt";
            case 27017 -> "MongoDB";
            default -> "Unknown-" + port;
        };
    }

    public static String bannerGrab(String host, int port, int timeoutMs) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), timeoutMs);
            s.setSoTimeout(timeoutMs);
            var out = s.getOutputStream();
            var in = s.getInputStream();
            out.write("HELP\r\n".getBytes());
            out.flush();
            byte[] buf = new byte[1024];
            int n = in.read(buf);
            if (n > 0) return new String(buf, 0, n).trim();
        } catch (Exception ignored) {}
        return null;
    }

    public static List<String> extractEmails(String text) {
        List<String> emails = new ArrayList<>();
        Pattern p = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher m = p.matcher(text);
        while (m.find()) emails.add(m.group());
        return emails;
    }

    public static List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();
        Pattern p = Pattern.compile("https?://[\\w./?=&%-]+");
        Matcher m = p.matcher(text);
        while (m.find()) urls.add(m.group());
        return urls;
    }

    public static String cveLookup(String cveId) {
        return switch (cveId.toUpperCase()) {
            case "CVE-2023-44487" -> "HTTP/2 Rapid Reset (Critical, CVSS 9.8)";
            case "CVE-2021-44228" -> "Log4Shell (Critical, CVSS 10.0)";
            case "CVE-2022-22965" -> "Spring4Shell (Critical, CVSS 9.8)";
            case "CVE-2017-5638" -> "Apache Struts2 RCE (Critical, CVSS 10.0)";
            case "CVE-2020-1472" -> "Zerologon (Critical, CVSS 10.0)";
            case "CVE-2023-25194" -> "Kafka RCE (High, CVSS 8.8)";
            default -> "Unknown CVE or mock data available";
        };
    }

    public static String buildReportSummary(Map<Integer, String> openPorts) {
        StringBuilder sb = new StringBuilder("=== Penetration Test Report Summary ===\n");
        sb.append("Open ports found: ").append(openPorts.size()).append("\n");
        for (var entry : openPorts.entrySet()) {
            sb.append("  Port ").append(entry.getKey()).append("/tcp - ").append(entry.getValue()).append("\n");
        }
        sb.append("\nRisk Assessment:\n");
        for (var entry : openPorts.entrySet()) {
            int p = entry.getKey();
            String risk = switch (p) {
                case 21, 23, 445 -> "HIGH";
                case 22, 25, 3389 -> "MEDIUM";
                default -> "LOW";
            };
            sb.append("  Port ").append(p).append(" (").append(entry.getValue()).append("): ").append(risk).append("\n");
        }
        return sb.toString();
    }
}
