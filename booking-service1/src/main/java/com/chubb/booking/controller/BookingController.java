package com.chubb.booking.controller;

import com.chubb.booking.dto.BookingRequest;
import com.chubb.booking.model.Booking;
import com.chubb.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // POST /api/booking/flight/{flightid}
    @PostMapping("/flight/{flightid}")
    public ResponseEntity<Booking> bookTicket(
            @PathVariable("flightid") String flightid,
            @RequestBody @Valid BookingRequest request,
            Principal principal
    ) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userEmail = principal.getName(); // in our setup username==email for simplicity
        Booking saved = bookingService.book(flightid, request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/booking/ticket/{pnr}
    @GetMapping("/ticket/{pnr}")
    public ResponseEntity<Booking> getTicket(@PathVariable String pnr) {
        Booking b = bookingService.getByPnr(pnr);
        return ResponseEntity.ok(b);
    }

    // GET /api/booking/history/{email}
    @GetMapping("/history/{email}")
    public ResponseEntity<List<Booking>> history(@PathVariable String email, Principal principal) {
        // allow only same user or admin check could be added (here assume same)
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        List<Booking> list = bookingService.history(email);
        return ResponseEntity.ok(list);
    }

    // DELETE /api/booking/cancel/{pnr}
    @DeleteMapping("/cancel/{pnr}")
    public ResponseEntity<Void> cancel(@PathVariable String pnr, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        bookingService.cancel(pnr);
        return ResponseEntity.noContent().build();
    }
}
