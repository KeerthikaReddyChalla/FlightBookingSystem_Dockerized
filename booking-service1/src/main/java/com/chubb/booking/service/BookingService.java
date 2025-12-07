package com.chubb.booking.service;

import com.chubb.booking.client.FlightClient;
import com.chubb.booking.config.RabbitConfig;
import com.chubb.booking.dto.BookingRequest;
import com.chubb.booking.dto.FlightInventoryDto;
import com.chubb.booking.exception.BadRequestException;
import com.chubb.booking.exception.BookingNotFoundException;
import com.chubb.booking.model.Booking;
import com.chubb.booking.repository.BookingRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.chubb.booking.dto.NotificationMessage;

@Service
public class BookingService {

    private final BookingRepository repo;
    private final FlightClient flightClient;
    private final AmqpTemplate amqp;

    public BookingService(BookingRepository repo, FlightClient flightClient, AmqpTemplate amqp) {
        this.repo = repo;
        this.flightClient = flightClient;
        this.amqp = amqp;
    }

    public Booking book(String flightId, BookingRequest req, String principalEmail) {
        // ensure logged-in user
    	// allow booking with any email
    	if (principalEmail == null) {
    	    throw new BadRequestException("User must be logged in to book a flight");
    	}


        FlightInventoryDto flight = flightClient.getById(flightId);
        if (flight == null) throw new BadRequestException("Flight not found");

        if (req.getSeats() > (flight.getAvailableSeats() == null ? 0 : flight.getAvailableSeats())) {
            throw new BadRequestException("Not enough seats available");
        }

        Booking b = new Booking();
        b.setFlightId(flightId);
        b.setName(req.getName());
        b.setEmail(req.getEmail());
        b.setSeats(req.getSeats());
        b.setPassengers(req.getPassengers());
        b.setMeal(req.getMeal());
        b.setSeatNumbers(req.getSeatNumbers());
        b.setBookingTime(LocalDateTime.now());
        b.setFlightDeparture(flight.getDeparture());
        b.setPnr(generatePnr());

        Booking saved = repo.save(b);

        NotificationMessage msg = new NotificationMessage(
                saved.getEmail(),
                "Your Flight Booking is Confirmed! PNR: " + saved.getPnr(),
                "Hello " + saved.getName() + ",\n\n" +
                "Your booking was successful!\n\n" +
                "PNR: " + saved.getPnr() + "\n" +
                "Seats Booked: " + saved.getSeats() + "\n\n" +
                "Thank you for choosing our service!"
        );

        amqp.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, msg);


       


        return saved;
    }

    public Booking getByPnr(String pnr) {
        Booking b = repo.findByPnr(pnr);
        if (b == null) throw new BookingNotFoundException("PNR not found: " + pnr);
        return b;
    }

    public List<Booking> history(String email) {
        return repo.findByEmail(email);
    }

    public void cancel(String pnr) {
        Booking b = repo.findByPnr(pnr);
        if (b == null) throw new BookingNotFoundException("PNR not found: " + pnr);
        if (b.getFlightDeparture() == null) throw new BadRequestException("Flight departure unknown");
        if (b.getFlightDeparture().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new BadRequestException("Cannot cancel within 24 hours of departure");
        }
        b.setCancelled(true);
        repo.save(b);
        var msg = String.format("Booking cancelled for %s, PNR: %s", b.getEmail(), b.getPnr());
        amqp.convertAndSend(RabbitConfig.EXCHANGE, "booking.cancel", msg);
    }

    private String generatePnr() {
        return "PNR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
