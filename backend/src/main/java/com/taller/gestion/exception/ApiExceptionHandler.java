package com.taller.gestion.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.security.access.AccessDeniedException;

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
  @ExceptionHandler(MissingServletRequestPartException.class)
  ResponseEntity<Map<String, String>> missingPart(MissingServletRequestPartException e) {
    return ResponseEntity.badRequest().body(Map.of("message", "Falta la parte multipart requerida: " + e.getRequestPartName()));
  }
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  ResponseEntity<Map<String, String>> fileTooLarge(MaxUploadSizeExceededException e) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of("message", "La fotografía excede el límite de 15 MB."));
  }
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  ResponseEntity<Map<String, String>> unsupportedMedia(HttpMediaTypeNotSupportedException e) {
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of("message", "La solicitud debe enviarse como multipart/form-data."));
  }
  /** Evita respuestas 500 sin contexto ante restricciones de integridad de MySQL. */
  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<Map<String, String>> integrity(DataIntegrityViolationException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "No fue posible guardar el cliente porque uno de sus datos ya existe o excede el límite permitido."));
  }
  @ExceptionHandler(NoSuchElementException.class)
  ResponseEntity<Map<String, String>> notFound(NoSuchElementException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
  }
  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<Map<String, String>> forbidden(AccessDeniedException e) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
  }
}
