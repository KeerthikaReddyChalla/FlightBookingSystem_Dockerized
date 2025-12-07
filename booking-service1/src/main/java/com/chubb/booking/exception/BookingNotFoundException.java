package com.chubb.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String msg) { super(msg); }
}
