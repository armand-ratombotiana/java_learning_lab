package com.learning.lab.module14;

import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.springframework.core.io.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 14: Spring Core ===");
        beanFactoryDemo();
        dependencyInjectionDemo();
        beanScopesDemo();
        lifecycleCallbacksDemo();
    }

    static void beanFactoryDemo() {
        System.out.println("\n--- BeanFactory Demo ---");
        try {
            BeanFactory factory = new XmlBeanFactory(
                new ClassPathResource("beans.xml"));
            MessageService service = (MessageService) factory.getBean("messageService");
            service.sendMessage("Hello Spring!");
        } catch (Exception e) {
            System.out.println("Note: beans.xml not found. Showing programmatic approach:");
            DefaultListableBeanFactory context = new DefaultListableBeanFactory();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(MessageServiceImpl.class);
            context.registerBeanDefinition("messageService", builder.getBeanDefinition());
            MessageService svc = context.getBean(MessageService.class);
            svc.sendMessage("Hello Spring!");
        }
    }

    static void dependencyInjectionDemo() {
        System.out.println("\n--- Dependency Injection Demo ---");
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        
        BeanDefinitionBuilder emailSvc = BeanDefinitionBuilder
            .rootBeanDefinition(EmailService.class);
        factory.registerBeanDefinition("emailService", emailSvc.getBeanDefinition());

        BeanDefinitionBuilder userSvc = BeanDefinitionBuilder
            .rootBeanDefinition(UserService.class)
            .addConstructorArgumentReference("emailService");
        factory.registerBeanDefinition("userService", userSvc.getBeanDefinition());

        UserService userService = factory.getBean(UserService.class);
        userService.processUser("john@example.com");
    }

    static void beanScopesDemo() {
        System.out.println("\n--- Bean Scopes Demo ---");
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        
        BeanDefinitionBuilder prototype = BeanDefinitionBuilder
            .rootBeanDefinition(PrototypeBean.class)
            .setScope("prototype");
        factory.registerBeanDefinition("prototypeBean", prototype.getBeanDefinition());

        BeanDefinitionBuilder singleton = BeanDefinitionBuilder
            .rootBeanDefinition(SingletonBean.class);
        factory.registerBeanDefinition("singletonBean", singleton.getBeanDefinition());

        PrototypeBean p1 = factory.getBean(PrototypeBean.class);
        PrototypeBean p2 = factory.getBean(PrototypeBean.class);
        System.out.println("Prototype beans same: " + (p1 == p2));

        SingletonBean s1 = factory.getBean(SingletonBean.class);
        SingletonBean s2 = factory.getBean(SingletonBean.class);
        System.out.println("Singleton beans same: " + (s1 == s2));
    }

    static void lifecycleCallbacksDemo() {
        System.out.println("\n--- Lifecycle Callbacks Demo ---");
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
            .rootBeanDefinition(LifecycleBean.class);
        factory.registerBeanDefinition("lifecycleBean", builder.getBeanDefinition());

        factory.addBeanPostProcessor(new BeanPostProcessor() {
            public Object postProcessBeforeInitialization(Object bean, String name) {
                System.out.println("Before init: " + name);
                return bean;
            }
            public Object postProcessAfterInitialization(Object bean, String name) {
                System.out.println("After init: " + name);
                return bean;
            }
        });

        LifecycleBean bean = factory.getBean(LifecycleBean.class);
        System.out.println("Bean created: " + bean);
    }
}

interface MessageService {
    void sendMessage(String msg);
}
class MessageServiceImpl implements MessageService {
    public void sendMessage(String msg) { System.out.println("Message sent: " + msg); }
}

interface EmailService { void send(String to, String msg); }
class EmailServiceImpl implements EmailService {
    public void send(String to, String msg) { System.out.println("Email to " + to + ": " + msg); }
}
class UserService {
    private final EmailService emailService;
    public UserService(EmailService emailService) { this.emailService = emailService; }
    public void processUser(String email) { emailService.send(email, "Welcome!"); }
}

class PrototypeBean {
    private final String id = java.util.UUID.randomUUID().toString();
    public String toString() { return "PrototypeBean-" + id; }
}
class SingletonBean {
    private final String id = java.util.UUID.randomUUID().toString();
    public String toString() { return "SingletonBean-" + id; }
}

class LifecycleBean {
    public LifecycleBean() { System.out.println("LifecycleBean: Constructor"); }
    public void init() { System.out.println("LifecycleBean: init()"); }
    public void destroy() { System.out.println("LifecycleBean: destroy()"); }
}

import org.springframework.beans.factory.support.*;
import org.springframework.context.*;
import org.springframework.core.io.ClassPathResource;
