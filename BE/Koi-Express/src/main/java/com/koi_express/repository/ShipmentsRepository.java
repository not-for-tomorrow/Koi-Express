package com.koi_express.repository;

import com.koi_express.entity.shipment.Shipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentsRepository extends JpaRepository<Shipments, Long> {
}
