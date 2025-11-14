package com.example.libreria.controller;

import com.example.libreria.dto.BookResponseDTO;
import com.example.libreria.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Libros", description = "API para la gestión de inventario de libros")
public class BookController {
    
    private final BookService bookService;
    
    @Operation(summary = "Sincronizar libros", description = "Sincroniza el catálogo de libros desde la API externa. DEBE ejecutarse primero antes de cualquier operación.")
    @ApiResponse(responseCode = "200", description = "Libros sincronizados exitosamente")
    @PostMapping("/sync")
    public ResponseEntity<String> syncBooks() {
        bookService.syncBooksFromExternalApi();
        return ResponseEntity.ok("Libros sincronizados exitosamente desde la API externa");
    }
    
    @Operation(summary = "Obtener todos los libros", description = "Retorna el catálogo completo de libros con información de stock")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> getAllBooks() {
        List<BookResponseDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    
    @Operation(summary = "Obtener libro por ID", description = "Retorna la información de un libro específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Libro encontrado"),
        @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    @GetMapping("/{externalId}")
    public ResponseEntity<BookResponseDTO> getBookByExternalId(
            @Parameter(description = "ID externo del libro", required = true, example = "258027") @PathVariable Long externalId) {
        BookResponseDTO book = bookService.getBookByExternalId(externalId);
        return ResponseEntity.ok(book);
    }
    
    @Operation(summary = "Actualizar stock", description = "Actualiza la cantidad de stock disponible de un libro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
        @ApiResponse(responseCode = "400", description = "Stock inválido (menor a cantidad reservada)")
    })
    @PutMapping("/{externalId}/stock")
    public ResponseEntity<BookResponseDTO> updateStock(
            @Parameter(description = "ID externo del libro", required = true, example = "258027") @PathVariable Long externalId,
            @Parameter(description = "Nueva cantidad de stock", required = true, example = "20") @RequestParam Integer stockQuantity) {
        BookResponseDTO book = bookService.updateStock(externalId, stockQuantity);
        return ResponseEntity.ok(book);
    }
}

