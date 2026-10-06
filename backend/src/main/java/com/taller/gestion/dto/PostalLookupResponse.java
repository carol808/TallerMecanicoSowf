package com.taller.gestion.dto;
import java.util.List;
/** Respuesta del catálogo postal regional. */
public record PostalLookupResponse(String postalCode,String state,String municipality,List<String> neighborhoods) {}
