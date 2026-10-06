package com.taller.gestion.facade;

import com.taller.gestion.domain.*;
import com.taller.gestion.dto.*;
import com.taller.gestion.exception.DuplicateClientException;
import com.taller.gestion.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.*;
import java.util.*;

/** Fachada de clientes: valida reglas de negocio y aísla datos por taller. */
@Service
public class ClientFacade {
  static final long MAX_PHOTO_BYTES=15L*1024*1024;
  static final Set<String> PHOTO_TYPES=Set.of("image/jpeg","image/png","image/webp");
  private final ClientRepository clients; private final WorkshopRepository workshops;
  public ClientFacade(ClientRepository clients, WorkshopRepository workshops){this.clients=clients;this.workshops=workshops;}
  /** Registra al cliente solo si su taller existe, sus identificadores son únicos y su foto es válida. */
  @Transactional public ClientDetailResponse register(ClientUpsertRequest request, MultipartFile photo){
    validatePhoto(photo,true); validateRequest(request); validateDuplicates(request,null); Client client=new Client(); apply(client,request); readPhoto(client,photo); return ClientDetailResponse.from(clients.save(client));
  }
  /** Edita un cliente en su mismo taller sin permitir duplicar campos de otro registro. */
  @Transactional public ClientDetailResponse update(Long id, ClientUpsertRequest request, MultipartFile photo){
    Client client=clients.findById(id).orElseThrow(()->new NoSuchElementException("Cliente no encontrado.")); validateRequest(request); validateDuplicates(request,id); apply(client,request); if(photo!=null&&!photo.isEmpty()){validatePhoto(photo,false);readPhoto(client,photo);} return ClientDetailResponse.from(clients.save(client));
  }
  /** Lista paginada del servidor, exclusivamente para un taller solicitado. */
  @Transactional(readOnly=true) public Page<ClientDetailResponse> list(Long workshopId,int page,String direction){
    requireWorkshop(workshopId); Sort sort=Sort.by("fullName"); sort="desc".equalsIgnoreCase(direction)?sort.descending():sort.ascending(); return clients.findByWorkshopId(workshopId,PageRequest.of(Math.max(0,page),10,sort)).map(ClientDetailResponse::from);
  }
  /** Mueve la propiedad operativa del cliente a otro taller existente. */
  @Transactional public ClientDetailResponse transfer(Long id,Long workshopId){Client c=clients.findById(id).orElseThrow(()->new NoSuchElementException("Cliente no encontrado."));requireWorkshop(workshopId);c.setWorkshopId(workshopId);return ClientDetailResponse.from(clients.save(c));}
  /** Cambia a suspendido sin borrar la evidencia del cliente. */
  @Transactional public ClientDetailResponse suspend(Long id){Client c=clients.findById(id).orElseThrow(()->new NoSuchElementException("Cliente no encontrado."));c.setStatus(ClientStatus.SUSPENDIDO);return ClientDetailResponse.from(clients.save(c));}
  private void validateRequest(ClientUpsertRequest r){
    if(Period.between(r.birthDate(),LocalDate.now()).getYears()!=r.age()) throw new IllegalArgumentException("La edad no coincide con la fecha de nacimiento.");
    requireWorkshop(r.workshopId()); if(!PostalCatalogService.isAllowed(r.address().state(),r.address().municipality(),r.address().neighborhood(),r.address().postalCode())) throw new IllegalArgumentException("La dirección no coincide con el catálogo postal regional.");
  }
  private void validateDuplicates(ClientUpsertRequest r,Long currentId){
    boolean curp=currentId==null?clients.existsByCurpIgnoreCase(normalize(r.curp())):clients.existsByCurpIgnoreCaseAndIdNot(normalize(r.curp()),currentId);
    boolean phone=currentId==null?clients.existsByAnyPhone(phone(r.personalPhone())):clients.existsByAnyPhoneAndIdNot(phone(r.personalPhone()),currentId);
    boolean mail=currentId==null?clients.existsByAnyEmail(email(r.email())):clients.existsByAnyEmailAndIdNot(email(r.email()),currentId);
    if(curp) throw new DuplicateClientException("CURP"); if(phone)throw new DuplicateClientException("teléfono celular"); if(mail)throw new DuplicateClientException("correo electrónico");
  }
  private void apply(Client c,ClientUpsertRequest r){
    c.setFirstName(normalize(r.firstName())); c.setPaternalLastName(normalize(r.paternalLastName()));c.setMaternalLastName(blank(r.maternalLastName()));c.setFullName(String.join(" ",List.of(normalize(r.firstName()),normalize(r.paternalLastName()),Optional.ofNullable(blank(r.maternalLastName())).orElse(""))).trim());
    c.setCurp(normalize(r.curp()).toUpperCase(Locale.ROOT));c.setRfc(blank(r.rfc())==null?null:normalize(r.rfc()).toUpperCase(Locale.ROOT));c.setBirthDate(r.birthDate());c.setAge(r.age());c.setEmail(email(r.email()));c.setPersonalPhone(phone(r.personalPhone()));c.setAlternateContactName(phone(r.contactPhone()));c.setWorkPhone(phone(r.workPhone()));c.setWorkshopId(r.workshopId());
    Address a=new Address();a.setStreet(normalize(r.address().street()));a.setNeighborhood(normalize(r.address().neighborhood()));a.setMunicipality(normalize(r.address().municipality()));a.setState(normalize(r.address().state()));a.setPostalCode(r.address().postalCode());c.setAddress(a);
  }
  private void requireWorkshop(Long id){if(!workshops.existsById(id))throw new IllegalArgumentException("El taller seleccionado no existe.");}
  static void validatePhoto(MultipartFile photo,boolean required){if(photo==null||photo.isEmpty()){if(required)throw new IllegalArgumentException("La fotografía es obligatoria.");return;}if(photo.getSize()>MAX_PHOTO_BYTES)throw new IllegalArgumentException("La fotografía excede el límite de 15 MB.");if(!PHOTO_TYPES.contains(photo.getContentType()))throw new IllegalArgumentException("Formato de fotografía no permitido. Usa JPG, PNG o WEBP.");}
  private void readPhoto(Client c,MultipartFile p){try{c.setPhoto(p.getBytes());c.setPhotoContentType(p.getContentType());}catch(IOException e){throw new IllegalArgumentException("No fue posible leer la fotografía.");}}
  private String normalize(String v){return v.trim().toLowerCase(Locale.ROOT);} private String blank(String v){return v==null||v.isBlank()?null:normalize(v);} private String email(String v){return normalize(v);} private String phone(String v){String x=blank(v);return x==null?null:x.replaceAll("[^0-9+]","");}
}
