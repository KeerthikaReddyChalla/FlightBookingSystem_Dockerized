package com.chubb.notify.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;


@Configuration
public class RabbitConfig {

    public static final String QUEUE = "booking.queue";
    public static final String RESET_PASSWORD_QUEUE = "reset.password.queue";

    @Bean
    public Queue bookingQueue() {
        // durable=true
        return new Queue(QUEUE, true);
    }
    @Bean
    public Queue resetPasswordQueue() {
        return new Queue(RESET_PASSWORD_QUEUE, true);
    }
    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
