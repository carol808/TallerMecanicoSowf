package com.taller.gestion.repository;

import com.taller.gestion.domain.Client;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

/** Repository JPA para persistencia y detección transversal de duplicados de clientes. */
public interface ClientRepository extends JpaRepository<Client, Long> {
  @Query("select (count(c) > 0) from Client c where lower(c.email) = lower(:value) or lower(coalesce(c.workEmail, '')) = lower(:value)")
  boolean existsByAnyEmail(@Param("value") String value);

  @Query("select (count(c) > 0) from Client c where c.personalPhone = :value or coalesce(c.workPhone, '') = :value")
  boolean existsByAnyPhone(@Param("value") String value);
}
