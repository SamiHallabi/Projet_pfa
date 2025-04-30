
package com.theater.reservation.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rowNumberr;
    private int seatNumber;
    private boolean available;

//    @ManyToOne
//    @JoinColumn(name = "show_id")
//    private Show show;

    @ManyToOne
    @JoinColumn(name = "show_id")
    @JsonBackReference
    @JsonIgnore
    private Show show;


    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    // Default constructor
    public Seat() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRowNumber() {
        return rowNumberr;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumberr = rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}