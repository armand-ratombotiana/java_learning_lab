package com.learning.backend26;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluator;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoConfigurationExplorer implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment environment;

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
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String name : beanNames) {
            beanFactory.getBeanDefinition(name).setAttribute("explored", "true");
        }
    }

    public void exploreConditions() {
        ConditionEvaluator evaluator = new ConditionEvaluator(null, environment, null);
        StandardAnnotationMetadata metadata = new StandardAnnotationMetadata(getClass());

        ConditionOutcome outcome = evaluator.shouldSkip(metadata, null, ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        System.out.println("Condition evaluation outcome: " + outcome.isMatch());
    }
}