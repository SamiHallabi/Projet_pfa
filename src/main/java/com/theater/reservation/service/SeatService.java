package com.theater.reservation.service;

import com.theater.reservation.model.Seat;
import com.theater.reservation.model.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.theater.reservation.repository.SeatRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public List<Seat> createSeatsForShow(Show show, int rows, int seatsPerRow) {
        // Create a grid of seats for the show
        for (int row = 1; row <= rows; row++) {
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                Seat seat = new Seat();
                seat.setRowNumber(row);
                seat.setSeatNumber(seatNum);
                seat.setAvailable(true);
                seat.setShow(show);
                seatRepository.save(seat);
            }
        }
        return getSeatsForShow(show);
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    public Seat updateSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    public List<Seat> getSeatsForShow(Show show) {
        return seatRepository.findByShow(show);
    }

    public List<Seat> getAvailableSeatsForShow(Show show) {
        return seatRepository.findByShowAndAvailable(show, true);
    }

    public void updateSeatAvailability(Long seatId, boolean available) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isPresent()) {
            Seat seat = optionalSeat.get();
            seat.setAvailable(available);
            seatRepository.save(seat);
        }
    }

    public List<Seat> getSelectedSeat(List<Long> seatIds) {
       List<Optional<Seat>> selectedSeatOpt = seatIds.stream().map(seatRepository::findById).toList();
        return selectedSeatOpt.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Seat::isAvailable)
                .collect(java.util.stream.Collectors.toList());
    }
}