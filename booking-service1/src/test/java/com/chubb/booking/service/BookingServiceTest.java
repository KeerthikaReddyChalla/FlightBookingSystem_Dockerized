package com.chubb.booking.service;

import com.chubb.booking.client.FlightClient;
import com.chubb.booking.dto.BookingRequest;
import com.chubb.booking.dto.FlightInventoryDto;
import com.chubb.booking.model.Booking;
import com.chubb.booking.model.Passenger;
import com.chubb.booking.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

    BookingRepository repo = Mockito.mock(BookingRepository.class);
    FlightClient flightClient = Mockito.mock(FlightClient.class);
    AmqpTemplate amqp = Mockito.mock(AmqpTemplate.class);
    BookingService service = new BookingService(repo, flightClient, amqp);

    @Test
    void testBookSuccess() {
        FlightInventoryDto flight = new FlightInventoryDto();
        flight.setId("FL1");
        flight.setAvailableSeats(3);
        flight.setDeparture(LocalDateTime.now().plusDays(2));

        Mockito.when(flightClient.getById("FL1")).thenReturn(flight);
        Mockito.when(repo.save(Mockito.any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingRequest req = new BookingRequest();
        req.setName("User");
        req.setEmail("user@example.com");
        req.setSeats(1);
        req.setPassengers(List.of(new Passenger()));
        req.setMeal("Veg");
        req.setSeatNumbers(List.of("1A"));

        Booking saved = service.book("FL1", req, "user@example.com");

        assertNotNull(saved.getPnr());
        assertEquals("FL1", saved.getFlightId());
    }

    @Test
    void testBookNotEnoughSeats() {
        FlightInventoryDto flight = new FlightInventoryDto();
        flight.setId("FL1");
        flight.setAvailableSeats(1);
        Mockito.when(flightClient.getById("FL1")).thenReturn(flight);

        BookingRequest req = new BookingRequest();
        req.setName("User");
        req.setEmail("user@example.com");
        req.setSeats(2);
        req.setPassengers(List.of(new Passenger()));
        req.setMeal("Veg");
        req.setSeatNumbers(List.of("1A","1B"));

        assertThrows(RuntimeException.class, () -> service.book("FL1", req, "user@example.com"));
    }

    @Test
    void testCancelWithin24Hours() {
        Booking b = new Booking();
        b.setPnr("PNR-1");
        b.setFlightDeparture(LocalDateTime.now().plusHours(12));
        Mockito.when(repo.findByPnr("PNR-1")).thenReturn(b);

        assertThrows(RuntimeException.class, () -> service.cancel("PNR-1"));
    }
}
