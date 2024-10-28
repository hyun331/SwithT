package com.tweety.SwithT.common.configs;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Autowired
    private ConnectionFactory connectionFactory;

    public static final String SCHEDULER_ALERT_QUEUE = "schedulerAlertQueue";
    public static final String SCHEDULER_ALERT_EXCHANGE = "schedulerAlertExchange";
    public static final String SCHEDULER_ALERT_ROUTING_KEY = "schedulerAlertKey";

    @Bean
    public Queue schedulerAlertQueue() {
        return new Queue(SCHEDULER_ALERT_QUEUE, true);
    }

    @Bean
    public DirectExchange schedulerAlertExchange() {
        return new DirectExchange(SCHEDULER_ALERT_EXCHANGE);
    }

    @Bean
    public Binding schedulerAlertBinding(Queue schedulerAlertQueue, DirectExchange schedulerAlertExchange) {
        return BindingBuilder.bind(schedulerAlertQueue).to(schedulerAlertExchange).with(SCHEDULER_ALERT_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter Jackson2JsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    @Bean
    public Jackson2JsonMessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
