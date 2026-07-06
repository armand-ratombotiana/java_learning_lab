package com.javaacademy.lab43.classloading;

import org.junit.jupiter.api.*;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ClassLoadingBytecodeTest {

    @Test
    void testClassLoaderHierarchy() {
        ClassLoader cl = this.getClass().getClassLoader();
        assertNotNull(cl);
        assertNotNull(cl.getParent());
    }

    @Test
    void testCustomClassLoaderDelegation() throws Exception {
        var tmp = Files.createTempDirectory("test-ccl");
        var loader = new CustomClassLoader(tmp, ClassLoader.getPlatformClassLoader());
        assertNotNull(loader.getParent());
    }

    @Test
    void testBytecodeAnalyzer() throws Exception {
        try (var in = BytecodeAnalyzer.class.getResourceAsStream("BytecodeAnalyzer.class")) {
            assertDoesNotThrow(() -> BytecodeAnalyzer.analyze(in));
        }
    }

    @Test
    void testAsmTransformation() throws Exception {
        String cls = "AsmTransformer.class";
        byte[] bytes = AsmTransformer.class.getResourceAsStream(cls).readAllBytes();
        byte[] transformed = AsmTransformer.addAnnotation(bytes);
        assertNotNull(transformed);
        assertTrue(transformed.length >= bytes.length);
    }

    @Test
    void testInvokeDynamic() throws Exception {
        InvokeDynamicExample.main(new String[]{});
    }
}
