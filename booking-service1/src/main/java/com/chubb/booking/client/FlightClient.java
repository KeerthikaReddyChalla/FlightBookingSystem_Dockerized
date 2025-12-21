package com.chubb.booking.client;

import com.chubb.booking.dto.FlightInventoryDto;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flight-service")
public interface FlightClient {

    @GetMapping("/api/flight/inventory/{id}")
    FlightInventoryDto getById(@PathVariable("id") String id);
    
    @PutMapping("/api/flight/{id}/seats/decrease/{count}")
    void decreaseSeats(
        @PathVariable("id") String flightId,
        @PathVariable("count") int count
    );


    @PutMapping("/api/flight/{id}/seats/increase/{count}")
    void increaseSeats(
        @PathVariable("id") String flightId,
        @PathVariable("count") int count
    );
    @PutMapping("/api/flight/airline/inventory/{flightId}/lock-seats")
    void lockSeats(
        @PathVariable String flightId,
        @RequestBody List<String> seatNumbers
    );
    @PutMapping("/api/flight/airline/inventory/{flightId}/seats/unbook")
    void unbookSeats(
            @PathVariable String flightId,
            @RequestBody List<String> seatNumbers
    );


}
