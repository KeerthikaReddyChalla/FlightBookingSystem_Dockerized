package com.chubb.booking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String pnr;
    private String flightId;
    private String name;
    private String email;
    private int seats;
    private List<Passenger> passengers;
    private String meal;
    private List<String> seatNumbers;
    private LocalDateTime bookingTime;
    private LocalDateTime flightDeparture;
    private boolean cancelled;

    // getters & setters
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getPnr() {return pnr;}
    public void setPnr(String pnr) {this.pnr = pnr;}
    public String getFlightId() {return flightId;}
    public void setFlightId(String flightId) {this.flightId = flightId;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public int getSeats() {return seats;}
    public void setSeats(int seats) {this.seats = seats;}
    public List<Passenger> getPassengers() {return passengers;}
    public void setPassengers(List<Passenger> passengers) {this.passengers = passengers;}
    public String getMeal() {return meal;}
    public void setMeal(String meal) {this.meal = meal;}
    public List<String> getSeatNumbers() {return seatNumbers;}
    public void setSeatNumbers(List<String> seatNumbers) {this.seatNumbers = seatNumbers;}
    public LocalDateTime getBookingTime() {return bookingTime;}
    public void setBookingTime(LocalDateTime bookingTime) {this.bookingTime = bookingTime;}
    public LocalDateTime getFlightDeparture() {return flightDeparture;}
    public void setFlightDeparture(LocalDateTime flightDeparture) {this.flightDeparture = flightDeparture;}
    public boolean isCancelled() {return cancelled;}
    public void setCancelled(boolean cancelled) {this.cancelled = cancelled;}
}
