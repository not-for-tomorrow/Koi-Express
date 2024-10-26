package com.koi_express.service.verification;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

import com.koi_express.exception.S3UploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    public String uploadFile(String customerId, String orderDate, String category, File file) {
        try {
            LocalDate date = LocalDate.parse(orderDate);
            String year = String.valueOf(date.getYear());
            String month = String.format("%02d", date.getMonthValue());
            String day = String.format("%02d", date.getDayOfMonth());

            String fileName = UUID.randomUUID() + "_" + file.getName();
            String keyName =
                    String.format("customer/%s/%s/%s/%s/%s/%s", customerId, year, month, day, category, fileName);

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder().bucket(bucketName).key(keyName).build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

            return s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketName).key(keyName))
                    .toExternalForm();

        } catch (S3Exception e) {
            String errorMsg = String.format("Failed to upload file '%s' for customer '%s' on '%s' in category '%s'. S3 Error: %s",
                    file.getName(), customerId, orderDate, category, e.awsErrorDetails().errorMessage());
            logger.severe(errorMsg);
            throw new S3UploadException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = String.format("Unexpected error while uploading file '%s' for customer '%s' on '%s' in category '%s'.",
                    file.getName(), customerId, orderDate, category);
            logger.severe(errorMsg);
            throw new S3UploadException(errorMsg, e);
        }
    }
}
