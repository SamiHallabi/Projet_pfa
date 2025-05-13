
package com.theater.reservation.service;


import com.theater.reservation.model.Show;
import com.theater.reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.theater.reservation.repository.ShowRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public ShowService(ShowRepository showRepository, ReservationRepository reservationRepository) {
        this.showRepository = showRepository;
        this.reservationRepository = reservationRepository;
    }

    public Show createShow(Show show) {
        return showRepository.save(show);
    }

    public List<Show> getAllShows() {
        var shows= showRepository.findAll();
       return shows.stream().map(show -> {
            show.setReservations(reservationRepository.findByShow(show));
          return show;
           }).toList();

    }

    public Optional<Show> getShowById(Long id) {
        return showRepository.findById(id);
    }

    public Show updateShow(Show show) {
        return showRepository.save(show);
    }

    public void deleteShow(Long id) {
        showRepository.deleteById(id);
    }

    public List<Show> getUpcomingShows() {
        return showRepository.findByDateAfter(LocalDateTime.now());
    }

    public List<Show> getShowsByGenre(String genre) {
        return showRepository.findByGenre(genre);
    }

    public List<Show> searchShowsByTitle(String title) {
        return showRepository.findByTitleContaining(title);
    }

    public List<Show> getShowsByMaxPrice(double maxPrice) {
        return showRepository.findByPriceLessThanEqual(maxPrice);
    }
}