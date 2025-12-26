package com.chubb.notify.service;

import com.chubb.notify.model.NotificationMessage;

public interface EmailService {
    void send(NotificationMessage message);
    void sendResetPasswordMail(String to, String resetLink);
}
