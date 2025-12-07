package com.chubb.flight.exception;

public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(String msg) {
        super(msg);
    }
}
