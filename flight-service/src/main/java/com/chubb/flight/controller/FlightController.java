package com.chubb.flight.controller;

import com.chubb.flight.model.FlightInventory;
import com.chubb.flight.model.Seat;
import com.chubb.flight.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flight")
public class FlightController {

    private final FlightService service;

    public FlightController(FlightService service) {
        this.service = service;
    }

    @PostMapping("/airline/inventory/add")
    public ResponseEntity<FlightInventory> addInventory(@RequestBody @Valid FlightInventory inventory) {
        FlightInventory saved = service.addInventory(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/search")
    public ResponseEntity<List<FlightInventory>> search(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(service.search(from, to, start, end));
    }

    @GetMapping("/inventory/{id}")
    public ResponseEntity<FlightInventory> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @PutMapping("/{id}/seats/decrease/{count}")
    public void decreaseSeats(@PathVariable String id, @PathVariable int count) {
        service.decreaseSeats(id, count);
    }

    @PutMapping("/{id}/seats/increase/{count}")
    public void increaseSeats(@PathVariable String id, @PathVariable int count) {
        service.increaseSeats(id, count);
    }
    //seat map
    @GetMapping("/airline/inventory/{flightId}/seats")
    public ResponseEntity<List<Seat>> getSeats(@PathVariable String flightId) {
        return ResponseEntity.ok(service.getSeats(flightId));
    }
    @PutMapping("/airline/inventory/{flightId}/lock-seats")
    public ResponseEntity<Void> lockSeats(
            @PathVariable String flightId,
            @RequestBody List<String> seatNumbers
    ) {
        service.lockSeats(flightId, seatNumbers);
        return ResponseEntity.ok().build();
    }
    //un-booking seats
    @PutMapping("/airline/inventory/{flightId}/seats/unbook")
    public ResponseEntity<Void> unbookSeats(
            @PathVariable String flightId,
            @RequestBody List<String> seatNumbers
    ) {
        service.unbookSeats(flightId, seatNumbers);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/airline/inventory/all")
    public ResponseEntity<List<FlightInventory>> getAllInventories() {
        return ResponseEntity.ok(service.getAllInventories());
    }
    @PutMapping("/airline/inventory/{flightId}/seats/book")
    public ResponseEntity<Void> bookSeats(
            @PathVariable String flightId,
            @RequestBody List<String> seatNumbers
    ) {
        service.bookSeats(flightId, seatNumbers);
        return ResponseEntity.ok().build();
    }
    


}
