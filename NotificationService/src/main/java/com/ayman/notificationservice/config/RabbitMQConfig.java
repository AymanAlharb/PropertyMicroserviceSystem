package com.ayman.notificationservice.config;


import com.ayman.notificationservice.properties.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RabbitMQConfig {
    private final RabbitMQProperties rabbitMQProperties;
    @Bean
    public Queue emailQueue() {
        return new Queue(rabbitMQProperties.getEmailQueue().getEmailQueueName());
    }


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(rabbitMQProperties.getExchangeName());
    }


    @Bean
    public Binding emailQueueBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(exchange())
                .with(rabbitMQProperties.getEmailQueue().getRoutingEmailKeyName());
    }


    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }


}
