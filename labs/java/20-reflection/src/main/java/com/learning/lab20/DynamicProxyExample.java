package com.learning.lab20;

import java.lang.reflect.*;

/**
 * Demonstrates dynamic proxy creation using InvocationHandler and Proxy.newProxyInstance.
 */
public class DynamicProxyExample {

    public static void showDynamicProxy() {
        System.out.println("=== Dynamic Proxy ===");

        Service realService = new RealService();
        Service proxy = (Service) Proxy.newProxyInstance(
            Service.class.getClassLoader(),
            new Class<?>[]{Service.class},
            new LoggingHandler(realService)
        );

        proxy.execute("Task 1");
        System.out.println();
        proxy.execute("Task 2");
    }
}

interface Service {
    void execute(String task);
}

class RealService implements Service {
    @Override
    public void execute(String task) {
        System.out.println("  RealService executing: " + task);
    }
}

class LoggingHandler implements InvocationHandler {
    private final Object target;

    public LoggingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("  [Proxy] Before " + method.getName() + " with args: " + java.util.Arrays.toString(args));
        Object result = method.invoke(target, args);
        System.out.println("  [Proxy] After " + method.getName());
        return result;
    }
}
