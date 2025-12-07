package com.chubb.flight.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testFlightNotFoundException() {
        FlightNotFoundException ex = new FlightNotFoundException("Not found");
        ResponseEntity<?> res = handler.handleNotFound(ex);

        assertEquals(404, res.getStatusCode().value());
        assertEquals("Not found", res.getBody());
    }

    @Test
    void testGenericException() {
        Exception ex = new Exception("Internal");
        ResponseEntity<?> res = handler.handleOthers(ex);

        assertEquals(500, res.getStatusCode().value());
        assertTrue(res.getBody().toString().contains("Internal"));
    }
}
