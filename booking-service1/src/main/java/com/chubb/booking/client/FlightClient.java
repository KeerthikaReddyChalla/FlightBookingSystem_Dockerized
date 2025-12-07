package com.chubb.booking.client;

import com.chubb.booking.dto.FlightInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "flight-service")
public interface FlightClient {

    @GetMapping("/api/flight/inventory/{id}")
    FlightInventoryDto getById(@PathVariable("id") String id);
}
