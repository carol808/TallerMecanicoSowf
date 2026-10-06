package com.taller.gestion.repository;

import com.taller.gestion.domain.Client;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Repository JPA para persistencia y detección transversal de duplicados de clientes. */
public interface ClientRepository extends JpaRepository<Client, Long> {
  @Query("select (count(c) > 0) from Client c where lower(c.email) = lower(:value) or lower(coalesce(c.workEmail, '')) = lower(:value)")
  boolean existsByAnyEmail(@Param("value") String value);

  @Query("select (count(c) > 0) from Client c where c.personalPhone = :value or coalesce(c.workPhone, '') = :value")
  boolean existsByAnyPhone(@Param("value") String value);

  boolean existsByCurpIgnoreCase(String curp);
  @Query("select (count(c) > 0) from Client c where lower(c.curp) = lower(:curp) and c.id <> :id")
  boolean existsByCurpIgnoreCaseAndIdNot(@Param("curp") String curp, @Param("id") Long id);
  @Query("select (count(c) > 0) from Client c where (c.personalPhone = :value or coalesce(c.workPhone, '') = :value) and c.id <> :id")
  boolean existsByAnyPhoneAndIdNot(@Param("value") String value, @Param("id") Long id);
  @Query("select (count(c) > 0) from Client c where (lower(c.email) = lower(:value) or lower(coalesce(c.workEmail, '')) = lower(:value)) and c.id <> :id")
  boolean existsByAnyEmailAndIdNot(@Param("value") String value, @Param("id") Long id);
  Page<Client> findByWorkshopId(Long workshopId, Pageable pageable);
}
