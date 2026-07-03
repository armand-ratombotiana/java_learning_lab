# Mini Project: Real-Time Directory Sync & Audit

## Objective
Build a background service that uses NIO.2 to monitor a directory for new files. When a file is added, the service will asynchronously read its attributes, extract the first line of text, and write an audit log to a separate file, demonstrating `WatchService`, `Path` manipulation, and `Files` utilities.

## Prerequisites
*   Java 17+

## Step 1: Setup and Directory Initialization
Create the paths and ensure the directories exist.

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SyncConfig {
    public static final Path WATCH_DIR = Paths.get("data", "inbox");
    public static final Path AUDIT_LOG = Paths.get("data", "audit.log");

    public static void init() throws IOException {
        // Create directories if they don't exist
        Files.createDirectories(WATCH_DIR);
        
        // Ensure audit log exists
        if (!Files.exists(AUDIT_LOG)) {
            Files.createFile(AUDIT_LOG);
        }
        System.out.println("Watching directory: " + WATCH_DIR.toAbsolutePath());
    }
}
```

## Step 2: The WatchService Loop
Implement the background thread that listens for OS file system events.

```java
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class DirectoryWatcher implements Runnable {

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            
            // Register the directory for CREATE events
            SyncConfig.WATCH_DIR.register(watchService, ENTRY_CREATE);

            while (true) {
                WatchKey key;
                try {
                    // Blocks until an event occurs
                    key = watchService.take();
                } catch (InterruptedException x) {
                    System.out.println("Watcher interrupted. Exiting.");
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // The context is the relative path of the file that triggered the event
                    Path filename = (Path) event.context();
                    
                    // Resolve it against the watched directory to get the full path
                    Path fullPath = SyncConfig.WATCH_DIR.resolve(filename);

                    if (kind == ENTRY_CREATE) {
                        System.out.println("New file detected: " + filename);
                        processNewFile(fullPath);
                    }
                }

                // CRITICAL: Reset the key. If this returns false, the directory is inaccessible.
                boolean valid = key.reset();
                if (!valid) {
                    System.err.println("Directory inaccessible. Stopping watcher.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

## Step 3: Processing and Auditing
When a file is detected, read its attributes and first line, then append to the audit log.

```java
    private void processNewFile(Path file) {
        try {
            // Wait briefly to ensure the OS has finished writing the file before we read it
            Thread.sleep(100); 

            // 1. Read Metadata
            long size = Files.size(file);
            
            // 2. Read first line safely (using try-with-resources to prevent handle leaks)
            String firstLine = "EMPTY";
            try (Stream<String> lines = Files.lines(file)) {
                firstLine = lines.findFirst().orElse("EMPTY");
            }

            // 3. Construct audit entry
            String logEntry = String.format("[%s] Size: %d bytes | Preview: %s%n", 
                                            file.getFileName(), size, firstLine);

            // 4. Append to audit log
            Files.writeString(SyncConfig.AUDIT_LOG, logEntry, StandardOpenOption.APPEND);
            System.out.println("Audit log updated.");

        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to process file: " + file + " - " + e.getMessage());
        }
    }
}
```

## Step 4: Execute and Test
Create the main class to run the watcher, and simulate a user dropping a file into the directory.

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        SyncConfig.init();

        // Start the watcher in a background thread
        Thread watcherThread = new Thread(new DirectoryWatcher());
        watcherThread.start();

        // Give the watcher a moment to start
        Thread.sleep(500);

        // Simulate a user creating a file in the watched directory
        Path newFile = SyncConfig.WATCH_DIR.resolve("report.txt");
        System.out.println("Simulating file creation: " + newFile);
        Files.writeString(newFile, "CONFIDENTIAL REPORT\nLine 2\nLine 3");

        // Wait for the watcher to process it
        Thread.sleep(1000);

        // Print the audit log to verify
        System.out.println("\n--- Audit Log Contents ---");
        System.out.println(Files.readString(SyncConfig.AUDIT_LOG));

        // Clean up
        Files.deleteIfExists(newFile);
        watcherThread.interrupt();
    }
}
```

## Expected Output
```text
Watching directory: /path/to/project/data/inbox
Simulating file creation: data/inbox/report.txt
New file detected: report.txt
Audit log updated.

--- Audit Log Contents ---
[report.txt] Size: 33 bytes | Preview: CONFIDENTIAL REPORT
```