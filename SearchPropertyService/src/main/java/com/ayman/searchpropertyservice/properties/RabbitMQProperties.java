package com.ayman.searchpropertyservice.properties;

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
    private PropertyQueue propertyQueue;

    @Getter
    @Setter
    public static class PropertyQueue {
        private String propertyQueueName;
        private String routingPropertyQueueKeyName;
    }
}
