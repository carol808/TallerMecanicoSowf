package com.taller.gestion.repository;
import com.taller.gestion.domain.*; import org.springframework.data.jpa.repository.JpaRepository; import java.time.*;
public interface WorkOrderRepository extends JpaRepository<WorkOrder,Long>{ long countByReceivedAtBetween(LocalDateTime start, LocalDateTime end); long countByStatus(OrderStatus status); }
