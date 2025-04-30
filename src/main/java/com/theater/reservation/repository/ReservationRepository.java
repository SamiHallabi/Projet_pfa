package com.theater.reservation.repository;

import com.theater.reservation.model.Reservation;
import com.theater.reservation.model.Show;
import com.theater.reservation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByShow(Show show);
    Optional<Reservation> findByReservationCode(String reservationCode);
    List<Reservation> findByReservationDateAfter(LocalDateTime date);
    List<Reservation> findByShowAndConfirmed(Show show, boolean confirmed);
}