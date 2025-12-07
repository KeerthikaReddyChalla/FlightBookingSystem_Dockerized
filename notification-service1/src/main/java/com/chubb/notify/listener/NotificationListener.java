package com.chubb.notify.listener;

import com.chubb.notify.model.NotificationMessage;
import com.chubb.notify.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private final EmailService emailService;

    public NotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    
    @RabbitListener(queues = "${notification.queue.name:booking.queue}")
    public void handleMessage(NotificationMessage msg) {
        System.out.println("Received NotificationMessage: " + msg);

        if (msg == null) {
            System.out.println("Null message received");
            return;
        }

        emailService.send(msg);
    }

  
    public void handleMessageDirect(NotificationMessage msg) {
        System.out.println("[TEST] Received NotificationMessage: " + msg);
        if (msg != null && msg.getTo() != null && !msg.getTo().isBlank()) {
            emailService.send(msg);
        }
    }
}
