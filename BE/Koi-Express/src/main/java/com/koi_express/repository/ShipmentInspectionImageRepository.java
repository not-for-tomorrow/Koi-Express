package com.koi_express.repository;

import com.koi_express.entity.image.ShipmentInspectionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentInspectionImageRepository extends JpaRepository<ShipmentInspectionImage, Long> {}
