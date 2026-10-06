package com.taller.gestion.controller;
import com.taller.gestion.dto.*;
import com.taller.gestion.facade.WorkshopFacade;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
/** API REST de talleres, limitada por SecurityConfig a administradores. */
@RestController @RequestMapping("/api/workshops") public class WorkshopController {
  private final WorkshopFacade workshops; public WorkshopController(WorkshopFacade workshops){this.workshops=workshops;}
  @GetMapping public List<WorkshopResponse> list(){return workshops.list();}
  @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<WorkshopResponse> create(@Valid @RequestPart("workshop") WorkshopRequest request,@RequestPart("photo") MultipartFile photo){return ResponseEntity.status(HttpStatus.CREATED).body(workshops.register(request,photo));}
}
