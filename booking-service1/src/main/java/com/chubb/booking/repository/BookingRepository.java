package com.chubb.booking.repository;

import com.chubb.booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByEmail(String email);
    Booking findByPnr(String pnr);
}
