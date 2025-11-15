package com.brunozambiazi.bookingsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookingSystemApplication {

	private static final Logger log = LoggerFactory.getLogger(BookingSystemApplication.class);

	public static void main(String[] args) {
		log.info(" --- Starting Booking System application");
		SpringApplication.run(BookingSystemApplication.class, args);
	}

}
