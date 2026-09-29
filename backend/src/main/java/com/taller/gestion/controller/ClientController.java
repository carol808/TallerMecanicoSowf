package com.taller.gestion.controller;

import com.taller.gestion.dto.*;
import com.taller.gestion.facade.ClientFacade;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** Adaptador REST del registro de clientes; solo recibe multipart y delega el caso de uso a ClientFacade. */
@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
public class ClientController {
  private final ClientFacade clients;
  public ClientController(ClientFacade clients) { this.clients = clients; }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ClientResponse> create(@Valid @RequestPart("client") ClientRegistrationRequest request, @RequestPart("photo") MultipartFile photo) {
    return ResponseEntity.status(HttpStatus.CREATED).body(clients.register(request, photo));
  }
}
