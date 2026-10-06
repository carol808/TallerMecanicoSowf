package com.taller.gestion.controller;
import com.taller.gestion.dto.*;
import com.taller.gestion.facade.ClientFacade;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
/** API REST de clientes; privilegios finos comprobados en cada operación sensible. */
@RestController @RequestMapping("/api/clients") public class ClientController {
  private final ClientFacade clients; public ClientController(ClientFacade clients){this.clients=clients;}
  @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<ClientDetailResponse> create(@Valid @RequestPart("client") ClientUpsertRequest r,@RequestPart("photo") MultipartFile photo){return ResponseEntity.status(HttpStatus.CREATED).body(clients.register(r,photo));}
  @GetMapping public Page<ClientDetailResponse> list(@RequestParam Long workshopId,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="asc") String direction){return clients.list(workshopId,page,direction);}
  @PutMapping(value="/{id}",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ClientDetailResponse update(@PathVariable Long id,@Valid @RequestPart("client") ClientUpsertRequest r,@RequestPart(value="photo",required=false) MultipartFile photo){return clients.update(id,r,photo);}
  @PatchMapping("/{id}/workshop") public ClientDetailResponse transfer(@PathVariable Long id,@RequestParam Long workshopId){return clients.transfer(id,workshopId);}
  @PatchMapping("/{id}/suspend") public ClientDetailResponse suspend(@PathVariable Long id,Authentication a){if(a.getAuthorities().stream().noneMatch(x->x.getAuthority().equals("ROLE_ADMINISTRADOR")))throw new org.springframework.security.access.AccessDeniedException("Solo un administrador puede suspender clientes.");return clients.suspend(id);}
}
