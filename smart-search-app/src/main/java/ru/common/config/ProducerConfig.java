package ru.common.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfig {

    @Bean
    public FanoutExchange broadcastExchange() {
        return new FanoutExchange("my-exchange");
    }
}