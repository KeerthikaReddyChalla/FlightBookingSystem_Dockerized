package com.chubb.booking.controller;

import com.chubb.booking.dto.BookingRequest;
import com.chubb.booking.model.Booking;
import com.chubb.booking.model.Passenger;
import com.chubb.booking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean BookingService bookingService;
    @Autowired ObjectMapper mapper;

    @Test
    void testBookTicketUnauthorized() throws Exception {
        BookingRequest req = new BookingRequest();
        req.setName("Test");
        req.setEmail("user@example.com");
        req.setSeats(1);
        req.setPassengers(List.of(new Passenger()));
        req.setMeal("Veg");
        req.setSeatNumbers(List.of("1A"));

        mockMvc.perform(post("/api/booking/flight/FL1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTicketNotFound() throws Exception {
        Mockito.when(bookingService.getByPnr("PNR-123")).thenThrow(new RuntimeException("PNR not found"));
        mockMvc.perform(get("/api/booking/ticket/PNR-123"))
                .andExpect(status().isInternalServerError());
    }
}
