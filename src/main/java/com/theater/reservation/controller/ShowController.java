package com.theater.reservation.controller;

import com.theater.reservation.model.Show;
import com.theater.reservation.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shows")
@CrossOrigin(origins = "http://localhost:3000")
public class ShowController {

    private final ShowService showService;

    @Autowired
    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @PostMapping
    public ResponseEntity<Show> createShow(@RequestBody Show show) {
        Show savedShow = showService.createShow(show);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShow);
    }

    @GetMapping
    public ResponseEntity<List<Show>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShowById(@PathVariable(required = false) Long id) {
        Optional<Show> show = showService.getShowById(id);
        return show.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value= "/upcoming", produces = "application/json")
    public ResponseEntity<List<Show>> getUpcomingShows() {
        return ResponseEntity.ok(showService.getUpcomingShows());
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Show>> getShowsByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(showService.getShowsByGenre(genre));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Show>> searchShowsByTitle(@RequestParam String title) {
        return ResponseEntity.ok(showService.searchShowsByTitle(title));
    }

    @GetMapping("/price")
    public ResponseEntity<List<Show>> getShowsByMaxPrice(@RequestParam double maxPrice) {
        return ResponseEntity.ok(showService.getShowsByMaxPrice(maxPrice));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShow(@PathVariable Long id, @RequestBody Show show) {
        Optional<Show> existingShow = showService.getShowById(id);
        if (existingShow.isPresent()) {
            show.setId(id);
            Show updatedShow = showService.updateShow(show);
            return ResponseEntity.ok(updatedShow);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShow(@PathVariable Long id) {
        Optional<Show> existingShow = showService.getShowById(id);
        if (existingShow.isPresent()) {
            showService.deleteShow(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}