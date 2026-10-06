package com.taller.gestion.facade;

import com.taller.gestion.dto.*;
import org.springframework.stereotype.Service;
import java.util.*;

/** Catálogo postal inicial para Hidalgo y las seis entidades con las que colinda. Datos normalizados para formularios, basado en SEPOMEX/INEGI. */
@Service
public class PostalCatalogService {
  private static final Map<String, PostalLookupResponse> CATALOG=Map.of(
    "42000", new PostalLookupResponse("42000","hidalgo","pachuca de soto",List.of("centro")),
    "76000", new PostalLookupResponse("76000","querétaro","querétaro",List.of("centro")),
    "78000", new PostalLookupResponse("78000","san luis potosí","san luis potosí",List.of("centro")),
    "91000", new PostalLookupResponse("91000","veracruz","xalapa",List.of("centro")),
    "72000", new PostalLookupResponse("72000","puebla","puebla",List.of("centro")),
    "90000", new PostalLookupResponse("90000","tlaxcala","tlaxcala",List.of("centro")),
    "50000", new PostalLookupResponse("50000","méxico","toluca",List.of("centro"))
  );
  /** Busca estado, municipio y colonias conocidas por código postal. */
  public PostalLookupResponse lookup(String postalCode){return Optional.ofNullable(CATALOG.get(postalCode)).orElseThrow(()->new NoSuchElementException("Código postal fuera del catálogo regional inicial."));}
  /** Obtiene el código postal cuando la combinación territorial se encuentra en el catálogo. */
  public PostalCodeResponse reverse(String state,String municipality,String neighborhood){return new PostalCodeResponse(CATALOG.values().stream().filter(x->same(x.state(),state)&&same(x.municipality(),municipality)&&x.neighborhoods().stream().anyMatch(n->same(n,neighborhood))).findFirst().orElseThrow(()->new NoSuchElementException("No existe código postal para esa dirección en el catálogo regional inicial.")).postalCode());}
  /** Verifica una dirección completa en ambos sentidos. */
  public static boolean isAllowed(String state,String municipality,String neighborhood,String postalCode){PostalLookupResponse x=CATALOG.get(postalCode);return x!=null&&same(x.state(),state)&&same(x.municipality(),municipality)&&x.neighborhoods().stream().anyMatch(n->same(n,neighborhood));}
  private static boolean same(String a,String b){return a!=null&&b!=null&&a.trim().equalsIgnoreCase(b.trim());}
}
