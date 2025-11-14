package com.example.libreria.controller;

import com.example.libreria.dto.ReservationRequestDTO;
import com.example.libreria.dto.ReservationResponseDTO;
import com.example.libreria.dto.ReturnBookRequestDTO;
import com.example.libreria.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API para la gestión de reservas de libros con cálculo automático de tarifas")
public class ReservationController {
    
    private final ReservationService reservationService;
    
    @Operation(summary = "Crear una reserva", description = "Crea una nueva reserva de libro. Calcula automáticamente la tarifa total (precio × días)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente. Stock disminuye en 1"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario o libro no encontrado"),
        @ApiResponse(responseCode = "500", description = "No hay copias disponibles del libro")
    })
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO requestDTO) {
        ReservationResponseDTO reservation = reservationService.createReservation(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }
    
    @Operation(summary = "Obtener reserva por ID", description = "Retorna los detalles de una reserva específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservationById(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable Long id) {
        ReservationResponseDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }
    
    @Operation(summary = "Obtener todas las reservas", description = "Retorna todas las reservas del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de reservas obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        List<ReservationResponseDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }
    
    @Operation(summary = "Obtener reservas por usuario", description = "Retorna todas las reservas de un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de reservas del usuario")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByUserId(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long userId) {
        List<ReservationResponseDTO> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }
    
    @Operation(summary = "Obtener reservas activas", description = "Retorna todas las reservas con estado ACTIVE")
    @ApiResponse(responseCode = "200", description = "Lista de reservas activas")
    @GetMapping("/active")
    public ResponseEntity<List<ReservationResponseDTO>> getActiveReservations() {
        List<ReservationResponseDTO> reservations = reservationService.getActiveReservations();
        return ResponseEntity.ok(reservations);
    }
    
    @Operation(summary = "Obtener reservas vencidas", description = "Retorna todas las reservas activas que han pasado su fecha de devolución esperada")
    @ApiResponse(responseCode = "200", description = "Lista de reservas vencidas")
    @GetMapping("/overdue")
    public ResponseEntity<List<ReservationResponseDTO>> getOverdueReservations() {
        List<ReservationResponseDTO> reservations = reservationService.getOverdueReservations();
        return ResponseEntity.ok(reservations);
    }
    
    @Operation(summary = "Devolver un libro", description = "Registra la devolución de un libro. Calcula multa del 15% por día de demora si aplica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Libro devuelto exitosamente. Stock aumenta en 1. Multa = precio × 0.15 × días de demora"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
        @ApiResponse(responseCode = "400", description = "La reserva ya fue devuelta")
    })
    @PostMapping("/{id}/return")
    public ResponseEntity<ReservationResponseDTO> returnBook(
            @Parameter(description = "ID de la reserva", required = true) @PathVariable Long id,
            @Valid @RequestBody ReturnBookRequestDTO returnRequest) {
        ReservationResponseDTO reservation = reservationService.returnBook(id, returnRequest);
        return ResponseEntity.ok(reservation);
    }
}

