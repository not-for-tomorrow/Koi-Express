package com.koi_express.service.image;

import com.koi_express.entity.image.ShipmentInspectionImage;
import com.koi_express.entity.shipment.Shipments;
import com.koi_express.repository.ShipmentInspectionImageRepository;
import com.koi_express.repository.ShipmentsRepository;
import org.springframework.stereotype.Service;

@Service
public class ShipmentInspectionImageService {

    private final ShipmentInspectionImageRepository imageRepository;
    private final ShipmentsRepository shipmentsRepository;

    public ShipmentInspectionImageService(ShipmentInspectionImageRepository imageRepository, ShipmentsRepository shipmentsRepository) {
        this.imageRepository = imageRepository;
        this.shipmentsRepository = shipmentsRepository;
    }

    public void saveImageUrl(Long shipmentId, String imageUrl) {
        // Find the shipment by ID
        Shipments shipment = shipmentsRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        // Save the image
        imageRepository.save(ShipmentInspectionImage.builder()
                .shipment(shipment)
                .inspectionImageUrl(imageUrl)
                .build());
    }
}
