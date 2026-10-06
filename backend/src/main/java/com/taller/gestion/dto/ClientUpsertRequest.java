package com.taller.gestion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** Datos normalizados del cliente enviados como parte JSON de una solicitud multipart. */
public record ClientUpsertRequest(
  @NotBlank @Pattern(regexp = "^[\\p{L} .'-]{2,80}$") String firstName,
  @NotBlank @Pattern(regexp = "^[\\p{L} .'-]{2,80}$") String paternalLastName,
  @Pattern(regexp = "^$|^[\\p{L} .'-]{2,80}$") String maternalLastName,
  @NotBlank @Pattern(regexp = "^[A-ZÑ&]{4}[0-9]{6}[HM][A-Z]{5}[A-Z0-9][0-9]$") String curp,
  @Pattern(regexp = "^$|^[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}$") String rfc,
  @NotNull @Past LocalDate birthDate,
  @NotNull @Min(0) @Max(120) Integer age,
  @NotBlank @Email @Size(max=160) String email,
  @NotBlank @Pattern(regexp = "^[0-9+() -]{7,20}$") String personalPhone,
  @Pattern(regexp = "^$|^[0-9+() -]{7,20}$") String contactPhone,
  @Pattern(regexp = "^$|^[0-9+() -]{7,20}$") String workPhone,
  @NotNull @Valid AddressRequest address,
  @NotNull @Positive Long workshopId
) {
  /** Dirección postal autorizada para el catálogo regional. */
  public record AddressRequest(
    @NotBlank @Size(max=180) String street,
    @NotBlank @Size(max=100) String neighborhood,
    @NotBlank @Size(max=100) String municipality,
    @NotBlank @Size(max=100) String state,
    @NotBlank @Pattern(regexp="^[0-9]{5}$") String postalCode
  ) {}
}
