package com.chubb.auth.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "notification.exchange";
    public static final String RESET_PASSWORD_ROUTING_KEY = "reset.password.key";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }
    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    @Bean
//    public AmqpTemplate rabbitTemplate(
//            ConnectionFactory connectionFactory,
//            MessageConverter jsonConverter
//    ) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(jsonConverter);
//        return template;
//    }
}
