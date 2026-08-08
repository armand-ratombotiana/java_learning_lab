package com.learning.backend26;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoConfigurationExplorer implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment environment;
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<String> configurations = SpringFactoriesLoader.loadFactoryNames(
                AutoConfigurationImportSelector.class, getClass().getClassLoader());

        for (String configClass : configurations) {
            GenericBeanDefinition bd = new GenericBeanDefinition();
            bd.setBeanClassName(configClass);
            bd.setRole(GenericBeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(configClass, bd);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String name : beanNames) {
            beanFactory.getBeanDefinition(name).setAttribute("explored", "true");
        }
    }

    public void exploreConditions() {
        ConditionEvaluationReport report = ConditionEvaluationReport.get(beanFactory);
        for (String source : report.getConditionAndOutcomesBySource().keySet()) {
            System.out.println("Condition source: " + source);
        }
    }
}