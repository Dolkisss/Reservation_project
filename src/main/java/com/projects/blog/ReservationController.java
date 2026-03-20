package com.projects.blog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/res")
public class ReservationController {

    public static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @GetMapping()
    public ResponseEntity<List<Reservation>> getAllReservations() {
        log.info("Called getAllReservations");
        return ResponseEntity.status(HttpStatus.OK)
                .body(reservationService.findAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
    ) {
        log.info("Called getReservationById by id = " + id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(reservationService.getReservationById(id));
    }

//    @GetMapping("/push/{id}")
//    public ResponseEntity<Reservation> pushReservationById(
//            @PathVariable("id") Long id
//    ) {
//        log.info("Called getReservationById by id = " + id);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(reservationService.pushReservationById(id));
//    }

    @PostMapping("/post")
    public ResponseEntity<Reservation> createReservation (
            @RequestBody Reservation reservationToCreate
    ) {
        log.info("Called createReservation");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(
                        reservationToCreate
                ));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable("id") Long id
    ) {
        log.info("Called deleteReservation by id" + id);
        reservationService.cancelReservation(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation (
            @PathVariable("id") Long id,
            @RequestBody Reservation reservationToUpdate
    ) {
        log.info("Called updateReservation by id " + id);
        var updated = reservationService.updateReservation(id, reservationToUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(updated);

    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation (
            @PathVariable("id") Long id
    ) {
        log.info("Called approveReservation by id = " + id);
        var approved = reservationService.approveReservation(id);
        return ResponseEntity.status(HttpStatus.OK).body(approved);
    }

}
