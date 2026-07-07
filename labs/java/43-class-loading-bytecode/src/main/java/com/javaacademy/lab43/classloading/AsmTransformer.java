package com.javaacademy.lab43.classloading;

import org.objectweb.asm.*;

/**
 * Uses ASM to add a class-level annotation at runtime.
 * Demonstrates bytecode transformation with the ASM library.
 */
public class AsmTransformer {

    public static byte[] addAnnotation(byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

        reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public void visitEnd() {
                AnnotationVisitor av = cv.visitAnnotation("Lcom/javaacademy/lab43/classloading/GeneratedAnnotation;", true);
                if (av != null) {
                    av.visit("value", "transformed by ASM");
                    av.visitEnd();
                }
                super.visitEnd();
            }
        }, 0);

        return writer.toByteArray();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== ASM Bytecode Transformer ===\n");

        Class<?> clazz = AsmTransformer.class;
        byte[] original = clazz.getResourceAsStream(clazz.getSimpleName() + ".class").readAllBytes();
        byte[] transformed = addAnnotation(original);

        System.out.println("Original size: " + original.length + " bytes");
        System.out.println("Transformed size: " + transformed.length + " bytes");

        ClassReader cr = new ClassReader(transformed);
        System.out.println("Class: " + cr.getClassName());
    }
}
