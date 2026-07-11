# Kafka Code Deep Dive

This lab provides a pure Java simulation of Kafka's core storage engine: the Append-Only Log and the Zero-Copy transfer mechanism.

## 💻 Pure Java Implementation

```java file="labs/system-design/06-messaging/kafka-internals/SOLUTION/MiniKafkaLog.java"
package systemdesign.messaging.kafka;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A simulation of Kafka's Log-Structured storage and Zero-Copy network transfer.
 */
public class MiniKafkaLog {

    private final Path logFilePath;
    private final FileChannel fileChannel;
    private final AtomicLong currentOffset = new AtomicLong(0);

    public MiniKafkaLog(String topic, int partition) throws IOException {
        String fileName = String.format("%s-%d.log", topic, partition);
        this.logFilePath = Paths.get(fileName);
        
        // Open file for appending and reading
        this.fileChannel = FileChannel.open(logFilePath, 
            StandardOpenOption.CREATE, 
            StandardOpenOption.APPEND, 
            StandardOpenOption.READ);
    }

    /**
     * Appends a message to the end of the log. O(1) time complexity.
     * 
     * @param message The payload to append.
     * @return The offset assigned to this message.
     */
    public long append(byte[] message) throws IOException {
        // In a real implementation, we would add headers (timestamp, CRC, size)
        ByteBuffer buffer = ByteBuffer.allocate(4 + message.length);
        buffer.putInt(message.length); // 4-byte size header
        buffer.put(message);           // The actual payload
        buffer.flip();

        synchronized (this) {
            // Write to the end of the file
            while (buffer.hasRemaining()) {
                fileChannel.write(buffer);
            }
            // Force flush to disk (fsync) - Kafka usually relies on OS background flush
            // fileChannel.force(false); 
            
            return currentOffset.getAndIncrement();
        }
    }

    /**
     * Demonstrates ZERO-COPY transfer.
     * Reads data from the disk and sends it directly to a network socket
     * without ever copying the data into the JVM heap.
     * 
     * @param socketChannel The network connection to the consumer.
     * @param position The physical byte position in the file to start reading from.
     * @param count The number of bytes to send.
     */
    public void transferToConsumerZeroCopy(SocketChannel socketChannel, long position, long count) throws IOException {
        // This single line invokes the OS 'sendfile()' system call.
        // Data moves: Disk -> OS Page Cache -> NIC. (Bypasses JVM entirely).
        long bytesTransferred = fileChannel.transferTo(position, count, socketChannel);
        System.out.println("Zero-Copy transferred " + bytesTransferred + " bytes directly to socket.");
    }

    public void close() throws IOException {
        fileChannel.close();
    }
}
```

## 🔍 Key Takeaways
1. **The Append Operation**: Notice how simple the `append()` method is. There is no B-Tree to update, no linked list to traverse. It simply writes bytes to the end of the `FileChannel`. This is why Kafka can handle millions of writes per second on standard spinning hard drives.
2. **The Magic of `transferTo()`**: The `transferToConsumerZeroCopy` method is the secret sauce of Kafka's read performance. The `FileChannel.transferTo()` method in Java maps directly to the Linux `sendfile` system call. The JVM never touches the bytes being sent, saving massive amounts of CPU and memory bandwidth.