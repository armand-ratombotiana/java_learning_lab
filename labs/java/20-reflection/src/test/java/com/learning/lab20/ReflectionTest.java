package com.learning.lab20;

import org.junit.jupiter.api.*;
import java.lang.reflect.*;
import static org.junit.jupiter.api.Assertions.*;

class ReflectionTest {

    @Test
    @DisplayName("Class.forName loads class dynamically")
    void classForNameLoadsClass() throws Exception {
        Class<?> clazz = Class.forName("java.lang.String");
        assertEquals(String.class, clazz);
    }

    @Test
    @DisplayName("Get declared fields of a class")
    void getDeclaredFields() throws Exception {
        Class<?> clazz = Class.forName("com.learning.lab20.ReflectionTestModel");
        Field[] fields = clazz.getDeclaredFields();
        assertTrue(fields.length >= 2);
    }

    @Test
    @DisplayName("Invoke method via reflection")
    void invokeMethodViaReflection() throws Exception {
        var obj = new ReflectionTestModel("test", 42);
        Method method = obj.getClass().getMethod("getName");
        String result = (String) method.invoke(obj);
        assertEquals("test", result);
    }

    @Test
    @DisplayName("Access private field via reflection")
    void accessPrivateField() throws Exception {
        var obj = new ReflectionTestModel("secret", 1);
        Field field = obj.getClass().getDeclaredField("name");
        field.setAccessible(true);
        assertEquals("secret", field.get(obj));
    }

    @Test
    @DisplayName("Get constructor and create instance")
    void constructorNewInstance() throws Exception {
        Constructor<ReflectionTestModel> ctor = ReflectionTestModel.class.getConstructor(String.class, int.class);
        var instance = ctor.newInstance("created", 99);
        assertEquals("created", instance.getName());
        assertEquals(99, instance.getValue());
    }

    @Test
    @DisplayName("Array reflection creates and manipulates arrays")
    void arrayReflection() {
        int[] array = (int[]) Array.newInstance(int.class, 5);
        assertEquals(5, Array.getLength(array));
        Array.set(array, 0, 42);
        assertEquals(42, Array.get(array, 0));
    }

    @Test
    @DisplayName("Dynamic proxy invokes handler")
    void dynamicProxyInvokes() {
        ProxiedInterface proxy = (ProxiedInterface) Proxy.newProxyInstance(
            ProxiedInterface.class.getClassLoader(),
            new Class<?>[]{ProxiedInterface.class},
            (obj, method, args) -> "proxied: " + args[0]
        );
        assertEquals("proxied: hello", proxy.say("hello"));
    }

    @Test
    @DisplayName("Get methods returns class methods")
    void getMethods() throws Exception {
        Method[] methods = ReflectionTestModel.class.getMethods();
        assertTrue(methods.length >= 2);
    }

    @Test
    @DisplayName("Modifiers are accessible via reflection")
    void modifiersCheck() throws Exception {
        Field field = ReflectionTestModel.class.getDeclaredField("value");
        assertTrue(Modifier.isPrivate(field.getModifiers()));
    }
}

class ReflectionTestModel {
    private String name;
    private int value;

    public ReflectionTestModel(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public int getValue() { return value; }
}

interface ProxiedInterface {
    String say(String message);
}
