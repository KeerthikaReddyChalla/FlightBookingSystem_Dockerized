package com.chubb.notify.listener;

import com.chubb.notify.config.RabbitConfig;
import com.chubb.notify.model.NotificationMessage;
import com.chubb.notify.model.ResetPasswordMessage;
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
    @RabbitListener(queues = RabbitConfig.RESET_PASSWORD_QUEUE)
    public void handleResetPassword(ResetPasswordMessage msg) {

        System.out.println("Received Reset Password Message: " + msg);

        if (msg == null) return;

        emailService.sendResetPasswordMail(
                msg.getEmail(),
                msg.getResetLink()
        );
    }

  
    public void handleMessageDirect(NotificationMessage msg) {
        System.out.println("[TEST] Received NotificationMessage: " + msg);
        if (msg != null && msg.getTo() != null && !msg.getTo().isBlank()) {
            emailService.send(msg);
        }
    }
}
