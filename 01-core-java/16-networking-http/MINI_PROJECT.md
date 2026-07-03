# Module 16: Networking & HTTP - Mini Project

**Project Name**: Lightweight HTTP Server  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Understand the underlying mechanisms of HTTP communication by building a multithreaded HTTP server from scratch using plain Java Sockets, `ServerSocket`, and basic string parsing.

## 📝 Requirements

### Core Features
1. **The Server Socket**:
   - Create a `ServerSocket` listening on a specific port (e.g., 8080).
   - Use a `while(true)` loop to accept incoming `Socket` connections.

2. **Multithreading**:
   - Every time a new client connects (e.g., a browser), wrap the `Socket` in a `Runnable` or `Callable` task.
   - Submit the task to an `ExecutorService` so multiple clients can be handled concurrently without blocking the main accept loop.

3. **HTTP Parsing**:
   - Read the incoming request from the socket's `InputStream` using a `BufferedReader`.
   - Parse the first line (the Request Line) to extract the HTTP Method (GET) and the requested path (e.g., `/index.html`).

4. **HTTP Response**:
   - Based on the requested path, generate a standard HTTP response.
   - If the path is `/` or `/index.html`, return a simple HTML page: `<html><body><h1>Hello Web!</h1></body></html>`.
   - The response must include standard HTTP headers:
     - `HTTP/1.1 200 OK`
     - `Content-Type: text/html`
     - `Content-Length: <size>`
     - A blank line (`\r\n`) separating headers and body.
   - Write the response to the socket's `OutputStream`.

5. **Resource Handling**:
   - Ensure the client `Socket` and streams are closed after the response is sent.

---

## 💡 Solution Blueprint

```java
public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started on port 8080...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleClient(clientSocket));
            }
        }
    }

    private static void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
             
            String requestLine = in.readLine();
            if (requestLine == null) return;
            System.out.println("Request: " + requestLine);
            
            // Send Response
            String body = "<html><body><h1>Hello from Java Socket!</h1></body></html>";
            out.print("HTTP/1.1 200 OK\r\n");
            out.print("Content-Type: text/html\r\n");
            out.print("Content-Length: " + body.length() + "\r\n");
            out.print("\r\n"); // Blank line between headers and body
            out.print(body);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }
}
```