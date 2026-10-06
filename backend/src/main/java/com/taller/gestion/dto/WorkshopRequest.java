package com.taller.gestion.dto;

import jakarta.validation.constraints.*;

/** Datos administrativos de un taller; la imagen viaja como parte multipart independiente. */
public record WorkshopRequest(
  @NotBlank @Size(max=120) String name,
  @NotBlank @Size(max=180) String street, @NotBlank @Size(max=100) String neighborhood,
  @NotBlank @Size(max=100) String municipality, @NotBlank @Size(max=100) String state,
  @NotBlank @Pattern(regexp="^[0-9]{5}$") String postalCode,
  @NotBlank @Size(max=160) String businessName,
  @NotBlank @Pattern(regexp="^[0-9+() -]{7,20}$") String phone,
  @NotBlank @Pattern(regexp="^[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}$") String rfc,
  @NotBlank @Email @Size(max=160) String email
) {}
