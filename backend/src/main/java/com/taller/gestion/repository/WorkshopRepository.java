package com.taller.gestion.repository;

import com.taller.gestion.domain.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository JPA de talleres. */
public interface WorkshopRepository extends JpaRepository<Workshop, Long> {
  boolean existsByRfcIgnoreCase(String rfc);
  boolean existsByEmailIgnoreCase(String email);
  boolean existsByNameIgnoreCaseAndPostalCode(String name, String postalCode);
}
