package com.learning.msg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class RabbitMQLab {
    public static void main(String[] args) {
        SpringApplication.run(RabbitMQLab.class, args);
    }
}

@RestController
class RabbitProducerController {

    private final RabbitTemplate rabbitTemplate;

    RabbitProducerController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/publish")
    public String publish(@RequestParam String message) {
        rabbitTemplate.convertAndSend("test-exchange", "test-routing-key", message);
        return "Published: " + message;
    }

    @PostMapping("/publish/queue")
    public String publishToQueue(@RequestParam String queue, @RequestParam String message) {
        rabbitTemplate.convertAndSend(queue, message);
        return "Published to " + queue + ": " + message;
    }
}