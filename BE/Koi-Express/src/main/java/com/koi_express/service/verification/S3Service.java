package com.koi_express.service.verification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(String customerId, String orderDate, String category, File file) {
        // Create dynamic folder structure
        LocalDate date = LocalDate.parse(orderDate); // Parse the order date
        String year = String.valueOf(date.getYear());
        String month = String.format("%02d", date.getMonthValue());
        String day = String.format("%02d", date.getDayOfMonth());

        // Build S3 folder path: /customer/{customerId}/{year}/{month}/{day}/{category}/{fileName}
        String fileName = UUID.randomUUID().toString() + "_" + file.getName(); // Unique file name to avoid conflicts
        String keyName = String.format("customer/%s/%s/%s/%s/%s/%s", customerId, year, month, day, category, fileName);

        // Create the PutObjectRequest with the dynamic key
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
//                .acl("public-read")  // Set ACL for public access
                .build();

        // Upload file to S3
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

        // Return the public URL of the uploaded file
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(keyName)).toExternalForm();
    }
}
