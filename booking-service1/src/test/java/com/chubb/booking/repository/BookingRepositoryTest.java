package com.chubb.booking.repository;

import com.chubb.booking.model.Booking;
import com.chubb.booking.model.Passenger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class BookingRepositoryTest {

    @Autowired BookingRepository repo;
    @BeforeEach
    void clean() {
        repo.deleteAll();
    }

    @Test
    void testSaveAndFind() {
        Booking b = new Booking();
        b.setPnr("PNR-TEST");
        b.setEmail("user@example.com");
        b.setSeats(1);
        b.setPassengers(List.of(new Passenger()));
        b.setBookingTime(LocalDateTime.now());
        b.setFlightDeparture(LocalDateTime.now().plusDays(3));
        repo.save(b);

        Booking found = repo.findByPnr("PNR-TEST");
        assertNotNull(found);
        assertEquals("user@example.com", found.getEmail());
    }
}
