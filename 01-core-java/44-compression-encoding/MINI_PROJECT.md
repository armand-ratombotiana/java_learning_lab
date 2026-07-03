# Mini Project: Secure Payload Packager

## Objective
Build a utility that takes a String payload, compresses it using GZIP, encodes the compressed binary data into a URL-safe Base64 string, and then reverses the process. This simulates creating a token (like a JWT) that contains a large amount of compressed data.

## Prerequisites
*   Java 11+

## Step 1: The Packager (Compress & Encode)
We will use `GZIPOutputStream` and `Base64.getUrlEncoder()`.

```java
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

public class PayloadPackager {

    public static String pack(String payload) throws IOException {
        // 1. Convert String to bytes using a strict Charset
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

        // 2. Compress the bytes using GZIP
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // CRITICAL: try-with-resources ensures gos.close() is called, writing the GZIP trailer!
        try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
            gos.write(payloadBytes);
        } 
        
        byte[] compressedBytes = baos.toByteArray();

        // 3. Encode the compressed binary data to a URL-Safe Base64 String (no padding)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(compressedBytes);
    }
```

## Step 2: The Unpackager (Decode & Decompress)
We reverse the process, adding a safeguard against Zip Bombs.

```java
    import java.io.ByteArrayInputStream;
    import java.util.zip.GZIPInputStream;

    // ... inside PayloadPackager class ...

    public static String unpack(String token) throws IOException {
        // 1. Decode the Base64 string back to compressed binary data
        byte[] compressedBytes = Base64.getUrlDecoder().decode(token);

        // 2. Decompress the data
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedBytes);
        
        // We use a custom buffer to prevent Zip Bomb OutOfMemoryErrors
        ByteArrayOutputStream uncompressedBaos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        int totalBytesRead = 0;
        final int MAX_SIZE = 1024 * 1024; // 1 MB limit

        try (GZIPInputStream gis = new GZIPInputStream(bais)) {
            while ((bytesRead = gis.read(buffer)) != -1) {
                totalBytesRead += bytesRead;
                // SECURITY: Zip Bomb Protection
                if (totalBytesRead > MAX_SIZE) {
                    throw new SecurityException("Decompressed payload exceeds maximum allowed size (Zip Bomb protection).");
                }
                uncompressedBaos.write(buffer, 0, bytesRead);
            }
        }

        // 3. Convert the uncompressed bytes back to a String using the exact same Charset
        return uncompressedBaos.toString(StandardCharsets.UTF_8);
    }
}
```

## Step 3: Test the Pipeline
Create a `Main` class to test the packaging, unpackaging, and the size reduction.

```java
public class Main {
    public static void main(String[] args) {
        try {
            // Create a repetitive payload that will compress very well
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                sb.append("This is a highly repetitive string meant to test GZIP compression. ");
            }
            String originalPayload = sb.toString();

            System.out.println("--- 1. Original Data ---");
            System.out.println("Original Size : " + originalPayload.getBytes().length + " bytes");

            System.out.println("\n--- 2. Packaging ---");
            String token = PayloadPackager.pack(originalPayload);
            System.out.println("Generated Token: " + token);
            System.out.println("Token Size     : " + token.getBytes().length + " bytes");
            
            // Calculate compression ratio
            double ratio = (1.0 - ((double) token.getBytes().length / originalPayload.getBytes().length)) * 100;
            System.out.printf("Size Reduction : %.2f%%\n", ratio);

            System.out.println("\n--- 3. Unpackaging ---");
            String restoredPayload = PayloadPackager.unpack(token);
            
            // Verify
            if (originalPayload.equals(restoredPayload)) {
                System.out.println("SUCCESS: Restored payload matches original perfectly.");
            } else {
                System.err.println("FAILURE: Payload was corrupted.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Expected Output
Notice how the final Base64 token is significantly smaller than the original text, thanks to GZIP. Also notice the URL-safe characters (`-` and `_`) instead of `+` and `/`.
```text
--- 1. Original Data ---
Original Size : 6700 bytes

--- 2. Packaging ---
Generated Token: H4sIAAAAAAAA_w3DQQ0AAAwDoBv0r_m4wU0iI_jXh... (truncated for brevity)
Token Size     : 147 bytes
Size Reduction : 97.81%

--- 3. Unpackaging ---
SUCCESS: Restored payload matches original perfectly.
```