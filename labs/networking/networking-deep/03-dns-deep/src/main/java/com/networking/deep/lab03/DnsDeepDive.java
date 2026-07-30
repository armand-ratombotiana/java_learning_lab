package com.networking.deep.lab03;

import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class DnsDeepDive {

    public record DnsHeader(int id, boolean qr, int opcode, boolean aa, boolean tc, boolean rd, boolean ra, int rcode) {}
    public record DnsQuestion(String qname, int qtype, int qclass) {}
    public record DnsRecord(String name, int type, int qclass, long ttl, String rdata) {}
    public record DnsMessage(DnsHeader header, List<DnsQuestion> questions, List<DnsRecord> answers, List<DnsRecord> authorities) {}

    public record DnssecSignature(String name, int type, String algorithm, String signature) {
        public boolean verify(String data, String publicKey) {
            try {
                var sig = Signature.getInstance("SHA256withRSA");
                var keyFactory = KeyFactory.getInstance("RSA");
                var keyBytes = Base64.getDecoder().decode(publicKey);
                var spec = new java.security.spec.X509EncodedKeySpec(keyBytes);
                var pubKey = keyFactory.generatePublic(spec);
                sig.initVerify(pubKey);
                sig.update(data.getBytes());
                return sig.verify(Base64.getDecoder().decode(signature));
            } catch (Exception e) { return false; }
        }
    }

    public static class RecursiveResolver {
        private final Map<String, DnsRecord> cache = new ConcurrentHashMap<>();
        private final List<String> rootServers = List.of("a.root-servers.net", "b.root-servers.net");

        public DnsRecord resolve(String domain, int type) {
            var cacheKey = domain + ":" + type;
            var cached = cache.get(cacheKey);
            if (cached != null) return cached;

            System.out.println("Resolving " + domain + " (type " + type + ")");
            var record = iterativeResolve(domain, type);
            if (record != null) cache.put(cacheKey, record);
            return record;
        }

        private DnsRecord iterativeResolve(String domain, int type) {
            if ("example.com".equals(domain)) {
                return new DnsRecord(domain, type, 1, 3600, "93.184.216.34");
            }
            if ("google.com".equals(domain)) {
                return new DnsRecord(domain, type, 1, 300, "142.250.80.14");
            }
            return new DnsRecord(domain, type, 1, 3600, "127.0.0.1");
        }
    }

    public static class DohClient {
        public byte[] query(String domain, int type) {
            System.out.println("DoH query: " + domain + " via https://cloudflare-dns.com/dns-query");
            var answer = new DnsRecord(domain, type, 1, 300, "1.1.1.1");
            return answer.toString().getBytes();
        }
    }

    public static class ZoneTransfer {
        private final List<DnsRecord> records = new CopyOnWriteArrayList<>();

        public List<DnsRecord> axfr(String zone) {
            System.out.println("AXFR for zone: " + zone);
            records.add(new DnsRecord(zone, 6, 1, 3600, "ns1." + zone));
            records.add(new DnsRecord(zone, 1, 1, 3600, "192.168.1.1"));
            records.add(new DnsRecord("www." + zone, 1, 1, 300, "192.168.1.10"));
            records.add(new DnsRecord("mail." + zone, 1, 1, 300, "192.168.1.20"));
            return List.copyOf(records);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== DNS Recursive Resolution ===");
        var resolver = new RecursiveResolver();
        var rec = resolver.resolve("example.com", 1);
        System.out.println("Resolved: " + rec.name() + " -> " + rec.rdata() + " (TTL=" + rec.ttl() + ")");

        System.out.println("\n=== DNS over HTTPS ===");
        var doh = new DohClient();
        doh.query("example.com", 1);

        System.out.println("\n=== DNSSEC Validation ===");
        var dnssec = new DnssecSignature("example.com", 1, "RSA-SHA256",
            "fake-signature-for-demo");
        System.out.println("DNSSEC signature algorithm: " + dnssec.algorithm());

        System.out.println("\n=== Zone Transfer ===");
        var zone = new ZoneTransfer();
        var records = zone.axfr("example.com");
        System.out.println("Zone records (" + records.size() + "):");
        for (var r : records) {
            System.out.printf("  %-30s %-5d %s%n", r.name(), r.ttl(), r.rdata());
        }
    }
}
