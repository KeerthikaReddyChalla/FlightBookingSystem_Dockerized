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

    // RabbitMQ listener (String)
    @RabbitListener(queues = "${notification.queue.name:booking.queue}")
    public void handleMessage(String msg) {
        System.out.println("Notification received (String): " + msg);
    }

    // Test-only method (NotificationMessage)
    public void handleMessageDirect(NotificationMessage msg) {
        System.out.println("Notification received (Test Mode): " + msg);

        if (msg == null) {
            System.out.println("Received null message - ignoring");
            return;
        }

        if (msg.getTo() == null || msg.getTo().isBlank()) {
            System.err.println("Notification missing recipient: " + msg);
            return;
        }

        try {
            emailService.send(msg);
        } catch (Exception ex) {
            System.err.println("Failed to process notification: " + ex.getMessage());
        }
    }
}
