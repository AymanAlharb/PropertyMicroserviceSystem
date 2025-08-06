package com.ayman.propertyservice.properties;

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
    private OwnerShipQueue ownerShipQueue;
    private PropertyQueue propertyQueue;
    @Getter
    @Setter
    public static class EmailQueue {
        private String emailQueueName;
        private String routingEmailKeyName;
    }

    @Getter
    @Setter
    public static class OwnerShipQueue {
        private String ownerShipQueueName;
        private String routingOwnerShipQueueKeyName;
    }


    @Getter
    @Setter
    public static class PropertyQueue {
        private String propertyQueueName;
        private String routingPropertyQueueKeyName;
    }
}
