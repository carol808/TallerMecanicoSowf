package com.taller.gestion.dto;

/** Respuesta mínima después de una alta; evita devolver la fotografía binaria en el flujo de registro. */
public record ClientResponse(Long id, String fullName, String email, String message) {}
