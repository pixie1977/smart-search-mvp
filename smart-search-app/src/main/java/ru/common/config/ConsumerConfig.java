package ru.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@EnableRabbit
public class ConsumerConfig {

    @Bean
    public String instanceId() {
        return "id-" + UUID.randomUUID();
    }

    @Bean
    public Queue instanceQueue(String instanceId) {
        return new Queue("my-queue-" + instanceId, true);
    }

    @Bean
    public Binding instanceBinding(Queue instanceQueue, FanoutExchange broadcastExchange) {
        return BindingBuilder.bind(instanceQueue).to(broadcastExchange);
    }

    @Bean
    public SimpleMessageListenerContainer container(String instanceId, ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("my-queue-" + instanceId);
        container.setMessageListener(listenerAdapter);
        return container;
    }
}