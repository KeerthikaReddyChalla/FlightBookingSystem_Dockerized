package com.chubb.notify.service;

import com.chubb.notify.model.NotificationMessage;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    public void send(NotificationMessage msg) {
 
        System.out.println("=== Sending email ===");
        System.out.println("To      : " + msg.getTo());
        System.out.println("Subject : " + msg.getSubject());
        System.out.println("Body    : " + msg.getBody());
        System.out.println("=====================");
    }
}
