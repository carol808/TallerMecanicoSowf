package com.taller.gestion.dto;

import com.taller.gestion.domain.Client;
import java.time.LocalDate;

/** Representación sin fotografía binaria para listados y edición. */
public record ClientDetailResponse(Long id, String firstName, String paternalLastName, String maternalLastName,
  String curp, String rfc, LocalDate birthDate, Integer age, String email, String personalPhone,
  String contactPhone, String workPhone, String status, Long workshopId, AddressResponse address) {
  public static ClientDetailResponse from(Client c) {
    var a=c.getAddress(); return new ClientDetailResponse(c.getId(), c.getFirstName(), c.getPaternalLastName(), c.getMaternalLastName(), c.getCurp(), c.getRfc(), c.getBirthDate(), c.getAge(), c.getEmail(), c.getPersonalPhone(), c.getAlternateContactName(), c.getWorkPhone(), c.getStatus().name(), c.getWorkshopId(), new AddressResponse(a.getStreet(),a.getNeighborhood(),a.getMunicipality(),a.getState(),a.getPostalCode()));
  }
  public record AddressResponse(String street,String neighborhood,String municipality,String state,String postalCode) {}
}
