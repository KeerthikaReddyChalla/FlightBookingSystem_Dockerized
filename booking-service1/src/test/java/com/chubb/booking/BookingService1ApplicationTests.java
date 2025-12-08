package com.chubb.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;



@SpringBootTest(classes = BookingService1Application.class)
@ActiveProfiles("test")
class BookingService1ApplicationTests {

	@Test
	void contextLoads() {
	}

}
