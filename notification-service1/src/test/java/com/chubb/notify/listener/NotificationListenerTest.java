package com.chubb.notify.listener;

import com.chubb.notify.model.NotificationMessage;
import com.chubb.notify.service.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NotificationListenerTest {

    @Test
    void testHandleMessageDirect_validMessage_callsEmailService() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        NotificationMessage msg =
                new NotificationMessage("user@example.com", "Test Subject", "Test Body");

        listener.handleMessageDirect(msg);

        Mockito.verify(emailService, Mockito.times(1)).send(Mockito.eq(msg));
    }

    @Test
    void testHandleMessageDirect_nullMessage_noInteraction() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        listener.handleMessageDirect(null);

        Mockito.verifyNoInteractions(emailService);
    }

    @Test
    void testHandleMessageDirect_missingRecipient_noInteraction() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        NotificationMessage msg =
                new NotificationMessage("", "Subject", "Body");

        listener.handleMessageDirect(msg);

        Mockito.verifyNoInteractions(emailService);
    }

    @Test
    void testHandleMessage_stringMessage_doesNotCallEmailService() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        // This should not trigger emailService.send()
        listener.handleMessage("Some plain text message from RabbitMQ");

        Mockito.verifyNoInteractions(emailService);
    }
}
