package com.learning.lab20;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.*;

class ReflectionUltraDeepTest {

    @Test
    void getSuperclassViaReflection() throws Exception {
        Class<?> clazz = Class.forName("com.learning.lab20.ReflectionTestModel");
        assertEquals(Object.class, clazz.getSuperclass());
    }

    @Test
    void getInterfacesViaReflection() {
        Class<?> clazz = java.util.ArrayList.class;
        Class<?>[] interfaces = clazz.getInterfaces();
        assertTrue(interfaces.length > 0);
    }

    @Test
    void invokePrivateMethodViaReflection() throws Exception {
        var obj = new PrivateMethodClass("secret");
        Method method = obj.getClass().getDeclaredMethod("getSecret");
        method.setAccessible(true);
        String result = (String) method.invoke(obj);
        assertEquals("secret", result);
    }

    @Test
    void proxyEqualityCheck() {
        ProxiedInterface proxy = (ProxiedInterface) Proxy.newProxyInstance(
            ProxiedInterface.class.getClassLoader(),
            new Class<?>[]{ProxiedInterface.class, java.io.Serializable.class},
            (obj, method, args) -> "proxy"
        );
        assertTrue(Proxy.isProxyClass(proxy.getClass()));
    }

    @Test
    void proxyGetInvocationHandler() {
        InvocationHandler handler = (obj, method, args) -> "handled";
        ProxiedInterface proxy = (ProxiedInterface) Proxy.newProxyInstance(
            ProxiedInterface.class.getClassLoader(),
            new Class<?>[]{ProxiedInterface.class},
            handler
        );
        assertSame(handler, Proxy.getInvocationHandler(proxy));
    }
}

class PrivateMethodClass {
    private String data;
    public PrivateMethodClass(String data) { this.data = data; }
    private String getSecret() { return data; }
}
