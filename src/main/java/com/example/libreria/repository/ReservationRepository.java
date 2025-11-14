package com.example.libreria.repository;

import com.example.libreria.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // Buscar reservas por ID de usuario
    List<Reservation> findByUserId(Long userId);
    
    // Buscar reservas por estado
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    
    // Buscar reservas vencidas (activas con fecha de devolución esperada pasada)
    @Query("SELECT r FROM Reservation r WHERE r.status = 'ACTIVE' AND r.expectedReturnDate < CURRENT_DATE")
    List<Reservation> findOverdueReservations();
}

