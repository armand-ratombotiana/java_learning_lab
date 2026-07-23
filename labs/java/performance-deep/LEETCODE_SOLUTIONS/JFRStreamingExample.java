package performance;

import java.io.*;
import java.nio.file.*;

/**
 * JFR (JDK Flight Recorder) Streaming API example.
 * 
 * JFR is Oracle's low-overhead profiling framework built into the JVM.
 * It records events (GC, allocations, locks, method profiling) with minimal overhead.
 * 
 * Recording methods:
 * 1. Command-line: -XX:StartFlightRecording=duration=60s,filename=rec.jfr
 * 2. Programmatic: Recording::start (jdk.jfr)
 * 3. jcmd: jcmd <pid> JFR.start
 * 
 * This demo shows the programmatic API and how to read recordings.
 * 
 * Requires: jdk.jfr module (included in JDK)
 */
public class JFRStreamingExample {

    public static void main(String[] args) throws Exception {
        // Check if JFR is available
        try {
            Class.forName("jdk.jfr.Recording");
            System.out.println("JFR API available");
        } catch (ClassNotFoundException e) {
            System.out.println("JFR not available in this JDK distribution.");
            System.out.println("Use Oracle JDK or OpenJDK with -XX:+UnlockCommercialFeatures");
            return;
        }

        // JFR programmatic recording
        // In a real application:
        /*
        var recording = new Recording();
        recording.setName("Demo Recording");
        recording.setDumpOnExit(true);
        recording.enable("jdk.GCHeapSummary").withPeriod(Duration.ofSeconds(1));
        recording.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(1));
        recording.start();

        // ... application code ...

        recording.stop();
        try (var fos = new FileOutputStream("demo.jfr")) {
            fos.write(recording.getStream(null, null).readAllBytes());
        }
        */

        // Read existing JFR file if available
        Path jfrFile = Paths.get("demo.jfr");
        if (Files.exists(jfrFile)) {
            System.out.println("Reading JFR file: " + jfrFile.toAbsolutePath());
            // In real code: use jdk.jfr.consumer.RecordingFile
            // RecordingFile.readAllEvents(path).forEach(event -> { ... });
            System.out.println("Use 'jfr print demo.jfr' or JDK Mission Control to view.");
        } else {
            System.out.println("No JFR file found. Run with:");
            System.out.println("  -XX:StartFlightRecording=duration=10s,filename=demo.jfr");
        }

        System.out.println("JFRStreamingExample done.");
    }
}