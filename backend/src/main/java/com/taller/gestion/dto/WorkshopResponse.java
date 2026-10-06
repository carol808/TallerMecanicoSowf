package com.taller.gestion.dto;
import com.taller.gestion.domain.Workshop;
/** Datos de taller seguros para selectores y listados. */
public record WorkshopResponse(Long id,String name,String businessName,String rfc,String email) {
  public static WorkshopResponse from(Workshop w){return new WorkshopResponse(w.getId(),w.getName(),w.getBusinessName(),w.getRfc(),w.getEmail());}
}
