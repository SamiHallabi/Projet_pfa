package com.theater.reservation.repository;

import com.theater.reservation.model.Seat;
import com.theater.reservation.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShow(Show show);
    List<Seat> findByShowAndAvailable(Show show, boolean available);
}