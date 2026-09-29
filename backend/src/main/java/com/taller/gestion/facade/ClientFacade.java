package com.taller.gestion.facade;

import com.taller.gestion.domain.*;
import com.taller.gestion.dto.*;
import com.taller.gestion.exception.DuplicateClientException;
import com.taller.gestion.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.*;
import java.util.*;

/**
 * Fachada del caso de uso Registrar Cliente. Centraliza reglas de formato de archivo,
 * consistencia de edad y prevención de duplicados antes de delegar al Repository.
 */
@Service
public class ClientFacade {
  private static final long MAX_PHOTO_BYTES = 15L * 1024 * 1024;
  private static final Set<String> PHOTO_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
  private final ClientRepository clients;

  public ClientFacade(ClientRepository clients) { this.clients = clients; }

  /** Persiste un cliente solamente cuando todos sus identificadores de contacto son únicos. */
  @Transactional
  public ClientResponse register(ClientRegistrationRequest request, MultipartFile photo) {
    validatePhoto(photo);
    validateAge(request.age(), request.birthDate());
    validateDuplicates(request);
    try {
      Client client = new Client();
      client.setFullName(request.fullName().trim());
      client.setAlternateContactName(blankToNull(request.alternateContactName()));
      client.setAge(request.age());
      client.setBirthDate(request.birthDate());
      client.setPersonalPhone(normalizePhone(request.personalPhone()));
      client.setWorkPhone(normalizePhone(blankToNull(request.workPhone())));
      client.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
      client.setWorkEmail(normalizeEmail(request.workEmail()));
      client.setPhoto(photo.getBytes());
      client.setPhotoContentType(photo.getContentType());
      client.setAddress(toAddress(request.address()));
      Client saved = clients.save(client);
      return new ClientResponse(saved.getId(), saved.getFullName(), saved.getEmail(), "Cliente guardado");
    } catch (IOException e) {
      throw new IllegalArgumentException("No fue posible leer la fotografía.");
    }
  }

  private void validatePhoto(MultipartFile photo) {
    if (photo == null || photo.isEmpty()) throw new IllegalArgumentException("La fotografía es obligatoria.");
    if (photo.getSize() > MAX_PHOTO_BYTES) throw new IllegalArgumentException("La fotografía excede el límite de 15 MB.");
    if (!PHOTO_TYPES.contains(photo.getContentType())) throw new IllegalArgumentException("Formato de fotografía no permitido. Usa JPG, PNG o WEBP.");
    try { if (!hasExpectedSignature(photo.getBytes(), photo.getContentType())) throw new IllegalArgumentException("El contenido de la fotografía no coincide con su formato declarado."); }
    catch (IOException e) { throw new IllegalArgumentException("No fue posible leer la fotografía."); }
  }
  /** Comprueba firmas binarias básicas para evitar aceptar un archivo no gráfico con una extensión simulada. */
  private boolean hasExpectedSignature(byte[] bytes, String contentType) {
    if ("image/jpeg".equals(contentType)) return bytes.length >= 3 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 && (bytes[2] & 0xFF) == 0xFF;
    if ("image/png".equals(contentType)) return bytes.length >= 8 && bytes[0] == (byte) 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47 && bytes[4] == 0x0D && bytes[5] == 0x0A && bytes[6] == 0x1A && bytes[7] == 0x0A;
    return "image/webp".equals(contentType) && bytes.length >= 12 && bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F' && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P';
  }
  private void validateAge(Integer age, LocalDate birthDate) {
    int calculated = Period.between(birthDate, LocalDate.now()).getYears();
    if (calculated != age) throw new IllegalArgumentException("La edad no coincide con la fecha de nacimiento.");
  }
  private void validateDuplicates(ClientRegistrationRequest r) {
    String personalPhone = normalizePhone(r.personalPhone());
    if (clients.existsByAnyPhone(personalPhone)) throw new DuplicateClientException("teléfono personal");
    String workPhone = normalizePhone(blankToNull(r.workPhone()));
    if (workPhone != null && clients.existsByAnyPhone(workPhone)) throw new DuplicateClientException("teléfono de trabajo");
    String email = normalizeEmail(r.email());
    if (clients.existsByAnyEmail(email)) throw new DuplicateClientException("correo electrónico");
    String workEmail = normalizeEmail(r.workEmail());
    if (workEmail != null && clients.existsByAnyEmail(workEmail)) throw new DuplicateClientException("correo electrónico de trabajo");
  }
  private Address toAddress(ClientRegistrationRequest.AddressRequest r) {
    Address address = new Address(); address.setStreet(r.street().trim()); address.setNeighborhood(r.neighborhood().trim()); address.setMunicipality(r.municipality().trim()); address.setState(r.state().trim()); address.setPostalCode(r.postalCode()); return address;
  }
  private String normalizeEmail(String value) { String v = blankToNull(value); return v == null ? null : v.trim().toLowerCase(Locale.ROOT); }
  private String normalizePhone(String value) { String v = blankToNull(value); return v == null ? null : v.replaceAll("[^0-9+]", ""); }
  private String blankToNull(String value) { return value == null || value.isBlank() ? null : value; }
}
