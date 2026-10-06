package com.taller.gestion.domain;

import jakarta.persistence.*;

/** Taller que delimita la información operativa de sus clientes. */
@Entity
@Table(name = "workshops", uniqueConstraints = {
  @UniqueConstraint(name = "uk_workshop_rfc", columnNames = "rfc"),
  @UniqueConstraint(name = "uk_workshop_email", columnNames = "email")
})
public class Workshop {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable = false, length = 120) private String name;
  @Column(nullable = false, length = 180) private String street;
  @Column(nullable = false, length = 100) private String neighborhood;
  @Column(nullable = false, length = 100) private String municipality;
  @Column(nullable = false, length = 100) private String state;
  @Column(nullable = false, length = 5) private String postalCode;
  @Column(nullable = false, length = 160) private String businessName;
  @Column(nullable = false, length = 20) private String phone;
  @Column(nullable = false, length = 13) private String rfc;
  @Column(nullable = false, length = 160) private String email;
  @Lob @Basic(fetch = FetchType.LAZY) @Column(columnDefinition = "MEDIUMBLOB") private byte[] photo;
  private String photoContentType;
  public Long getId(){ return id; } public String getName(){ return name; } public void setName(String v){name=v;}
  public String getStreet(){return street;} public void setStreet(String v){street=v;} public String getNeighborhood(){return neighborhood;} public void setNeighborhood(String v){neighborhood=v;}
  public String getMunicipality(){return municipality;} public void setMunicipality(String v){municipality=v;} public String getState(){return state;} public void setState(String v){state=v;}
  public String getPostalCode(){return postalCode;} public void setPostalCode(String v){postalCode=v;} public String getBusinessName(){return businessName;} public void setBusinessName(String v){businessName=v;}
  public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getRfc(){return rfc;} public void setRfc(String v){rfc=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
  public byte[] getPhoto(){return photo;} public void setPhoto(byte[] v){photo=v;} public String getPhotoContentType(){return photoContentType;} public void setPhotoContentType(String v){photoContentType=v;}
}
