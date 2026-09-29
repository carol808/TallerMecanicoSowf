package com.taller.gestion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** Datos JSON del formulario multipart de alta de cliente. La fotografía viaja como otra parte del request. */
public record ClientRegistrationRequest(
  @NotBlank @Pattern(regexp = "^[\\p{L} .'-]{3,120}$") String fullName,
  @Pattern(regexp = "^$|^[\\p{L} .'-]{3,120}$") String alternateContactName,
  @NotNull @Min(0) @Max(120) Integer age,
  @NotNull @Past LocalDate birthDate,
  @NotBlank @Pattern(regexp = "^[0-9+() -]{7,20}$") String personalPhone,
  @Pattern(regexp = "^$|^[0-9+() -]{7,20}$") String workPhone,
  @NotBlank @Email @Size(max = 160) String email,
  @Email @Size(max = 160) String workEmail,
  @NotNull @Valid AddressRequest address
) {
  /** Dirección obligatoria del cliente. */
  public record AddressRequest(
    @NotBlank @Size(max = 180) String street,
    @NotBlank @Size(max = 100) String neighborhood,
    @NotBlank @Size(max = 100) String municipality,
    @NotBlank @Size(max = 100) String state,
    @NotBlank @Pattern(regexp = "^[0-9]{5}$") String postalCode
  ) {}
}
