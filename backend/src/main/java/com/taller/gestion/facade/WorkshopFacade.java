package com.taller.gestion.facade;
import com.taller.gestion.domain.Workshop;
import com.taller.gestion.dto.*;
import com.taller.gestion.repository.WorkshopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
/** Fachada de talleres: concentra validación, normalización y prevención de duplicados. */
@Service public class WorkshopFacade {
  private final WorkshopRepository workshops; public WorkshopFacade(WorkshopRepository workshops){this.workshops=workshops;}
  @Transactional public WorkshopResponse register(WorkshopRequest r,MultipartFile photo){
    String rfc=norm(r.rfc()).toUpperCase(Locale.ROOT), email=norm(r.email());
    if(workshops.existsByRfcIgnoreCase(rfc)||workshops.existsByEmailIgnoreCase(email)||workshops.existsByNameIgnoreCaseAndPostalCode(norm(r.name()),r.postalCode()))throw new IllegalArgumentException("Ya existe un taller con ese RFC, correo o nombre y código postal.");
    if(!PostalCatalogService.isAllowed(r.state(),r.municipality(),r.neighborhood(),r.postalCode()))throw new IllegalArgumentException("La dirección no coincide con el catálogo postal regional.");
    ClientFacade.validatePhoto(photo,true); Workshop w=new Workshop();w.setName(norm(r.name()));w.setStreet(norm(r.street()));w.setNeighborhood(norm(r.neighborhood()));w.setMunicipality(norm(r.municipality()));w.setState(norm(r.state()));w.setPostalCode(r.postalCode());w.setBusinessName(norm(r.businessName()));w.setPhone(r.phone().replaceAll("[^0-9+]",""));w.setRfc(rfc);w.setEmail(email);try{w.setPhoto(photo.getBytes());w.setPhotoContentType(photo.getContentType());}catch(IOException e){throw new IllegalArgumentException("No fue posible leer la fotografía.");}return WorkshopResponse.from(workshops.save(w));
  }
  @Transactional(readOnly=true) public List<WorkshopResponse> list(){return workshops.findAll().stream().map(WorkshopResponse::from).toList();}
  private String norm(String v){return v.trim().toLowerCase(Locale.ROOT);}
}
