package com.theater.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.theater.reservation", "websocket"})

//@EntityScan(basePackages = {"model"})
public class TheaterReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheaterReservationApplication.class, args);
    }
}
