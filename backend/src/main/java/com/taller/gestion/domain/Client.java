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
  /** MEDIUMBLOB admite fotografías de hasta 16 MB; el caso de uso limita el archivo a 15 MB. */
  @Lob @Basic(fetch = FetchType.LAZY) @Column(nullable = false, columnDefinition = "MEDIUMBLOB") private byte[] photo;
  @Column(nullable = false, length = 40) private String photoContentType;
  @Embedded private Address address;
  /** Reservado para fase de sucursales; no se recibe ni se asigna en esta fase. */
  private Long workshopId;
  @Column(length = 80) private String firstName;
  @Column(length = 80) private String paternalLastName;
  @Column(length = 80) private String maternalLastName;
  @Column(unique = true, length = 18) private String curp;
  @Column(length = 13) private String rfc;
  @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private ClientStatus status = ClientStatus.ACTIVO;

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
  public Long getWorkshopId() { return workshopId; }
  public void setWorkshopId(Long v) { workshopId = v; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String v) { firstName = v; }
  public String getPaternalLastName() { return paternalLastName; }
  public void setPaternalLastName(String v) { paternalLastName = v; }
  public String getMaternalLastName() { return maternalLastName; }
  public void setMaternalLastName(String v) { maternalLastName = v; }
  public String getCurp() { return curp; }
  public void setCurp(String v) { curp = v; }
  public String getRfc() { return rfc; }
  public void setRfc(String v) { rfc = v; }
  public ClientStatus getStatus() { return status; }
  public void setStatus(ClientStatus v) { status = v; }
}
