package com.projects.blog;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReservationService {

    public static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    public final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getReservationById(
            Long id
    ) {
        ReservationEntity foundedReservationByID = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found reservation with id = " + id
                ));

        return toReservation(foundedReservationByID);
    }

    public List<Reservation> findAllReservations() {
        List<ReservationEntity> foundedReservations = repository.findAll();

        return foundedReservations.stream()
                .map(this::toReservation)
                .toList();
    }

//    public Reservation pushReservationById(Long id) { // для добавления через Get (эксперим.)
//        try {
//            var pushedReservation = repository.save(
//                    new ReservationEntity(
//                            id,
//                            (500L + id),
//                            (50L + id),
//                            LocalDate.now(),
//                            LocalDate.now().plusDays(id),
//                            ReservationStatus.PENDING
//                    ));
//            return toReservation(pushedReservation);
//        } catch (Exception e) {
//            log.info("Called Exception: " + e);
//            throw e;
//        }
//    }

    public Reservation createReservation(
            Reservation reservationToCreate
    ) {
        if (reservationToCreate.id() != null) {
            throw new IllegalArgumentException("ID should be empty!");
        }
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty!");
        }

        var createdReservation = new ReservationEntity(
                null,
                reservationToCreate.userId(),
                reservationToCreate.roomId(),
                reservationToCreate.startDate(),
                reservationToCreate.endDate(),
                ReservationStatus.PENDING
        );

        var savedReservation = repository.save(createdReservation);

        return toReservation(savedReservation);
    }

    @Transactional
    public void cancelReservation(
            Long id
    ) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Not found reservation with id =" + id);
        }
        repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Called cancelReservation by id = " + id);
    }

    public Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found reservation with id = " + id
                ));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Status should be PENDING for update!");
        }

        var updatedReservation = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.userId(),
                reservationToUpdate.roomId(),
                reservationToUpdate.startDate(),
                reservationToUpdate.endDate(),
                ReservationStatus.PENDING
        );

        var savedReservation = repository.save(updatedReservation);

        return toReservation(savedReservation);
    }

    public Reservation approveReservation(
            Long id
    ) {
        var reservationToApprove = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found reservation with id = " + id
                ));

        if (reservationToApprove.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Status should be PENDING for approve!");
        }

        if (isReservationConflict(reservationToApprove)) {
            throw new IllegalStateException("Conflict of Reservation!");
        }

        reservationToApprove.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationToApprove);

        return toReservation(reservationToApprove);
    }

    private boolean isReservationConflict(
            ReservationEntity reservation
    ) {
        var allReservation = repository.findAll();
        return allReservation.stream()
                .filter(existing -> !existing.getId().equals(reservation.getId())) // Проверяем, что это не текущее бронирование
                .filter(existing -> reservation.getRoomId().equals(existing.getRoomId()))
                .filter(existing -> existing.getStatus().equals(ReservationStatus.APPROVED))
                .anyMatch(existing ->
                        reservation.getStartDate().isBefore(existing.getEndDate()) &&
                                existing.getStartDate().isBefore(reservation.getEndDate())
                );
    }

    private Reservation toReservation (
            ReservationEntity reservationEntity
    ) {
        return new Reservation(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                reservationEntity.getStatus()
        );

    }
}
