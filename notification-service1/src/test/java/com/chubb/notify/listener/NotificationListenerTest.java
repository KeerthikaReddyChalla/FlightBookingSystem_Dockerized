package com.chubb.notify.listener;

import com.chubb.notify.model.NotificationMessage;
import com.chubb.notify.service.EmailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NotificationListenerTest {

    @Test
    void testHandleMessage_printsWithoutException() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        NotificationMessage msg = new NotificationMessage("user@example.com", "Test Subject", "Test Body");
        listener.handleMessageDirect(msg);

        Mockito.verify(emailService, Mockito.times(1)).send(Mockito.eq(msg));
    }

    @Test
    void testHandleMessage_nullIsIgnored() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        listener.handleMessageDirect(null);

        Mockito.verifyNoInteractions(emailService);
    }

    @Test
    void testHandleMessage_missingRecipient() {
        EmailService emailService = Mockito.mock(EmailService.class);
        NotificationListener listener = new NotificationListener(emailService);

        NotificationMessage msg = new NotificationMessage("", "S", "B");
        listener.handleMessageDirect(msg);

        Mockito.verifyNoInteractions(emailService);
    }
}
