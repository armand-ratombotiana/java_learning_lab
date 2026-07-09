package com.learning.backend26;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AutoConfigurationExplorerTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AutoConfigurationExplorer explorer;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }

    @Test
    void autoConfigurationExplorerBeanExists() {
        assertNotNull(explorer);
    }

    @Test
    void beanDefinitionsAreRegistered() {
        String[] beanNames = context.getBeanDefinitionNames();
        assertTrue(beanNames.length > 0);
        assertTrue(explorer.getClass().isAnnotationPresent(
                org.springframework.stereotype.Component.class));
    }

    @Test
    void exploreConditionsRunsWithoutError() {
        explorer.exploreConditions();
    }
}