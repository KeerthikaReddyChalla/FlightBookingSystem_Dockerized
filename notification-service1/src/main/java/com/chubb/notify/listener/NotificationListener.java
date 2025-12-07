package com.chubb.notify.listener;

import com.chubb.notify.model.NotificationMessage;
import com.chubb.notify.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final EmailService emailService;

    // constructor injection (listener is not a controller so constructors are fine)
    public NotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    // queue name must match booking-service1 config (booking.queue)
    @RabbitListener(queues = "${notification.queue.name:booking.queue}")
    public void handleMessage(String msg) {
        System.out.println("Notification received: " + msg);
    }

    // helper for tests to call directly
    public void handleMessageDirect(String msg) {
        handleMessage(msg);
    }

}
