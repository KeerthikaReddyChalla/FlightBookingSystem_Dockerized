package com.chubb.flight.service;

import com.chubb.flight.exception.FlightNotFoundException;
import com.chubb.flight.model.FlightInventory;
import com.chubb.flight.model.Seat;
import com.chubb.flight.repository.FlightInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightService {

    private final FlightInventoryRepository repo;

    public FlightService(FlightInventoryRepository repo) {
        this.repo = repo;
    }

    public FlightInventory addInventory(FlightInventory inv) {
    	 inv.setSeats(generateSeats(inv.getAvailableSeats()));
        return repo.save(inv);
    }

    public List<FlightInventory> search(String from, String to, LocalDateTime start, LocalDateTime end) {
        return repo.findByFromPlaceAndToPlaceAndDepartureBetween(from, to, start, end);
    }

    public FlightInventory getById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found with ID: " + id));
    }
    public void decreaseSeats(String flightId, int count) {
        FlightInventory f = repo.findById(flightId)
            .orElseThrow(() -> new RuntimeException("Flight not found"));

        if (f.getAvailableSeats() < count) {
            throw new RuntimeException("Not enough seats");
        }

        f.setAvailableSeats(f.getAvailableSeats() - count);
        repo.save(f);
    }

    public void increaseSeats(String flightId, int count) {
        FlightInventory f = repo.findById(flightId)
            .orElseThrow(() -> new RuntimeException("Flight not found"));

        f.setAvailableSeats(f.getAvailableSeats() + count);
        repo.save(f);
    }
    private List<Seat> generateSeats(int totalSeats) {

        List<Seat> seats = new ArrayList<>();
        char[] columns = {'A','B','C','D','E','F'};
        int seatCount = 0;
        int row = 1;

        while (seatCount < totalSeats) {
            for (char col : columns) {
                if (seatCount >= totalSeats) break;
                seats.add(new Seat(col + String.valueOf(row), false));
                seatCount++;
            }
            row++;
        }
        return seats;
    }
    public List<Seat> getSeats(String flightId) {
        FlightInventory flight = repo.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        return flight.getSeats();
    }
    @Transactional
    public void lockSeats(String flightId, List<String> requestedSeats) {

        FlightInventory flight = repo.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        for (Seat seat : flight.getSeats()) {
            if (requestedSeats.contains(seat.getSeatNumber())) {

                if (seat.isBooked()) {
                    throw new RuntimeException("Seat already booked");
                }

                seat.setBooked(true);
            }
        }

        flight.setAvailableSeats(
                flight.getAvailableSeats() - requestedSeats.size()
        );

        repo.save(flight);
    }
    public void unbookSeats(String flightId, List<String> seatNumbers) {

        FlightInventory flight = repo.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        for (Seat seat : flight.getSeats()) {
            if (seatNumbers.contains(seat.getSeatNumber())) {
                seat.setBooked(false); 
            }
        }

        flight.setAvailableSeats(
                flight.getAvailableSeats() + seatNumbers.size()
        );

        repo.save(flight);
    }

    public List<FlightInventory> getAllInventories() {
        return repo.findAll();
    }


}
