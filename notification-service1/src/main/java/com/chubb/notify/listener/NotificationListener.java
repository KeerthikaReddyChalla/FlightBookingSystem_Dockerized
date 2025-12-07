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
    public void handleMessage(NotificationMessage msg) {
        if (msg == null) {
            System.out.println("Received null message - ignoring");
            return;
        }
        try {
            // basic validation
            if (msg.getTo() == null || msg.getTo().isBlank()) {
                System.err.println("Notification missing recipient: " + msg);
                return;
            }
            // send (simulated)
            emailService.send(msg);
        } catch (Exception ex) {
            // log and swallow - RabbitMQ can be configured to DLQ if needed
            System.err.println("Failed to process notification: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // helper for tests to call directly
    public void handleMessageDirect(NotificationMessage msg) {
        handleMessage(msg);
    }
}
