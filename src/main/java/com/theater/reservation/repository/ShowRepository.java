package com.theater.reservation.repository;

import com.theater.reservation.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByDateAfter(LocalDateTime date);
    List<Show> findByGenre(String genre);
    List<Show> findByTitleContaining(String title);
    List<Show> findByPriceLessThanEqual(double price);
}