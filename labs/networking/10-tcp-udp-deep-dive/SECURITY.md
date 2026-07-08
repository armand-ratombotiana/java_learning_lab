# TCP/UDP Deep Dive — Security

## 1. TCP Security Concerns

### 1.1 SYN Flood Attack
An attacker sends a flood of SYN packets without completing the handshake, exhausting the server's connection queue.

**Mitigation:**
- SYN cookies (enabled by default in Linux)
- Increase tcp_max_syn_backlog
- Reduce SYN_RCVD timeout (tcp_synack_retries)
- Rate-limit incoming SYNs

```java
// SYN cookie validation (simplified)
public class SynCookie {
    public static int generateCookie(SocketAddress addr, int seq) {
        int hash = addr.hashCode();
        int crypto = (int) (System.currentTimeMillis() / 60000); // Change every minute
        return hash ^ crypto ^ seq;
    }

    public static boolean validateCookie(SocketAddress addr, int cookie, int seq) {
        int expected = generateCookie(addr, seq);
        return cookie == expected;
    }
}
```

### 1.2 TCP Sequence Number Attacks
If an attacker can predict sequence numbers, they can inject spoofed packets into an established connection.

**Protection:**
- Random ISN (RFC 6528)
- TCP timestamps for PAWS (Protection Against Wrapped Sequences)
- IPsec or TLS for cryptographically protected connections

### 1.3 TCP RST Injection
An attacker sends a spoofed RST packet to tear down a connection.

**Conditions for success:**
- Sequence number must match exactly (in window)
- 4-tuple (src IP, src port, dst IP, dst port) must match

**Mitigation:**
- Challenge ACK validation (RFC 5961)
- TCP MD5 option (RFC 2385, used in BGP)
- TCP-AO (RFC 5925)

### 1.4 TCP Session Hijacking
An attacker takes over an existing TCP session by spoofing packets with guessed sequence numbers.

## 2. UDP Security Concerns

### 2.1 UDP Amplification Attack
A reflection attack where the attacker sends small queries with a spoofed source IP, and the server sends large responses to the victim.

**Common protocols abused:**
- DNS (28x amplification)
- NTP (556x amplification, monlist)
- Memcached (51,000x amplification)
- SSDP (30x amplification)

```java
public class AmplificationDetector {
    private static final double MAX_AMPLIFICATION = 20.0;

    public static boolean isAmplificationAttack(int requestSize, int responseSize) {
        double ratio = (double) responseSize / requestSize;
        return ratio > MAX_AMPLIFICATION;
    }
}
```

### 2.2 UDP Flood
A volumetric attack that saturates the target's bandwidth with UDP packets.

**Mitigation:**
- Rate limiting
- UDP packet validation
- Disable unnecessary UDP services
- Use Anycast to distribute load

### 2.3 UDP Spoofing
Without handshakes, source IP addresses are trivially spoofed.

**Mitigation:**
- Ingress filtering (BCP 38)
- uRPF (unicast Reverse Path Forwarding)
- Application-level authentication

## 3. Secure Socket Configuration

```java
public class SecureSocketConfig {
    public static void hardenSocket(Socket socket) throws SocketException {
        // Enable keepalive to detect half-open connections
        socket.setKeepAlive(true);

        // Set reasonable timeouts
        socket.setSoTimeout(30000);

        // Enable SO_REUSEADDR for fast socket reuse
        socket.setReuseAddress(true);

        // Limit linger to prevent data leakage
        socket.setSoLinger(true, 10);
    }

    public static void hardenServerSocket(ServerSocket serverSocket) throws IOException {
        // Backlog limit to prevent SYN flood impact
        serverSocket.setPerformancePreferences(0, 1, 2);

        // Reuse address for fast restart
        serverSocket.setReuseAddress(true);

        // Set receive buffer size
        serverSocket.setReceiveBufferSize(65536);
    }
}
```

## 4. TLS over TCP

For production systems, use TLS to encrypt TCP streams:

```java
public class TcpTlsExample {
    public static void main(String[] args) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
        sslContext.init(null, null, new SecureRandom());

        SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
        try (SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(8443)) {
            // Enable only TLS 1.3
            serverSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
            // Require client certificate
            serverSocket.setNeedClientAuth(true);
        }
    }
}
```

## 5. DTLS over UDP

For encrypted UDP communication, use DTLS (Datagram TLS):

```java
public class DtlsExample {
    public static void initDtls() throws Exception {
        // DTLS is available in Java through Bouncy Castle
        // or native JDK SSLEngine with DTLS (Java 9+)
        SSLContext dtlsContext = SSLContext.getInstance("DTLSv1.2");
        dtlsContext.init(null, null, new SecureRandom());

        SSLEngine engine = dtlsContext.createSSLEngine();
        engine.setUseClientMode(false);
        engine.setEnabledProtocols(new String[]{"DTLSv1.2"});
    }
}
```

## 6. Defense in Depth for Transport Layer

| Threat | TCP Protection | UDP Protection | Application Layer |
|--------|---------------|----------------|-------------------|
| Spoofing | SEQ checking, ISN random | Ingress filtering | Auth tokens |
| Eavesdropping | TLS/SSL | DTLS | Encryption |
| Tampering | TLS HMAC | DTLS HMAC | Signatures |
| Flooding | SYN cookies, backlog | Rate limiting, Anycast | CAPTCHA |
| Amplification | N/A | Response size limits | Request auth |
| Hijacking | Random ISN, SEQ+ACK | Per-packet auth | Session tokens |
