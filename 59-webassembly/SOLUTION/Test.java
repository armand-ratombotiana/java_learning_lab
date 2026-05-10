package com.learning.webassembly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebAssemblySolutionTest {

    private WebAssemblySolution solution;

    @BeforeEach
    void setUp() {
        solution = new WebAssemblySolution();
    }

    @Test
    void testCreateModule() {
        byte[] bytecode = new byte[]{0x00, 0x61, 0x73, 0x6d};
        WebAssemblySolution.WasmModule module = solution.createModule("test-module", bytecode);
        assertEquals("test-module", module.getName());
        assertEquals(bytecode, module.getBytecode());
    }

    @Test
    void testCreateFunction() {
        WebAssemblySolution.WasmFunction func = solution.createFunction("add", "i32", "i32", "i32");
        assertEquals("add", func.getName());
        assertEquals("i32", func.getReturnType());
        assertEquals(2, func.getParamTypes().size());
    }

    @Test
    void testExecuteFunction() {
        byte[] bytecode = new byte[]{0x00, 0x61, 0x73, 0x6d};
        WebAssemblySolution.WasmModule module = solution.createModule("test", bytecode);
        
        WebAssemblySolution.WasmFunction func = solution.createFunction("add", "i32", "i32", "i32");
        func.setImplementation(args -> (Integer)args.get(0) + (Integer)args.get(1));
        module.addFunction("add", func);
        
        Object result = solution.executeFunction(module, "add", Arrays.asList(2, 3));
        assertEquals(5, result);
    }

    @Test
    void testCreateNativeImage() {
        WebAssemblySolution.GraalVMNative nativeImg = solution.createNativeImage("my-app", "com.example.Main");
        assertEquals("my-app", nativeImg.getImageName());
        assertEquals("com.example.Main", nativeImg.getEntryPoint());
    }

    @Test
    void testWithReflection() {
        WebAssemblySolution.GraalVMNative nativeImg = solution.createNativeImage("my-app", "Main");
        nativeImg = solution.withReflection(nativeImg, "com.example.Class");
        assertEquals("true", nativeImg.getBuildConfig().get("reflect.com.example.Class"));
    }

    @Test
    void testCreateMemory() {
        WebAssemblySolution.WasmMemory memory = solution.createMemory(1, 2);
        assertEquals(65536, memory.getSize());
    }

    @Test
    void testMemoryReadWrite() {
        WebAssemblySolution.WasmMemory memory = solution.createMemory(1, 1);
        memory.write(0, new byte[]{1, 2, 3, 4});
        byte[] data = memory.read(0, 4);
        assertEquals(4, data.length);
        assertEquals(1, data[0]);
    }
}