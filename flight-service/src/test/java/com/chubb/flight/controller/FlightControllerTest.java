package com.chubb.flight.controller;

import com.chubb.flight.config.TestSecurityConfig;
import com.chubb.flight.exception.FlightNotFoundException;
import com.chubb.flight.model.FlightInventory;
import com.chubb.flight.service.FlightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
@Import(TestSecurityConfig.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddInventory() throws Exception {
        FlightInventory inv = new FlightInventory();
        inv.setId("1");
        inv.setAirlineName("IndiGo");
        inv.setFromPlace("HYD");
        inv.setToPlace("DEL");
        inv.setDeparture(LocalDateTime.now());
        inv.setArrival(LocalDateTime.now().plusHours(2));
        inv.setPrice(4500.0);
        inv.setAvailableSeats(100);
        inv.setOneWay(true);

        Mockito.when(flightService.addInventory(any())).thenReturn(inv);

        mockMvc.perform(post("/api/flight/airline/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inv)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.airlineName").value("IndiGo"));
    }

    @Test
    void testSearch() throws Exception {
        Mockito.when(flightService.search(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/flight/search")
                        .param("from", "HYD")
                        .param("to", "DEL")
                        .param("start", "2025-01-01T10:00:00")
                        .param("end", "2025-01-02T10:00:00"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        FlightInventory inv = new FlightInventory();
        inv.setId("1");
        inv.setAirlineName("IndiGo");

        Mockito.when(flightService.getById("1")).thenReturn(inv);

        mockMvc.perform(get("/api/flight/inventory/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.airlineName").value("IndiGo"));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        Mockito.when(flightService.getById("999"))
                .thenThrow(new FlightNotFoundException("Flight not found with ID: 999"));

        mockMvc.perform(get("/api/flight/inventory/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Flight not found with ID: 999"));
    }
}
