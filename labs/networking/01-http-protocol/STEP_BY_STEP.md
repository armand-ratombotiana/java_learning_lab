# HTTP Protocol - Step by Step

## Complete Client-Server Interaction Flow

### Step 1: DNS Resolution
```java
InetAddress address = InetAddress.getByName("example.com");
System.out.println("Resolved: " + address.getHostAddress());
```

### Step 2: TCP Connection
```java
Socket socket = new Socket("example.com", 443);
SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory)
    SSLSocketFactory.getDefault()).createSocket(socket, "example.com", 443, true);
sslSocket.startHandshake();
```

### Step 3: Send HTTP Request
```java
OutputStream out = sslSocket.getOutputStream();
String request = "GET /api/data HTTP/1.1\r\n" +
    "Host: example.com\r\n" +
    "Accept: application/json\r\n" +
    "Connection: keep-alive\r\n\r\n";
out.write(request.getBytes());
out.flush();
```

### Step 4: Receive Response
```java
BufferedReader reader = new BufferedReader(
    new InputStreamReader(sslSocket.getInputStream()));
String statusLine = reader.readLine();
int contentLength = 0;
String line;
while (!(line = reader.readLine()).isEmpty()) {
    if (line.toLowerCase().startsWith("content-length:"))
        contentLength = Integer.parseInt(line.split(":")[1].trim());
}
char[] body = new char[contentLength];
reader.read(body, 0, contentLength);
System.out.println("Body: " + new String(body));
```

### Step 5: Complete with java.net.http
```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .GET().build();
HttpResponse<String> response = client.send(request,
    HttpResponse.BodyHandlers.ofString());
System.out.println("Status: " + response.statusCode());
System.out.println("Body: " + response.body());
```
