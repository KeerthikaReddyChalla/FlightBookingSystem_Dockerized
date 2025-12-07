package com.chubb.booking.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleNotFound() {
        BookingNotFoundException ex = new BookingNotFoundException("not found");
        ResponseEntity<?> r = handler.handleNotFound(ex);
        assertEquals(404, r.getStatusCodeValue());
        assertEquals("not found", r.getBody());
    }

    @Test
    void testHandleBad() {
        BadRequestException ex = new BadRequestException("bad");
        ResponseEntity<?> r = handler.handleBad(ex);
        assertEquals(400, r.getStatusCodeValue());
        assertEquals("bad", r.getBody());
    }
}
