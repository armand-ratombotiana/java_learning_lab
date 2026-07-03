# Mini Project: Non-Blocking Chat Server

## Objective
Build a single-threaded, non-blocking TCP Chat Server using Java NIO. This server will handle multiple clients simultaneously without spawning a new thread for each connection, demonstrating the power of the `Selector` and `ByteBuffer`.

## Prerequisites
*   Java 17+
*   A terminal to run `telnet` or `nc` (netcat) as clients.

## Step 1: The Server Setup
Create the `ServerSocketChannel` and register it with the `Selector`.

```java
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NioChatServer {
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private final ByteBuffer buffer = ByteBuffer.allocate(256);

    public void start(int port) throws IOException {
        // 1. Open Selector and ServerSocketChannel
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        
        // 2. Bind to port and configure as NON-BLOCKING
        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        
        // 3. Register the ServerSocket with the Selector, listening for ACCEPT events
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        
        System.out.println("Chat Server started on port " + port);
        
        // Start the multiplexing loop
        runLoop();
    }
```

## Step 2: The Multiplexing Loop
This is the heart of the Reactor pattern. A single thread loops infinitely, blocking only on `selector.select()`.

```java
    private void runLoop() throws IOException {
        while (true) {
            // Blocks until at least one registered channel has an event
            selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                
                // CRITICAL: Remove the key so we don't process it again next loop
                iter.remove();

                if (!key.isValid()) {
                    continue;
                }

                // Handle the event based on its type
                if (key.isAcceptable()) {
                    acceptClient(key);
                } else if (key.isReadable()) {
                    readClientMessage(key);
                }
            }
        }
    }
```

## Step 3: Handling Events (Accept and Read)
Implement the logic to accept new connections and read data into the `ByteBuffer`.

```java
    private void acceptClient(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        
        // Accept the connection
        SocketChannel client = server.accept();
        client.configureBlocking(false); // Make the client non-blocking too!
        
        // Register the new client with the selector, listening for READ events
        client.register(selector, SelectionKey.OP_READ);
        
        System.out.println("Accepted new connection from: " + client.getRemoteAddress());
    }

    private void readClientMessage(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        buffer.clear(); // Prepare buffer for writing

        int bytesRead;
        try {
            bytesRead = client.read(buffer);
        } catch (IOException e) {
            // Client forcibly closed connection
            System.out.println("Client disconnected unexpectedly.");
            key.cancel();
            client.close();
            return;
        }

        if (bytesRead == -1) {
            // Client gracefully closed connection
            System.out.println("Client disconnected: " + client.getRemoteAddress());
            key.cancel();
            client.close();
            return;
        }

        // CRITICAL: Flip the buffer before reading from it
        buffer.flip();
        
        // Convert bytes to String
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        String message = new String(data).trim();
        
        System.out.println("Received: [" + message + "] from " + client.getRemoteAddress());
        
        // Broadcast the message to all other connected clients
        broadcast(message, client);
    }
```

## Step 4: Broadcasting (Writing to Channels)
Iterate over all registered keys and write the message to everyone except the sender.

```java
    private void broadcast(String message, SocketChannel sender) throws IOException {
        String formattedMessage = "User says: " + message + "\n";
        ByteBuffer writeBuffer = ByteBuffer.wrap(formattedMessage.getBytes());

        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel targetClient = (SocketChannel) key.channel();
                
                if (targetClient != sender) {
                    writeBuffer.rewind(); // Reset position to 0 for each client
                    
                    // Note: In a production server, write() might not write all bytes.
                    // You would need to handle partial writes here.
                    targetClient.write(writeBuffer);
                }
            }
        }
    }
}
```

## Step 5: Test the Server
```java
public class Main {
    public static void main(String[] args) {
        try {
            new NioChatServer().start(8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### How to test:
1.  Run the `Main` class.
2.  Open a terminal and type: `telnet localhost 8080` (or `nc localhost 8080`).
3.  Open a second terminal and do the same.
4.  Type a message in terminal 1 and press Enter. It will appear in terminal 2!
5.  Notice that the Java application is only using ONE thread to handle everything.