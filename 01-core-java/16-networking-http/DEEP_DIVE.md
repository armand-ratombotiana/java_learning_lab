# Module 16: Networking & HTTP - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-15  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Java Networking Basics](#basics)
2. [Sockets and ServerSockets](#sockets)
3. [URL and URI Classes](#url)
4. [HttpURLConnection](#httpurl)
5. [Java 11+ HttpClient](#httpclient)

---

## 1. Java Networking Basics <a name="basics"></a>
Java provides the `java.net` package for network programming, supporting both TCP (Transmission Control Protocol) and UDP (User Datagram Protocol). TCP provides reliable, ordered delivery of a stream of bytes, while UDP is a connectionless protocol.

---

## 2. Sockets and ServerSockets <a name="sockets"></a>
- **Socket**: Represents a client connection. Used to send and receive data over TCP.
- **ServerSocket**: Listens for incoming client connections.

```java
// Server
try (ServerSocket serverSocket = new ServerSocket(8080)) {
    Socket clientSocket = serverSocket.accept();
    // Handle communication
}

// Client
try (Socket socket = new Socket("localhost", 8080)) {
    // Communicate with server
}
```

---

## 3. URL and URI Classes <a name="url"></a>
- **URI (Uniform Resource Identifier)**: Identifies a resource.
- **URL (Uniform Resource Locator)**: A type of URI that provides the means to locate the resource.

```java
URL url = new URL("https://example.com/api/data");
System.out.println(url.getHost());
```

---

## 4. HttpURLConnection <a name="httpurl"></a>
The legacy way of making HTTP requests.

```java
URL url = new URL("https://api.example.com/data");
HttpURLConnection con = (HttpURLConnection) url.openConnection();
con.setRequestMethod("GET");

int status = con.getResponseCode();
```

---

## 5. Java 11+ HttpClient <a name="httpclient"></a>
Introduced in Java 11, `java.net.http.HttpClient` provides a modern, asynchronous API for HTTP requests.

```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .build();

HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
System.out.println(response.body());
```