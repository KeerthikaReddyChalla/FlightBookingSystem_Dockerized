package com.chubb.booking.dto;

import com.chubb.booking.model.Passenger;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class BookingRequest {

    @NotBlank
    private String name;

    @NotBlank @Email
    private String email;

    @Min(1)
    private int seats;

    @NotEmpty
    private List<Passenger> passengers;

    @NotBlank
    private String meal; // "Veg" or "NonVeg"

    @NotEmpty
    private List<String> seatNumbers;

    // getters & setters
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
}
