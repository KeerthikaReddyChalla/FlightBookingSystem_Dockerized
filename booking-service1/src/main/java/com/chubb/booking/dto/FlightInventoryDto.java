package com.chubb.booking.dto;

import java.time.LocalDateTime;

public class FlightInventoryDto {
    private String id;
    private String airlineName;
    private LocalDateTime departure;
    private LocalDateTime arrival;
    private Double price;
    private Integer availableSeats;
    // getters & setters
    public String getId() {return id;}
    public void setId(String id) {this.id=id;}
    public String getAirlineName() {return airlineName;}
    public void setAirlineName(String airlineName) {this.airlineName = airlineName;}
    public LocalDateTime getDeparture() {return departure;}
    public void setDeparture(LocalDateTime departure) {this.departure = departure;}
    public LocalDateTime getArrival() {return arrival;}
    public void setArrival(LocalDateTime arrival) {this.arrival = arrival;}
    public Double getPrice() {return price;}
    public void setPrice(Double price) {this.price = price;}
    public Integer getAvailableSeats() {return availableSeats;}
    public void setAvailableSeats(Integer availableSeats) {this.availableSeats = availableSeats;}
}
