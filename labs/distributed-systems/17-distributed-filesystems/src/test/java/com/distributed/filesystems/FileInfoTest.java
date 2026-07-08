package com.distributed.filesystems;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class FileInfoTest {

    @Test
    void testFileRecord() {
        Instant now = Instant.now();
        FileInfo info = new FileInfo("/data/file.txt", 1024, now, false);
        assertEquals("/data/file.txt", info.path());
        assertEquals(1024, info.size());
        assertFalse(info.isDirectory());
    }

    @Test
    void testDirectoryRecord() {
        FileInfo info = new FileInfo("/data", 0, Instant.now(), true);
        assertTrue(info.isDirectory());
        assertTrue(info.toString().startsWith("D "));
    }
}
