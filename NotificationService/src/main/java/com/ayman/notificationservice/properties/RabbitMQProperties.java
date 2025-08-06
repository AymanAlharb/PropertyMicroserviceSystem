package com.ayman.notificationservice.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "rabbitmq")
@Validated
@Getter
@Setter
public class RabbitMQProperties {

    private String exchangeName;
    private EmailQueue emailQueue;

    @Getter
    @Setter
    public static class EmailQueue {
        private String emailQueueName;
        private String routingEmailKeyName;
    }
}

