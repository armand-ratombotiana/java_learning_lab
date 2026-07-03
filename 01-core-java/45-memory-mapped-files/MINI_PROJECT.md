# Mini Project: High-Speed IPC (Inter-Process Communication)

## Objective
Build a system where two completely separate Java processes communicate with each other at microsecond speeds. They will do this by mapping the same physical file into their respective memories (Shared Memory). One will act as a Writer, and the other as a Reader, using `FileLock` for synchronization.

## Prerequisites
*   Java 17+
*   You will need to run two separate `main` methods simultaneously.

## Step 1: Shared Configuration
Create a config class so both processes agree on the file path and buffer size.

```java
import java.nio.file.Path;
import java.nio.file.Paths;

public class IPCConfig {
    public static final Path SHARED_FILE = Paths.get("shared_memory.bin");
    // We only need a few bytes: 1 byte for a "ready" flag, and 4 bytes for an integer payload
    public static final int BUFFER_SIZE = 5; 
}
```

## Step 2: The Writer Process
This process maps the file, writes an integer, sets a flag, and forces the write to disk.

```java
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class WriterProcess {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Writer Process...");

        try (RandomAccessFile file = new RandomAccessFile(IPCConfig.SHARED_FILE.toFile(), "rw");
             FileChannel channel = file.getChannel()) {

            // Map the file into memory
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, IPCConfig.BUFFER_SIZE);

            int counter = 0;
            while (true) {
                // 1. Acquire an exclusive lock before writing
                FileLock lock = channel.lock();
                try {
                    // 2. Write the payload (an integer) at index 1
                    buffer.putInt(1, counter);
                    
                    // 3. Set the "ready" flag at index 0 to 1 (true)
                    buffer.put(0, (byte) 1);
                    
                    System.out.println("Writer wrote: " + counter);
                    counter++;
                    
                    // 4. Force changes to be visible to other processes immediately
                    buffer.force();
                } finally {
                    lock.release(); // CRITICAL: Release the lock
                }

                // Wait 2 seconds before the next write
                Thread.sleep(2000);
            }
        }
    }
}
```

## Step 3: The Reader Process
This process maps the exact same file. It polls the "ready" flag. When it sees the flag is 1, it reads the payload and resets the flag to 0.

```java
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class ReaderProcess {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Reader Process...");

        // Ensure file exists before reading
        if (!IPCConfig.SHARED_FILE.toFile().exists()) {
            System.err.println("Please start the WriterProcess first!");
            return;
        }

        try (RandomAccessFile file = new RandomAccessFile(IPCConfig.SHARED_FILE.toFile(), "rw");
             FileChannel channel = file.getChannel()) {

            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, IPCConfig.BUFFER_SIZE);

            while (true) {
                // 1. Acquire an exclusive lock (we need to write the flag back to 0)
                FileLock lock = channel.lock();
                try {
                    // 2. Check the "ready" flag at index 0
                    byte isReady = buffer.get(0);
                    
                    if (isReady == 1) {
                        // 3. Read the payload at index 1
                        int payload = buffer.getInt(1);
                        System.out.println("Reader received: " + payload);
                        
                        // 4. Reset the flag to 0 to acknowledge receipt
                        buffer.put(0, (byte) 0);
                        buffer.force();
                    }
                } finally {
                    lock.release();
                }

                // Poll very quickly (100ms). In a real high-perf system, you might spin-wait.
                Thread.sleep(100);
            }
        }
    }
}
```

## Step 4: Execute the IPC Test
1.  Compile all three classes.
2.  Open **Terminal 1** and run `java WriterProcess`. You will see it writing numbers.
3.  Open **Terminal 2** and run `java ReaderProcess`. 
4.  You will see the Reader instantly picking up the numbers written by the Writer. They are communicating by reading and writing to the exact same physical memory addresses via the OS page cache!

## Expected Output

**(Terminal 1 - Writer)**
```text
Starting Writer Process...
Writer wrote: 0
Writer wrote: 1
Writer wrote: 2
```

**(Terminal 2 - Reader)**
```text
Starting Reader Process...
Reader received: 0
Reader received: 1
Reader received: 2
```