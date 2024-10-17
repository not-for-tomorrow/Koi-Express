package com.koi_express.repository;

import com.koi_express.entity.image.ShipmentInspectionImage;
import com.koi_express.entity.shipment.Shipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentInspectionImageRepository extends JpaRepository<ShipmentInspectionImage, Long> {

}
