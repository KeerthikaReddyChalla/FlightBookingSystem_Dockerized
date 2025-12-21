package com.chubb.flight.service;

import com.chubb.flight.exception.FlightNotFoundException;
import com.chubb.flight.model.FlightInventory;
import com.chubb.flight.repository.FlightInventoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightInventoryRepository repo;

    public FlightService(FlightInventoryRepository repo) {
        this.repo = repo;
    }

    public FlightInventory addInventory(FlightInventory inv) {
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

}
