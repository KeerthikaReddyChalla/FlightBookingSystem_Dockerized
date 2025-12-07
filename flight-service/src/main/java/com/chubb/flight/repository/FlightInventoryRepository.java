package com.chubb.flight.repository;

import com.chubb.flight.model.FlightInventory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightInventoryRepository
        extends MongoRepository<FlightInventory, String> {

    List<FlightInventory> findByFromPlaceAndToPlaceAndDepartureBetween(
            String fromPlace,
            String toPlace,
            LocalDateTime start,
            LocalDateTime end
    );
}
