
package com.theater.reservation.controller;



// or correct the import if the package is different

import com.theater.reservation.model.Reservation;
import com.theater.reservation.model.Seat;
import com.theater.reservation.model.Show;
import com.theater.reservation.model.User;
import com.theater.reservation.service.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/reservations")
//@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final ShowService showService;
    private final PdfService pdfService;
    private final SeatService seatService;
    @Autowired
    public ReservationController(
            ReservationService reservationService,
            UserService userService,
            ShowService showService,
            PdfService pdfService,
            SeatService seatService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.showService = showService;
        this.pdfService = pdfService;
        this.seatService = seatService;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestBody Map<String, Object> reservationRequest) {
        try {
            Long userId = Long.valueOf(reservationRequest.get("userId").toString());
            Long showId = Long.valueOf(reservationRequest.get("showId").toString());

            List<?> rawSeatIds = (List<?>) reservationRequest.get("seatIds");
            List<Long> seatIds = rawSeatIds.stream()
                    .map(Object::toString)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());

            Optional<User> userOptional = userService.getUserById(userId);
            Optional<Show> showOptional = showService.getShowById(showId);
            List<Seat> confirmedSeats = new ArrayList<>();
            if(showOptional.isPresent()){
                 confirmedSeats = seatService.getSelectedSeat(seatIds);
            }

            if (userOptional.isPresent() && showOptional.isPresent() && seatIds != null && !seatIds.isEmpty() && !confirmedSeats.isEmpty()) {
                Reservation reservation = new Reservation();
                reservation.setUser(userOptional.get());
                reservation.setShow(showOptional.get());
                reservation.setConfirmed(true); // Auto-confirm for simplicity
                reservation.setSeats(confirmedSeats);
                Reservation savedReservation = reservationService.createReservation(reservation, seatIds);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
            } else {
                return ResponseEntity.badRequest().body("Invalid user, show, or seats");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating reservation: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        return reservation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getReservationByCode(@PathVariable String code) {
        Optional<Reservation> reservation = reservationService.getReservationByCode(code);
        return reservation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReservationsForUser(@PathVariable Long userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isPresent()) {
            List<Reservation> reservations = reservationService.getReservationsForUser(userOptional.get());
            return ResponseEntity.ok(reservations);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/show/{showId}")
    public ResponseEntity<?> getReservationsForShow(@PathVariable Long showId) {
        Optional<Show> showOptional = showService.getShowById(showId);
        if (showOptional.isPresent()) {
            List<Reservation> reservations = reservationService.getReservationsForShow(showOptional.get());
            return ResponseEntity.ok(reservations);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cancel/{code}")
    public ResponseEntity<?> cancelReservation(@PathVariable String code) {
        Optional<Reservation> reservationOptional = reservationService.getReservationByCode(code);
        if (reservationOptional.isPresent()) {
            reservationService.cancelReservation(code);
            return ResponseEntity.ok().body("Reservation cancelled successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/invoice/{code}")
    public ResponseEntity<?> generateInvoice(@PathVariable String code) {
        Optional<Reservation> reservationOptional = reservationService.getReservationByCode(code);
        if (reservationOptional.isPresent()) {
            byte[] pdfContent = pdfService.generateInvoice(reservationOptional.get());

            // Mark invoice as generated
            reservationService.markInvoiceAsGenerated(code);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "invoice-" + code + ".pdf");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        Optional<Reservation> existingReservation = reservationService.getReservationById(id);
        if (existingReservation.isPresent()) {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}