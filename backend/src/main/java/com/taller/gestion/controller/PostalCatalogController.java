package com.taller.gestion.controller;
import com.taller.gestion.dto.*;
import com.taller.gestion.facade.PostalCatalogService;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;
/** API de consulta de direcciones permitidas por el registro. */
@RestController @RequestMapping("/api/postal-codes") public class PostalCatalogController {
  private final PostalCatalogService catalog; public PostalCatalogController(PostalCatalogService catalog){this.catalog=catalog;}
  @GetMapping("/{postalCode}") public PostalLookupResponse lookup(@PathVariable @Pattern(regexp="[0-9]{5}") String postalCode){return catalog.lookup(postalCode);}
  @GetMapping public PostalCodeResponse reverse(@RequestParam String state,@RequestParam String municipality,@RequestParam String neighborhood){return catalog.reverse(state,municipality,neighborhood);}
}
