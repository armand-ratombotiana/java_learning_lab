# HTTP Protocol - Internals

## TCP Socket Level HTTP

```java
public class RawHttpClient {
    public static String send(String host, int port, String request) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5000);
            socket.setSoTimeout(10000);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.print(request);
            out.flush();
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
                response.append(line).append("\r\n");
            return response.toString();
        }
    }
}
```

## HTTP/2 Binary Framing

9-byte frame header.

Frame types: DATA, HEADERS, PRIORITY, RST_STREAM, SETTINGS, PUSH_PROMISE, GOAWAY, PING

## HTTP/3 QUIC Stack

```
+------------------+
|    HTTP/3        |
+------------------+
|    QUIC (UDP)    |
+------------------+
|    IP            |
+------------------+
```
