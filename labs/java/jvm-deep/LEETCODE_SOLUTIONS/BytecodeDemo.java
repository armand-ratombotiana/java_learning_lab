package jvm.deep;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Bytecode-level demonstration using java.lang.invoke.
 * 
 * Shows: invokedynamic, MethodHandle, MethodHandles.Lookup,
 *        LambdaMetafactory, and reflective equivalents.
 * 
 * This demonstrates what javac generates under the hood for lambdas.
 * 
 * Time: O(1) per handle binding
 * Space: O(1)
 */
public class BytecodeDemo {

    @FunctionalInterface
    interface StringTransformer {
        String transform(String input);
    }

    public static void main(String[] args) throws Throwable {
        // 1. MethodHandle — direct bytecode-level invocation
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        MethodHandle toUpperCase = lookup.findVirtual(String.class, "toUpperCase",
            MethodType.methodType(String.class));
        String result = (String) toUpperCase.invoke("hello");
        assert result.equals("HELLO");
        System.out.println("MethodHandle: " + result);

        // 2. MethodHandle with bound receiver
        MethodHandle boundUpper = toUpperCase.bindTo("world");
        assert boundUpper.invoke().equals("WORLD");

        // 3. invokedynamic via LambdaMetafactory (what lambdas compile to)
        var factory = java.lang.invoke.LambdaMetafactory.metafactory(
            lookup,
            "transform",
            MethodType.methodType(StringTransformer.class),
            MethodType.methodType(String.class, String.class),
            lookup.findStatic(BytecodeDemo.class, "exclaim",
                MethodType.methodType(String.class, String.class)),
            MethodType.methodType(String.class, String.class)
        );
        StringTransformer transform = (StringTransformer) factory.getTarget().invokeExact();
        assert transform.transform("hi").equals("hi!");

        // 4. Lambda equivalent (what javac generates)
        StringTransformer lambda = s -> s + "?";
        StringTransformer methodRef = BytecodeDemo::exclaim;

        assert lambda.transform("ok").equals("ok?");
        assert methodRef.transform("ok").equals("ok!");

        // 5. Reflection — same operation, much slower
        Method reflectMethod = String.class.getMethod("toUpperCase");
        assert reflectMethod.invoke("java").equals("JAVA");

        System.out.println("All BytecodeDemo tests passed.");
    }

    static String exclaim(String s) { return s + "!"; }
}