package com.java.reflection.annotations.lab03;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

interface Greeter {
    String greet(String name);
    void farewell(String name);
}

class RealGreeter implements Greeter {
    @Override
    public String greet(String name) {
        return "Hello, " + name + "!";
    }

    @Override
    public void farewell(String name) {
        System.out.println("Goodbye, " + name + ".");
    }
}

class LoggingHandler implements InvocationHandler {
    private final Object target;

    LoggingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("[PROXY] Before " + method.getName());
        Object result = method.invoke(target, args);
        System.out.println("[PROXY] After " + method.getName());
        return result;
    }
}

public class ProxyPatternLab {

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T target) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new LoggingHandler(target));
    }

    public static void main(String[] args) {
        Greeter real = new RealGreeter();
        Greeter proxy = createProxy(real);

        System.out.println("Proxy class: " + proxy.getClass().getName());
        System.out.println("Is proxy? " + Proxy.isProxyClass(proxy.getClass()));

        String msg = proxy.greet("world");
        System.out.println("Result: " + msg);

        proxy.farewell("world");
    }
}
