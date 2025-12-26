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

    public BookingService(BookingRepository repo,
                          FlightClient flightClient,
                          AmqpTemplate amqp) {
        this.repo = repo;
        this.flightClient = flightClient;
        this.amqp = amqp;
    }

    public Booking book(String flightId, BookingRequest req, String principalEmail) {

        if (principalEmail == null) {
            throw new BadRequestException("User must be logged in to book a flight");
        }

        if (req.getSeatNumbers() == null || req.getSeatNumbers().isEmpty()) {
            throw new BadRequestException("No seats selected");
        }

        if (req.getPassengers() == null ||
            req.getPassengers().size() != req.getSeatNumbers().size()) {
            throw new BadRequestException("Passenger count and seat count must match");
        }

        FlightInventoryDto flight = flightClient.getById(flightId);
        if (flight == null) {
            throw new BadRequestException("Flight not found");
        }

        if (flight.getAvailableSeats() < req.getSeatNumbers().size()) {
            throw new BadRequestException("Not enough seats available");
        }

        // 🔒 SINGLE call – transactional in FlightService
        flightClient.bookSeats(flightId, req.getSeatNumbers());

        Booking booking = new Booking();
        booking.setFlightId(flightId);
        booking.setName(req.getName());
        booking.setEmail(req.getEmail());
        booking.setSeats(req.getSeatNumbers().size());
        booking.setPassengers(req.getPassengers());
        booking.setMeal(req.getMeal());
        booking.setSeatNumbers(req.getSeatNumbers());
        booking.setBookingTime(LocalDateTime.now());
        booking.setFlightDeparture(flight.getDeparture());
        booking.setPnr(generatePnr());

        Booking saved = repo.save(booking);

        NotificationMessage msg = new NotificationMessage(
                saved.getEmail(),
                "Your Flight Booking is Confirmed! PNR: " + saved.getPnr(),
                "Hello " + saved.getName() + ",\n\n" +
                "Your booking was successful!\n\n" +
                "PNR: " + saved.getPnr() + "\n" +
                "Seats: " + String.join(", ", saved.getSeatNumbers()) + "\n\n" +
                "Thank you for choosing our service!"
        );

        amqp.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                msg
        );

        return saved;
    }

    public Booking getByPnr(String pnr) {
        Booking b = repo.findByPnr(pnr);
        if (b == null) {
            throw new BookingNotFoundException("PNR not found: " + pnr);
        }
        return b;
    }

    public List<Booking> history(String email) {
        return repo.findByEmail(email);
    }

    public void cancel(String pnr) {

        Booking b = repo.findByPnr(pnr);
        if (b == null) {
            throw new BookingNotFoundException("PNR not found: " + pnr);
        }

        if (b.getFlightDeparture().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new BadRequestException("Cannot cancel within 24 hours of departure");
        }

        flightClient.unbookSeats(
                b.getFlightId(),
                b.getSeatNumbers()
        );

        b.setCancelled(true);
        repo.save(b);

        NotificationMessage notification = new NotificationMessage(
                b.getEmail(),
                "Flight Ticket Cancelled - PNR " + b.getPnr(),
                "Your booking has been cancelled.\n\nPNR: " + b.getPnr()
        );

        amqp.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                notification
        );
    }

    private String generatePnr() {
        return "PNR-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }
}
