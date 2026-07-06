package com.javaacademy.lab49.offheap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OffHeapMemoryTest {

    @Test
    void testDirectBuffer() {
        DirectBufferExample.main(new String[]{});
    }

    @Test
    void testMappedFile() throws Exception {
        MappedFileExample.main(new String[]{});
    }

    @Test
    void testVarHandle() throws Exception {
        VarHandleExample.main(new String[]{});
    }

    @Test
    void testForeignMemory() {
        ForeignMemoryExample.main(new String[]{});
    }
}
