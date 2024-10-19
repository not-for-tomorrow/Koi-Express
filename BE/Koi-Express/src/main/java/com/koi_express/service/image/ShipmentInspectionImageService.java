package com.koi_express.service.image;

import java.io.File;
import java.io.IOException;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.image.ShipmentInspectionImage;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.Shipments;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.ShipmentInspectionImageRepository;
import com.koi_express.repository.ShipmentsRepository;
import com.koi_express.service.verification.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShipmentInspectionImageService {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentInspectionImageService.class);
    private final S3Service s3Service;
    private final ShipmentInspectionImageRepository imageRepository;
    private final ShipmentsRepository shipmentsRepository;
    private final OrderRepository ordersRepository;

    public ShipmentInspectionImageService(
            S3Service s3Service,
            ShipmentInspectionImageRepository imageRepository,
            ShipmentsRepository shipmentsRepository,
            OrderRepository ordersRepository) {
        this.s3Service = s3Service;
        this.imageRepository = imageRepository;
        this.shipmentsRepository = shipmentsRepository;
        this.ordersRepository = ordersRepository;
    }

    public ApiResponse<String> uploadInspectionImage(Long orderId, MultipartFile multipartFile) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        File tempFile = null;
        try {
            tempFile = convertMultipartFileToFile(multipartFile);

            String imageUrl = s3Service.uploadFile(
                    order.getCustomer().getId().toString(),
                    order.getCreatedAt().toString(),
                    "inspection_images",
                    tempFile);

            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }

            return new ApiResponse<>(HttpStatus.OK.value(), "Image uploaded successfully", imageUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

    private void saveImageUrl(Shipments shipment, String imageUrl) {
        ShipmentInspectionImage inspectionImage = ShipmentInspectionImage.builder()
                .shipment(shipment)
                .inspectionImageUrl(imageUrl)
                .build();

        imageRepository.save(inspectionImage);
        logger.info("Image URL saved for shipment ID {}: {}", shipment.getShipmentId(), imageUrl);
    }
}
