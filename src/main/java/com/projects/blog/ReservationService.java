package com.projects.blog;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReservationService {

    public static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final AtomicLong counter;

    public final Map<Long, Reservation> reservationMap;

    public ReservationService() {
        reservationMap = new HashMap<>();
        counter = new AtomicLong();
    }

    public Reservation getReservationById(
            Long id
    ) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation with id = " + id);
        }
        return reservationMap.get(id);
    }

    public List<Reservation> findAllReservations() {
        return reservationMap.values().stream().toList();
    }

    public Reservation pushReservationById(Long id) {
        try {
            reservationMap.put(
                    id, new Reservation(
                            id,
                            (100L + id),
                            (40L + id),
                            LocalDate.now(),
                            LocalDate.now().plusDays(id),
                            ReservationStatus.APPROVED

                    ));
            return reservationMap.get(id);
        } catch (Exception e) {
            log.info("Called Exception: " + e);
            throw e;
        }
    }

    public Reservation createReservation(
            Reservation reservationToCreate
    ) {
        if (reservationToCreate.id() != null) {
            throw new IllegalArgumentException("ID should be empty!");
        }
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty!");
        }

        var newReservation = new Reservation(
                counter.incrementAndGet(),
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        reservationMap.put(newReservation.id(), newReservation);
        return newReservation;
    }

    public void deleteReservation(
            Long id
    ) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation with id =" + id);
        }
        reservationMap.remove(id);
    }

    public Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation with id =" + id);
        }

        var reservation = reservationMap.get(id);
        if (reservation.status() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Status should be PENDING for update!");
        }

        var updatedReservation = new Reservation(
                reservation.id(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );

        reservationMap.put(reservation.id(), updatedReservation);

        return updatedReservation;
    }

    public Reservation approveReservation(
            Long id
    ) {
        if (!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation with id =" + id);
        }

        var reservation = reservationMap.get(id);
        if (reservation.status() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Status should be PENDING for approve!");
        }

        if (isReservationConflict(reservation)) {
            throw new IllegalStateException("Conflict of Reservation!");
        }
        var approvedReservation = new Reservation(
                reservation.id(),
                reservation.userId(),
                reservation.roomId(),
                reservation.startDate(),
                reservation.endDate(),
                ReservationStatus.APPROVED
        );

        reservationMap.put(reservation.id(), approvedReservation);
        return approvedReservation;
    }

    private boolean isReservationConflict(
            Reservation reservation
    ) {
        return reservationMap.values().stream()
                .filter(existing -> !reservation.id().equals(existing.id()))
                .filter(existing -> reservation.roomId().equals(existing.roomId()))
                .filter(existing -> existing.status().equals(ReservationStatus.APPROVED))
                .anyMatch(existing ->
                        reservation.startDate().isBefore(existing.endDate()) &&
                                existing.startDate().isBefore(reservation.endDate())
                );
    }
}
