# Mini Project: Reactive File Copy Utility

## Objective
Build a utility that copies a massive file from one location to another using pure asynchronous, non-blocking I/O. You will use `AsynchronousFileChannel` and chain `CompletionHandler` callbacks to read chunks of data and write them without ever blocking a thread.

## Prerequisites
*   Java 17+

## Step 1: Configuration and Setup
Define the source and destination paths, and create a custom thread pool to handle the callbacks so we don't exhaust the default system pool.

```java
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncFileCopy {
    private static final int BUFFER_SIZE = 1024 * 1024; // 1MB chunks
    
    // Custom thread pool for I/O callbacks
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void copyFile(Path source, Path dest) throws IOException, InterruptedException {
        // Create destination file if it doesn't exist
        if (!Files.exists(dest)) {
            Files.createFile(dest);
        }

        // Open channels using our custom executor
        AsynchronousFileChannel readChannel = AsynchronousFileChannel.open(
            source, java.util.Set.of(StandardOpenOption.READ), executor);
            
        AsynchronousFileChannel writeChannel = AsynchronousFileChannel.open(
            dest, java.util.Set.of(StandardOpenOption.WRITE), executor);

        // Latch to keep the main thread alive until the async copy finishes
        CountDownLatch latch = new CountDownLatch(1);
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        System.out.println("Starting asynchronous copy...");
        
        // Kick off the first read operation (position 0)
        readChunk(readChannel, writeChannel, buffer, 0, latch);
        
        System.out.println("Main thread is free to do other work while copying happens in background!");
        
        // Wait for completion
        latch.await();
        
        readChannel.close();
        writeChannel.close();
        executor.shutdown();
        System.out.println("Copy operation completed entirely asynchronously.");
    }
```

## Step 2: The Read Callback
When a chunk is read from the source file, this callback is triggered. It flips the buffer and initiates the write operation.

```java
    private void readChunk(AsynchronousFileChannel readChannel, 
                           AsynchronousFileChannel writeChannel, 
                           ByteBuffer buffer, 
                           long position, 
                           CountDownLatch latch) {
        
        readChannel.read(buffer, position, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesRead, Void attachment) {
                if (bytesRead == -1) {
                    // End of file reached
                    latch.countDown();
                    return;
                }

                // Prepare buffer for writing
                buffer.flip();

                // Initiate asynchronous write
                writeChunk(readChannel, writeChannel, buffer, position, bytesRead, latch);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.err.println("Read failed: " + exc.getMessage());
                latch.countDown();
            }
        });
    }
```

## Step 3: The Write Callback
When a chunk is written to the destination file, this callback is triggered. It clears the buffer and initiates the *next* read operation, creating an asynchronous loop.

```java
    private void writeChunk(AsynchronousFileChannel readChannel, 
                            AsynchronousFileChannel writeChannel, 
                            ByteBuffer buffer, 
                            long position, 
                            int bytesRead, 
                            CountDownLatch latch) {

        writeChannel.write(buffer, position, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesWritten, Void attachment) {
                // Prepare buffer for the next read
                buffer.clear();
                
                // Calculate the new position and initiate the next read
                long nextPosition = position + bytesRead;
                readChunk(readChannel, writeChannel, buffer, nextPosition, latch);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.err.println("Write failed: " + exc.getMessage());
                latch.countDown();
            }
        });
    }
}
```

## Step 4: Execute
```java
public class Main {
    public static void main(String[] args) {
        try {
            // Setup a dummy file to copy
            Path source = Paths.get("source.txt");
            Path dest = Paths.get("destination.txt");
            
            // Create a 10MB file for testing
            byte[] data = new byte[10 * 1024 * 1024]; 
            Files.write(source, data);

            AsyncFileCopy copier = new AsyncFileCopy();
            copier.copyFile(source, dest);

            // Cleanup
            Files.deleteIfExists(source);
            Files.deleteIfExists(dest);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Expected Output
Notice how the main thread prints its message immediately after kicking off the copy process, proving that the I/O is truly non-blocking.
```text
Starting asynchronous copy...
Main thread is free to do other work while copying happens in background!
Copy operation completed entirely asynchronously.
```