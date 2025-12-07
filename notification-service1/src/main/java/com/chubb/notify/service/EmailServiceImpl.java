package com.chubb.notify.service;

import com.chubb.notify.model.NotificationMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(NotificationMessage msg) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(msg.getTo());
            email.setSubject(msg.getSubject());
            email.setText(msg.getBody());

            mailSender.send(email);

            System.out.println("📧 Email sent successfully to " + msg.getTo());

        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
