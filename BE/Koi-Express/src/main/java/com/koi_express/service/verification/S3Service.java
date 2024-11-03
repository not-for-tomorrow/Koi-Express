package com.koi_express.service.verification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.aws.s3.bucket-name}")
    private String bucketName;

    public String uploadFile(String category, String date, String title, MultipartFile file, boolean isImage) {
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

            String fileTypeFolder = isImage ? "image" : "file";

            String keyName = String.format(
                    "%s/%s/%s/%s/%s/%s/%s",
                    category, year, month, day, title, fileTypeFolder, UUID.randomUUID(), file.getOriginalFilename());

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder().bucket(bucketName).key(keyName).build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return s3Client.utilities()
                    .getUrl(builder -> builder.bucket(bucketName).key(keyName))
                    .toExternalForm();

        } catch (Exception e) {
            throw new S3UploadException("Error uploading file", e);
        }
    }

    public File downloadFile(String keyName) {
        try {
            String tempFilePath =
                    "/tmp/" + UUID.randomUUID() + "_" + Paths.get(keyName).getFileName();
            File file = new File(tempFilePath);
            Files.createDirectories(file.getParentFile().toPath());

            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder().bucket(bucketName).key(keyName).build();

            s3Client.getObject(getObjectRequest, ResponseTransformer.toFile(Paths.get(tempFilePath)));
            return file;
        } catch (Exception e) {
            throw new S3UploadException("Error downloading file", e);
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }
}
