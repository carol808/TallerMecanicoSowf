package com.taller.gestion.domain;

import jakarta.persistence.Embeddable;

/** Dirección postal embebida en el cliente; queda preparada para asociarse a una sucursal en otra fase. */
@Embeddable
public class Address {
  private String street;
  private String neighborhood;
  private String municipality;
  private String state;
  private String postalCode;

  public String getStreet() { return street; }
  public void setStreet(String street) { this.street = street; }
  public String getNeighborhood() { return neighborhood; }
  public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
  public String getMunicipality() { return municipality; }
  public void setMunicipality(String municipality) { this.municipality = municipality; }
  public String getState() { return state; }
  public void setState(String state) { this.state = state; }
  public String getPostalCode() { return postalCode; }
  public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
}
