package com.taller.gestion.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/** Convierte errores de validación y duplicidad en respuestas REST claras para Vue. */
@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(DuplicateClientException.class)
  ResponseEntity<Map<String, String>> duplicate(DuplicateClientException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, String>> invalid(MethodArgumentNotValidException e) {
    String message = e.getBindingResult().getFieldErrors().stream().findFirst().map(x -> x.getField() + ": formato inválido").orElse("Datos inválidos");
    return ResponseEntity.badRequest().body(Map.of("message", message));
  }
  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
  }
}
