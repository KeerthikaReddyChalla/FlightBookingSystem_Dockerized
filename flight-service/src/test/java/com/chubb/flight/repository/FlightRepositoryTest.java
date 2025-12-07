package com.chubb.flight.repository;

import com.chubb.flight.model.FlightInventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class FlightRepositoryTest {

    @Autowired
    private FlightInventoryRepository repo;

    @Test
    void testSaveAndFind() {
        FlightInventory inv = new FlightInventory();
        inv.setAirlineName("TestAir");
        inv.setFromPlace("HYD");
        inv.setToPlace("BLR");
        inv.setDeparture(LocalDateTime.now());
        inv.setArrival(LocalDateTime.now().plusHours(1));
        inv.setPrice(3000.0);
        inv.setAvailableSeats(50);
        inv.setOneWay(true);

        FlightInventory saved = repo.save(inv);

        List<FlightInventory> list = repo.findByFromPlaceAndToPlaceAndDepartureBetween(
                "HYD",
                "BLR",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        assertFalse(list.isEmpty());
    }
}
