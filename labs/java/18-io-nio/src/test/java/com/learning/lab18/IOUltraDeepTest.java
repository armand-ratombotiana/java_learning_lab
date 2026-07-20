package com.learning.lab18;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;

class IOUltraDeepTest {

    @Test
    void tryWithResourcesClosesStream() throws IOException {
        Path f = Files.createTempFile("test", ".txt");
        f.toFile().deleteOnExit();
        try (FileWriter fw = new FileWriter(f.toFile())) {
            fw.write("data");
        }
        assertTrue(Files.exists(f));
        assertEquals("data", Files.readString(f));
    }

    @Test
    void fileInputStreamToOutputStreamCopy() throws IOException {
        Path src = Files.createTempFile("src", ".dat");
        Path dst = Files.createTempFile("dst", ".dat");
        src.toFile().deleteOnExit();
        dst.toFile().deleteOnExit();
        Files.writeString(src, "binary content");
        try (FileInputStream fis = new FileInputStream(src.toFile());
             FileOutputStream fos = new FileOutputStream(dst.toFile())) {
            fis.transferTo(fos);
        }
        assertEquals(Files.size(src), Files.size(dst));
    }

    @Test
    void createDirectoriesCreatesIntermediate() throws IOException {
        Path nested = Files.createTempDirectory("nested");
        Path deep = nested.resolve("a/b/c");
        Files.createDirectories(deep);
        assertTrue(Files.exists(deep));
    }

    @Test
    void pathOperationsNormalize() {
        Path p = Path.of("/a/b/../c/./d");
        assertEquals(Path.of("/a/c/d"), p.normalize());
    }

    @Test
    void pathResolveCombinesPaths() {
        Path base = Path.of("/base");
        assertEquals(Path.of("/base/child"), base.resolve("child"));
    }

    @Test
    void readAttributesOfFile() throws IOException {
        Path f = Files.createTempFile("attr", ".txt");
        f.toFile().deleteOnExit();
        var attrs = Files.readAttributes(f, "size,lastModifiedTime");
        assertNotNull(attrs.get("size"));
        assertNotNull(attrs.get("lastModifiedTime"));
    }
}
