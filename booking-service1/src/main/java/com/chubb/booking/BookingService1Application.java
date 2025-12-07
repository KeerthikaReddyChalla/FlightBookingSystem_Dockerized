package com.chubb.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BookingService1Application {
    public static void main(String[] args) {
        SpringApplication.run(BookingService1Application.class, args);
    }
}
