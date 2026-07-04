# HTTP Protocol - Debugging

## Java HTTP Client Logging
```java
System.setProperty("jdk.httpclient.HttpClient.log", "all");
System.setProperty("jdk.httpclient.HttpClient.log", "headers");
System.setProperty("jdk.httpclient.HttpClient.log", "requests,trace");
```

## Debugging Proxy Setup
```java
System.setProperty("http.proxyHost", "localhost");
System.setProperty("http.proxyPort", "8888");
System.setProperty("https.proxyHost", "localhost");
System.setProperty("https.proxyPort", "8888");
```

## cURL Commands
```bash
curl -v https://api.example.com/users           # Verbose
curl -I https://api.example.com/users            # Headers only
curl --http2 -v https://api.example.com/users    # HTTP/2
curl -w "Connect: %{time_connect}s\nTTFB: %{time_starttransfer}s\n" -o /dev/null -s https://example.com
```

## Wireshark Filters
```
http         - All HTTP traffic
http2        - HTTP/2 frames
tcp.port == 443 - HTTPS traffic
tls.handshake.type == 1 - Client Hello
```
