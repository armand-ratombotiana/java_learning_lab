package com.learning.webassembly;

import java.util.*;
import java.util.function.*;

public class WebAssemblySolution {

    public static class WasmModule {
        private String name;
        private byte[] bytecode;
        private Map<String, WasmFunction> functions;

        public WasmModule(String name, byte[] bytecode) {
            this.name = name;
            this.bytecode = bytecode;
            this.functions = new HashMap<>();
        }

        public void addFunction(String name, WasmFunction func) {
            functions.put(name, func);
        }

        public WasmFunction getFunction(String name) {
            return functions.get(name);
        }

        public byte[] getBytecode() { return bytecode; }
        public String getName() { return name; }
    }

    public static class WasmFunction {
        private String name;
        private String returnType;
        private List<String> paramTypes;
        private Function<List<Object>, Object> implementation;

        public WasmFunction(String name, String returnType, List<String> paramTypes) {
            this.name = name;
            this.returnType = returnType;
            this.paramTypes = paramTypes;
        }

        public void setImplementation(Function<List<Object>, Object> impl) {
            this.implementation = impl;
        }

        public Object invoke(List<Object> args) {
            if (implementation != null) {
                return implementation.apply(args);
            }
            return null;
        }

        public String getName() { return name; }
        public String getReturnType() { return returnType; }
        public List<String> getParamTypes() { return paramTypes; }
    }

    public WasmModule createModule(String name, byte[] bytecode) {
        return new WasmModule(name, bytecode);
    }

    public WasmFunction createFunction(String name, String returnType, String... paramTypes) {
        return new WasmFunction(name, returnType, Arrays.asList(paramTypes));
    }

    public Object executeFunction(WasmModule module, String functionName, List<Object> args) {
        WasmFunction func = module.getFunction(functionName);
        if (func != null) {
            return func.invoke(args);
        }
        return null;
    }

    public static class GraalVMNative {
        private String imageName;
        private String entryPoint;
        private Map<String, String> buildConfig;

        public GraalVMNative(String imageName, String entryPoint) {
            this.imageName = imageName;
            this.entryPoint = entryPoint;
            this.buildConfig = new HashMap<>();
        }

        public void setBuildOption(String key, String value) {
            buildConfig.put(key, value);
        }

        public String getImageName() { return imageName; }
        public String getEntryPoint() { return entryPoint; }
        public Map<String, String> getBuildConfig() { return buildConfig; }
    }

    public GraalVMNative createNativeImage(String name, String entryPoint) {
        return new GraalVMNative(name, entryPoint);
    }

    public GraalVMNative withReflection(GraalVMNative nativeImg, String... classes) {
        for (String cls : classes) {
            nativeImg.setBuildOption("reflect." + cls, "true");
        }
        return nativeImg;
    }

    public GraalVMNative withInitClass(GraalVMNative nativeImg, String className) {
        nativeImg.setBuildOption("init", className);
        return nativeImg;
    }

    public static class WasmMemory {
        private long minPages;
        private long maxPages;
        private byte[] data;

        public WasmMemory(long minPages, long maxPages) {
            this.minPages = minPages;
            this.maxPages = maxPages;
            this.data = new byte[(int)(minPages * 65536)];
        }

        public byte[] read(long offset, long length) {
            if (offset + length > data.length) {
                return new byte[0];
            }
            return Arrays.copyOfRange(data, (int)offset, (int)(offset + length));
        }

        public void write(long offset, byte[] bytes) {
            if (offset + bytes.length <= data.length) {
                System.arraycopy(bytes, 0, data, (int)offset, bytes.length);
            }
        }

        public long getSize() { return data.length; }
    }

    public WasmMemory createMemory(long minPages, long maxPages) {
        return new WasmMemory(minPages, maxPages);
    }
}