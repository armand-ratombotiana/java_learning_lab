package com.learning.lab.module14;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.junit.jupiter.api.Assertions.*;

class ExampleTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
    }

    @Test
    @DisplayName("Test singleton bean scope returns same instance")
    void testSingletonScope() {
        SingletonBean bean1 = context.getBean(SingletonBean.class);
        SingletonBean bean2 = context.getBean(SingletonBean.class);

        assertSame(bean1, bean2, "Singleton beans should return the same instance");
    }

    @Test
    @DisplayName("Test prototype bean scope returns different instances")
    void testPrototypeScope() {
        PrototypeBean bean1 = context.getBean(PrototypeBean.class);
        PrototypeBean bean2 = context.getBean(PrototypeBean.class);

        assertNotSame(bean1, bean2, "Prototype beans should return different instances");
    }

    @Test
    @DisplayName("Test dependency injection via constructor")
    void testConstructorInjection() {
        UserService userService = context.getBean(UserService.class);

        assertNotNull(userService, "UserService should be created");
        assertNotNull(userService.getEmailService(), "EmailService should be injected");
    }

    @Test
    @DisplayName("Test bean registration and retrieval")
    void testBeanRegistration() {
        String[] beanNames = context.getBeanDefinitionNames();

        assertTrue(beanNames.length > 0, "Should have registered beans");
        assertTrue(containsBean(beanNames, "emailService"), "Should contain emailService");
        assertTrue(containsBean(beanNames, "userService"), "Should contain userService");
    }

    @Test
    @DisplayName("Test message service functionality")
    void testMessageService() {
        MessageService messageService = context.getBean(MessageService.class);

        assertDoesNotThrow(() -> messageService.sendMessage("Test message"));
    }

    private boolean containsBean(String[] beanNames, String name) {
        for (String beanName : beanNames) {
            if (beanName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Configuration
    static class TestConfig {

        @Bean
        @Scope("singleton")
        public SingletonBean singletonBean() {
            return new SingletonBean();
        }

        @Bean
        @Scope("prototype")
        public PrototypeBean prototypeBean() {
            return new PrototypeBean();
        }

        @Bean
        public EmailService emailService() {
            return new EmailServiceImpl();
        }

        @Bean
        public UserService userService(EmailService emailService) {
            return new UserService(emailService);
        }

        @Bean
        public MessageService messageService() {
            return new MessageServiceImpl();
        }
    }

    interface MessageService {
        void sendMessage(String msg);
    }

    static class MessageServiceImpl implements MessageService {
        public void sendMessage(String msg) {
            assertNotNull(msg, "Message should not be null");
        }
    }

    interface EmailService {
        void send(String to, String msg);
    }

    static class EmailServiceImpl implements EmailService {
        public void send(String to, String msg) {
            assertNotNull(to);
            assertNotNull(msg);
        }
    }

    static class UserService {
        private final EmailService emailService;

        public UserService(EmailService emailService) {
            this.emailService = emailService;
        }

        public EmailService getEmailService() {
            return emailService;
        }

        public void processUser(String email) {
            emailService.send(email, "Welcome!");
        }
    }

    static class PrototypeBean {
        private final String id = java.util.UUID.randomUUID().toString();

        public String getId() {
            return id;
        }
    }

    static class SingletonBean {
        private final String id = java.util.UUID.randomUUID().toString();

        public String getId() {
            return id;
        }
    }
}