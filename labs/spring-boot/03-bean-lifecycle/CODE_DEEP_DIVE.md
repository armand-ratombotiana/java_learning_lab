# Spring Bean Lifecycle Code Deep Dive

This lab provides a pure Java Spring Boot example that logs every single phase of a Bean's lifecycle to demonstrate the order of execution.

## 💻 Pure Java Implementation

```java file="labs/spring-boot/03-bean-lifecycle/SOLUTION/LifecycleLoggingBean.java"
package springboot.internals.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * A bean that logs every lifecycle event to console.
 */
@Component
public class LifecycleLoggingBean implements 
        BeanNameAware, 
        ApplicationContextAware, 
        InitializingBean, 
        DisposableBean {

    private String beanName;

    public LifecycleLoggingBean() {
        System.out.println("1. [CONSTRUCTOR] LifecycleLoggingBean instance created.");
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
        System.out.println("2. [AWARE] setBeanName called: " + name);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("3. [AWARE] setApplicationContext called.");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("4. [ANNOTATION] @PostConstruct method called.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("5. [INTERFACE] InitializingBean.afterPropertiesSet called.");
    }

    public void initMethod() {
        System.out.println("6. [CUSTOM] Custom init-method called.");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("7. [ANNOTATION] @PreDestroy method called.");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("8. [INTERFACE] DisposableBean.destroy called.");
    }
}

/**
 * Demonstrates the BeanPostProcessor interceptors.
 */
@Component
class CustomBPP implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LifecycleLoggingBean) {
            System.out.println(">> [BPP] postProcessBeforeInitialization for: " + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LifecycleLoggingBean) {
            System.out.println(">> [BPP] postProcessAfterInitialization for: " + beanName);
        }
        return bean;
    }
}
```

## 🔍 Key Takeaways
1. **The Sequence**: If you run this in a Spring Boot app, you will see that `@PostConstruct` runs *after* the Aware interfaces but *before* `InitializingBean`.
2. **Global Impact**: `BeanPostProcessor` is a global interface. Every BPP in the context is called for *every* bean in the context. This is why we use `if (bean instanceof ...)` to target specific beans.
3. **Proxy Timing**: If you were to add `@Transactional` to this class, the `postProcessAfterInitialization` log would show that the bean being returned is no longer a `LifecycleLoggingBean`, but a dynamic proxy class.