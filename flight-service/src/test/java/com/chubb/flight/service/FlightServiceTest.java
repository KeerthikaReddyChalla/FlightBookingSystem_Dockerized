package com.chubb.flight.service;

import com.chubb.flight.exception.FlightNotFoundException;
import com.chubb.flight.model.FlightInventory;
import com.chubb.flight.repository.FlightInventoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class FlightServiceTest {

    private final FlightInventoryRepository repo = Mockito.mock(FlightInventoryRepository.class);
    private final FlightService service = new FlightService(repo);

    @Test
    void testAddInventory() {
        FlightInventory inv = new FlightInventory();
        inv.setId("1");

        Mockito.when(repo.save(any())).thenReturn(inv);

        FlightInventory saved = service.addInventory(inv);

        assertEquals("1", saved.getId());
    }

    @Test
    void testGetById() {
        FlightInventory inv = new FlightInventory();
        inv.setId("1");

        Mockito.when(repo.findById("1")).thenReturn(Optional.of(inv));

        FlightInventory found = service.getById("1");

        assertEquals("1", found.getId());
    }

    @Test
    void testGetByIdNotFound() {
        Mockito.when(repo.findById("999")).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class, () -> {
            service.getById("999");
        });
    }

    @Test
    void testSearch() {
        Mockito.when(repo.findByFromPlaceAndToPlaceAndDepartureBetween(
                any(), any(), any(), any()
        )).thenReturn(java.util.Collections.emptyList());

        var result = service.search(
                "HYD",
                "DEL",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1)
        );

        assertTrue(result.isEmpty());
    }
}
