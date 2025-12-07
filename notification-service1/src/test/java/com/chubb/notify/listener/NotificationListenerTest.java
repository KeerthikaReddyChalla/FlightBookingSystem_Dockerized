package com.chubb.notify.listener;

import com.chubb.notify.model.NotificationMessage;
import com.chubb.notify.service.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NotificationListenerTest {

    @Test
    void testHandleMessage_validMessage_callsEmailService() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        NotificationMessage msg =
                new NotificationMessage("user@example.com", "Test Subject", "Test Body");

        // call the real Rabbit listener method (now accepts NotificationMessage)
        listener.handleMessage(msg);

        Mockito.verify(emailService, Mockito.times(1)).send(Mockito.eq(msg));
    }

    @Test
    void testHandleMessage_nullMessage_noInteraction() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        listener.handleMessage(null);

        Mockito.verifyNoInteractions(emailService);
    }

    @Test
    void testHandleMessage_missingRecipient_noInteraction() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        NotificationMessage msg = new NotificationMessage("", "Subject", "Body");

        listener.handleMessage(msg);

        Mockito.verifyNoInteractions(emailService);
    }
}
