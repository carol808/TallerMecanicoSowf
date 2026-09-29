package com.taller.gestion.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Cliente del taller y sus datos de contacto. workshopId es reservado para la futura relación con sucursales. */
@Entity
@Table(name = "clients")
public class Client {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(nullable = false, length = 120) private String fullName;
  @Column(length = 120) private String alternateContactName;
  @Column(nullable = false) private Integer age;
  @Column(nullable = false) private LocalDate birthDate;
  @Column(nullable = false, unique = true, length = 20) private String personalPhone;
  @Column(unique = true, length = 20) private String workPhone;
  @Column(nullable = false, unique = true, length = 160) private String email;
  @Column(unique = true, length = 160) private String workEmail;
  @Lob @Basic(fetch = FetchType.LAZY) @Column(nullable = false) private byte[] photo;
  @Column(nullable = false, length = 40) private String photoContentType;
  @Embedded private Address address;
  /** Reservado para fase de sucursales; no se recibe ni se asigna en esta fase. */
  private Long workshopId;

  public Long getId() { return id; }
  public String getFullName() { return fullName; }
  public void setFullName(String v) { fullName = v; }
  public String getAlternateContactName() { return alternateContactName; }
  public void setAlternateContactName(String v) { alternateContactName = v; }
  public Integer getAge() { return age; }
  public void setAge(Integer v) { age = v; }
  public LocalDate getBirthDate() { return birthDate; }
  public void setBirthDate(LocalDate v) { birthDate = v; }
  public String getPersonalPhone() { return personalPhone; }
  public void setPersonalPhone(String v) { personalPhone = v; }
  public String getWorkPhone() { return workPhone; }
  public void setWorkPhone(String v) { workPhone = v; }
  public String getEmail() { return email; }
  public void setEmail(String v) { email = v; }
  public String getWorkEmail() { return workEmail; }
  public void setWorkEmail(String v) { workEmail = v; }
  public byte[] getPhoto() { return photo; }
  public void setPhoto(byte[] v) { photo = v; }
  public String getPhotoContentType() { return photoContentType; }
  public void setPhotoContentType(String v) { photoContentType = v; }
  public Address getAddress() { return address; }
  public void setAddress(Address v) { address = v; }
}
