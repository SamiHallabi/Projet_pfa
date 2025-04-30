
package com.theater.reservation.service;

import com.theater.reservation.model.Reservation;
import com.theater.reservation.model.Seat;
import com.theater.reservation.model.Show;
import com.theater.reservation.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.theater.reservation.repository.ReservationRepository;
import com.theater.reservation.repository.SeatRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
//    private final com.theater.reservation.service.EmailService emailService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, SeatRepository seatRepository) {
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
//        this.emailService = emailService;
    }

    public Reservation createReservation(Reservation reservation, List<Long> seatIds) {
        // Generate a unique reservation code
        reservation.setReservationCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservation.setReservationDate(LocalDateTime.now());

        Reservation savedReservation = reservationRepository.save(reservation);

        // Update seats to be associated with this reservation
        double totalPrice = 0;
        for (Long seatId : seatIds) {
            Optional<Seat> optionalSeat = seatRepository.findById(seatId);
            if (optionalSeat.isPresent()) {
                Seat seat = optionalSeat.get();
                seat.setAvailable(false);
                seat.setReservation(savedReservation);
                seatRepository.save(seat);

                // Add the price of the show to the total
                totalPrice += reservation.getShow().getPrice();
            }
        }

        // Update the total price
        savedReservation.setTotalPrice(totalPrice);
        reservationRepository.save(savedReservation);

        // Send confirmation email
        sendReservationConfirmation(savedReservation);

        return savedReservation;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Optional<Reservation> getReservationByCode(String code) {
        return reservationRepository.findByReservationCode(code)
                .map(reservation -> (Reservation) reservation);
    }

    public Reservation updateReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        // Release the seats before deleting
        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            for (Seat seat : reservation.getSeats()) {
                seat.setAvailable(true);
                seat.setReservation(null);
                seatRepository.save(seat);
            }
        }

        reservationRepository.deleteById(id);
    }

    public List<Reservation> getReservationsForUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public List<Reservation> getReservationsForShow(Show show) {
        return reservationRepository.findByShow(show);
    }

    public void cancelReservation(String reservationCode) {
        Optional<Reservation> optionalReservation = reservationRepository.findByReservationCode(reservationCode)
                .map(reservation -> (Reservation) reservation);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();

            // Check if cancellation is allowed (24h before show)
            LocalDateTime showTime = reservation.getShow().getDate();
            if (LocalDateTime.now().plusHours(24).isBefore(showTime)) {
                // Release the seats
                for (Seat seat : reservation.getSeats()) {
                    seat.setAvailable(true);
                    seat.setReservation(null);
                    seatRepository.save(seat);
                }

                // Delete the reservation
                reservationRepository.delete(reservation);

                // Notify user
                sendCancellationNotification(reservation);
            }
        }
    }

    public void markReservationAsConfirmed(String reservationCode) {
        Optional<Reservation> optionalReservation = reservationRepository.findByReservationCode(reservationCode);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setConfirmed(true);
            reservationRepository.save(reservation);
        }
    }

    public void markInvoiceAsGenerated(String reservationCode) {
        Optional<Reservation> optionalReservation = reservationRepository.findByReservationCode(reservationCode);
        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setInvoiceGenerated(true);
            reservationRepository.save(reservation);
        }
    }

    private void sendReservationConfirmation(Reservation reservation) {
        String to = reservation.getUser().getEmail();
        String subject = "Theater Reservation Confirmation: " + reservation.getReservationCode();
        String body = "Dear " + reservation.getUser().getFullName() + ",\n\n" +
                "Your reservation for \"" + reservation.getShow().getTitle() + "\" on " +
                reservation.getShow().getDate() + " has been confirmed.\n\n" +
                "Reservation Details:\n" +
                "- Reservation Code: " + reservation.getReservationCode() + "\n" +
                "- Total Seats: " + reservation.getSeats().size() + "\n" +
                "- Total Price: €" + reservation.getTotalPrice() + "\n\n" +
                "You can cancel your reservation up to 24 hours before the show.\n\n" +
                "Thank you for your reservation!\n" +
                "Theater Reservation System";

//        emailService.sendEmail(to, subject, body);
    }

    private void sendCancellationNotification(Reservation reservation) {
        String to = reservation.getUser().getEmail();
        String subject = "Theater Reservation Cancelled: " + reservation.getReservationCode();
        String body = "Dear " + reservation.getUser().getFullName() + ",\n\n" +
                "Your reservation for \"" + reservation.getShow().getTitle() + "\" has been cancelled.\n\n" +
                "Reservation Details:\n" +
                "- Reservation Code: " + reservation.getReservationCode() + "\n" +
                "- Total Refunded: €" + reservation.getTotalPrice() + "\n\n" +
                "If you did not cancel this reservation, please contact us immediately.\n\n" +
                "Theater Reservation System";

//        emailService.sendEmail(to, subject, body);
    }

    public void sendReminderEmails() {
        // Get all confirmed reservations for shows happening in 24 hours
        LocalDateTime targetTime = LocalDateTime.now().plusHours(24);
        List<Reservation> reservationsToRemind = reservationRepository.findAll();

        for (Reservation reservation : reservationsToRemind) {
            Show show = reservation.getShow();
            // Check if the show is approximately 24 hours away
            if (show.getDate().isAfter(targetTime.minusHours(1)) &&
                    show.getDate().isBefore(targetTime.plusHours(1))) {

                // Send reminder email
                String to = reservation.getUser().getEmail();
                String subject = "Reminder: Your Theater Show Tomorrow";
                String body = "Dear " + reservation.getUser().getFullName() + ",\n\n" +
                        "This is a reminder that you have tickets for \"" + show.getTitle() + "\" tomorrow at " +
                        show.getDate() + ".\n\n" +
                        "Reservation Details:\n" +
                        "- Reservation Code: " + reservation.getReservationCode() + "\n" +
                        "- Total Seats: " + reservation.getSeats().size() + "\n\n" +
                        "We look forward to seeing you!\n" +
                        "Theater Reservation System";

//                emailService.sendEmail(to, subject, body);
            }
        }
    }
}