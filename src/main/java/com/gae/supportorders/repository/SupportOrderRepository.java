package com.gae.supportorders.repository;

import com.gae.supportorders.domain.OrderStatus;
import com.gae.supportorders.domain.SupportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportOrderRepository extends JpaRepository<SupportOrder, Long> {

    @Query("SELECT o FROM SupportOrder o WHERE o.externalReference = :externalReference " +
           "AND o.status IN (com.gae.supportorders.domain.OrderStatus.OPEN, " +
           "com.gae.supportorders.domain.OrderStatus.IN_PROGRESS)")
    Optional<SupportOrder> findActiveByExternalReference(@Param("externalReference") String externalReference);

    @Query("SELECT o FROM SupportOrder o WHERE (:status IS NULL OR o.status = :status)")
    List<SupportOrder> findAllByOptionalStatus(@Param("status") OrderStatus status);
}