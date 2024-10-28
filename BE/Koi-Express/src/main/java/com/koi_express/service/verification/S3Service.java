package com.koi_express.service.verification;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

import com.koi_express.exception.S3UploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3Service {

    private static final Logger logger = Logger.getLogger(S3Service.class.getName());
    private final S3Client s3Client;

    @Value("${spring.aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(String category, String date, String folder, MultipartFile file) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            String year = String.valueOf(localDate.getYear());
            String month = String.format("%02d", localDate.getMonthValue());
            String day = String.format("%02d", localDate.getDayOfMonth());

            String keyName = String.format("%s/%s/%s/%s/%s/%s", category, year, month, day, folder, UUID.randomUUID() + "_" + file.getOriginalFilename());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(keyName)).toExternalForm();

        } catch (Exception e) {
            throw new S3UploadException("Error uploading file", e);
        }
    }

}
