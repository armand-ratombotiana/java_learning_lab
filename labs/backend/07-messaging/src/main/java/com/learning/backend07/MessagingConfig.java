package com.learning.backend07;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;

/**
 * Messaging configuration supporting JMS.
 *
 * @EnableJms activates detection of @JmsListener annotations on beans.
 * In production, you would also configure KafkaTemplate/RabbitTemplate beans
 * depending on your messaging broker of choice.
 */
@Configuration
@EnableJms
public class MessagingConfig {

    private static final Logger log = LoggerFactory.getLogger(MessagingConfig.class);

    public static final String ORDER_QUEUE = "order.queue";
    public static final String NOTIFICATION_TOPIC = "notification.topic";

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        log.info("Configuring JMS listener container factory");
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("3-10");
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        log.info("Configuring JMS template");
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDeliveryPersistent(true);
        return template;
    }
}
