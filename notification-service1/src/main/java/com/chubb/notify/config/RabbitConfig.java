package com.chubb.notify.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE = "booking.queue";

    @Bean
    public Queue bookingQueue() {
        // durable=true
        return new Queue(QUEUE, true);
    }
}
