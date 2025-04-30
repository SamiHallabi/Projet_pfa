
package com.theater.reservation.controller;


import com.theater.reservation.model.Seat;
import com.theater.reservation.model.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.theater.reservation.service.SeatService;
import com.theater.reservation.service.ShowService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seats")
@CrossOrigin
public class SeatController {

    private final SeatService seatService;
    private final ShowService showService;

    @Autowired
    public SeatController(SeatService seatService, ShowService showService) {
        this.seatService = seatService;
        this.showService = showService;
    }

    @PostMapping
    public ResponseEntity<Seat> createSeat(@RequestBody Seat seat) {
        Seat savedSeat = seatService.createSeat(seat);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSeat);
    }

    @PostMapping("/create-for-show/{showId}")
    public ResponseEntity<?> createSeatsForShow(
            @PathVariable Long showId,
            @RequestParam int rows,
            @RequestParam int seatsPerRow) {
        Optional<Show> showOptional = showService.getShowById(showId);
        if (showOptional.isPresent()) {
            List<Seat> seats = seatService.createSeatsForShow(showOptional.get(), rows, seatsPerRow);
            return ResponseEntity.status(HttpStatus.CREATED).body(seats);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Seat>> getAllSeats() {
        return ResponseEntity.ok(seatService.getAllSeats());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSeatById(@PathVariable Long id) {
        Optional<Seat> seat = seatService.getSeatById(id);
        return seat.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/show/{showId}")
    public ResponseEntity<?> getSeatsForShow(@PathVariable Long showId) {
        Optional<Show> showOptional = showService.getShowById(showId);
        if (showOptional.isPresent()) {
            List<Seat> seats = seatService.getSeatsForShow(showOptional.get());
            return ResponseEntity.ok(seats);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/show/{showId}/available")
    public ResponseEntity<?> getAvailableSeatsForShow(@PathVariable Long showId) {
        Optional<Show> showOptional = showService.getShowById(showId);
        if (showOptional.isPresent()) {
            List<Seat> availableSeats = seatService.getAvailableSeatsForShow(showOptional.get());
            return ResponseEntity.ok(availableSeats);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSeat(@PathVariable Long id, @RequestBody Seat seat) {
        Optional<Seat> existingSeat = seatService.getSeatById(id);
        if (existingSeat.isPresent()) {
            seat.setId(id);
            Seat updatedSeat = seatService.updateSeat(seat);
            return ResponseEntity.ok(updatedSeat);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<?> updateSeatAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        Optional<Seat> existingSeat = seatService.getSeatById(id);
        if (existingSeat.isPresent()) {
            seatService.updateSeatAvailability(id, available);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeat(@PathVariable Long id) {
        Optional<Seat> existingSeat = seatService.getSeatById(id);
        if (existingSeat.isPresent()) {
            seatService.deleteSeat(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}