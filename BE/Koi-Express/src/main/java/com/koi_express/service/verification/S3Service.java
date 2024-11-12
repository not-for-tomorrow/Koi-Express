package com.koi_express.service.verification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.koi_express.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.aws.s3.bucket-name}")
    private String bucketName;

    public String uploadImage(String category, String date, String title, MultipartFile imageFile) {
        try {
            LocalDateTime localDateTime;

            if (date.length() == 10) {
                localDateTime =
                        LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            } else {
                localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            LocalDate localDate = localDateTime.toLocalDate();
            String year = String.valueOf(localDate.getYear());
            String month = String.format("%02d", localDate.getMonthValue());
            String day = String.format("%02d", localDate.getDayOfMonth());

            String keyName =
                    String.format("%s/%s/%s/%s/%s/imageFile/%s", category, year, month, day, title, UUID.randomUUID());

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder().bucket(bucketName).key(keyName).build();

            s3Client.putObject(
                    putObjectRequest, RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize()));

            return s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketName).key(keyName))
                    .toExternalForm();

        } catch (Exception e) {
            throw new S3UploadException("Error uploading imageFile", e);
        }
    }
}
